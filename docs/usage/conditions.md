# Conditions

Logging conditions can be handled gracefully using `Condition` functions.  A `Condition` will take a `Level` and a `LoggingContext` which will return the fields of the logger.

```java
final Condition errorCondition = new Condition() {
  @Override
  public boolean test(Level level, LoggingContext context) {
    return level.equals(Level.ERROR);
  }
};
```

Conditions can be used either on the logger, on the statement, or against the predicate check.

There are two elemental conditions, `Condition.always()` and `Condition.never()`.  Echopraxia has optimizations for conditions; it will treat `Condition.always()` as a no-op, and return a `NeverLogger` that has no operations for logging.  The JVM can recognize that logging has no effect at all, and will [eliminate the method call as dead code](https://shipilev.net/jvm/anatomy-quarks/27-compiler-blackholes/).

Conditions are a great way to manage diagnostic logging in your application with more flexibility than global log levels can provide. Consider enabling setting your application logging to `DEBUG` i.e. `<logger name="your.application.package" level="DEBUG"/>` and using [conditions to turn on and off debugging as needed](https://tersesystems.com/blog/2019/07/22/targeted-diagnostic-logging-in-production/).  Conditions come with `and`, `or`, and `xor` functionality, and can provide more precise and expressive logging criteria than can be managed with filters and markers.  This is particularly useful when combined with [filters](#filters).

For example, if you want to have logging that only activates during business hours, you can use the following:

```java
import echopraxia.logging.api.Condition;

public class MyBusinessConditions {
  private static final Clock officeClock = Clock.system(ZoneId.of("America/Los_Angeles")) ;

  public Condition businessHoursOnly() {
    return Condition.operational().and(weekdays().and(from9to5()));
  }
  
  public Condition weekdays() {
    return (level, context) -> {
      LocalDate now = LocalDate.now(officeClock);
      final DayOfWeek dayOfWeek = now.getDayOfWeek();
      return ! (dayOfWeek.equals(DayOfWeek.SATURDAY) || dayOfWeek.equals(DayOfWeek.SUNDAY));
    };
  }
  
  public Condition from9to5() {
    return (level, context) -> LocalTime.now(officeClock).query(temporal -> {
      // hour is zero based, so adjust for readability
      final int hour = temporal.get(ChronoField.HOUR_OF_DAY) + 1;
      return (hour >= 9) && (hour <= 17); // 8 am to 5 pm
    });
  }
}
```

Matching values is best done using `Value.equals` in conjunction with one of the match methods:

```java
// Better type safety using Value.equals
Condition hasDerp = Condition.stringMatch("herp", v -> Value.equals(v, Value.string("herp")))

// this works too
Condition logins = Condition.numberMatch("logins", v -> v.equals(number(1)));
```

The `context` parameter that is passed in is a `LoggingContext` that contains the argument fields, the fields added directly to the logger, and a reference to the `CoreLogger`, which can return useful context like the logger name.

This is only a part of the available functionality in conditions.  You can tie conditions directly to a backend, such as a database or key/value store, or trigger them to work in response to an exception or unusual metrics.  See the [redis example](https://github.com/tersesystems/echopraxia-examples/tree/main/redis), [jmx example](https://github.com/tersesystems/echopraxia-examples/tree/main/jmx), [metrics example](https://github.com/tersesystems/echopraxia-examples/tree/main/metrics), and [timed diagnostic example](https://github.com/tersesystems/echopraxia-examples/tree/main/timed-diagnostic).

## JSON Path

If you are using the Logstash implementation or have explicitly added the `jsonpath` module, you can use the `JsonPathCondition.pathCondition` method to provide you with an extended context that has logging methods:

This will give you a context that extends `FindPathMethods` that will let you use [JSONPath](https://github.com/json-path/JsonPath#jayway-jsonpath) to find values from the logging context in a condition.

```java
import static echopraxia.logging.api.JsonPathCondition.pathCondition;

Condition fooCondition = pathCondition((level, ctx) -> 
    ctx.findString("$.foo").filter(s -> s.equals("bar")).isPresent()
);
```

Tip: if you are using IntelliJ IDEA, you can add the [@Language("JSONPath")](https://www.jetbrains.com/help/idea/using-language-injections.html#language_annotation) annotation to [inject JSONPATH](https://www.jetbrains.com/idea/guide/tips/evaluate-json-path-expressions/).

The `context.find*` methods take a class as a type, and a [JSON path](https://www.ietf.org/archive/id/draft-ietf-jsonpath-base-03.html), which can be used to search through context fields (or arguments, if the condition is used in a logging statement).

The basic types are `String`, the `Number` subclasses such as `Integer`, and `Boolean`.  If no matching path is found, an empty `Optional` is returned.

```java
Optional<String> optName = context.findString("$.person.name");
```

This also applies to `Throwable` which are usually passed in as arguments:

```java
Optional<Throwable> optThrowable = context.findThrowable();
```

You can treat a `Throwable` as a JSON object, i.e. the following will all work with the default `$.exception` path:

```java
Optional<String> className = ctx.findString("$.exception.className");
Optional<String> message = ctx.findString("$.exception.message");
Optional<Throwable> cause = ctx.findThrowable("$.exception.cause");
```

And you can also query stack trace elements:

```java
Optional<Map<String, ?>> stacktraceElement = ctx.findObject("$.exception.stackTrace[0]")
Optional<String> methodName = ctx.findString("$.exception.stackTrace[0].methodName");
Optional<List<?>> listOfElements = ctx.findObject("$.exception.stackTrace[5..10]")
```

Finding an explicitly null value returns a `boolean`:

```java
// fb.nullValue("keyWithNullValue") sets an explicitly null value
boolean isNull = context.findNull("$.keyWithNullValue");
```

Finding an object will return a `Map`:

```java
Optional<Map<String, ?>> mother = context.findObject("$.person.mother");
```

For a `List`, in the case of an array value or when using indefinite queries:

```java
List<String> interests = context.findList("$.person.mother.interests");
```

You can use [inline predicates](https://github.com/json-path/JsonPath#inline-predicates), which will return a `List` of the results:

```java
final Condition cheapBookCondition = pathCondition(
  (level, context) -> ! context.findList("$.store.book[?(@.price < 10)]").isEmpty());
```

The inline and filter predicates are not available for exceptions. Instead, you must use `filter`:

```java
class FindException {
  void logException() {
    Condition throwableCondition = pathCondition(
      (level, ctx) ->
        ctx.findThrowable()
          .filter(e -> "test message".equals(e.getMessage()))
          .isPresent());
    
    logger.error(throwableCondition, "Error message", new RuntimeException("test message"));
  }
}
```

There are many more options available using JSONPath.  You can try out the [online evaluator](https://jsoning.com/jsonpath/) to test out expressions.

## Logger

You can use conditions in a logger, and statements will only log if the condition is met:

```java
var loggerWithCondition = logger.withCondition(condition);
```

You can also build up conditions:

```java
Logger loggerWithAandB = logger.withCondition(conditionA).withCondition(conditionB);
```

Conditions are only evaluated once a level/marker check is passed, so something like

```java
loggerWithAandB.trace("some message");
```

will short circuit on the level check before any condition is reached.

Conditions look for fields, but those fields can come from *either* context or argument.  For example, the following condition will log because the condition finds an argument field:

```java
Condition cond = pathCondition((level, ctx) -> ctx.findString("somename").isPresent());
logger.withCondition(cond).info("some message",  fb.string("somename", "somevalue")); // matches argument
```

## Statement

You can also use conditions in an individual statement:

```java
logger.info(mustHaveFoo, "Only log if foo is present");
```

## Predicates

Conditions can also be used in predicate blocks for expensive objects.

```java
if (logger.isInfoEnabled(condition)) {
  // only true if condition and is info  
}
```

Conditions will only be checked after an `isEnabled` check is passed -- the level (and optional marker) is always checked first, before any conditions.

A condition may also evaluate context fields that are set in a logger:

```java
// Conditions may evaluate context
Condition cond = pathCondition((level, ctx) -> ctx.findString("somename").isPresent());
boolean loggerEnabled = logger
  .withFields( fb.string("somename", "somevalue"))
  .withCondition(condition)
  .isInfoEnabled();
```

Using a predicate with a condition does not trigger any logging, so it can be a nice way to "dry run" a condition.  Note that the context evaluation takes place every time a condition is run, so doing something like this is not good:

```java
var loggerWithContextAndCondition = logger
  .withFields(fb.string("somename", "somevalue"))
  .withCondition(condition);

// check evaluates context
if (loggerWithContextAndCondition.isInfoEnabled()) {
  // info statement _also_ evaluates context
  loggerWithContextAndCondition.info("some message");
}
```

This results in the context being evaluated both in the block and in the info statement itself, which is inefficient.

It is generally preferable to pass in a condition explicitly on the statement, as it will only evaluate once.

```java
var loggerWithContext = logger.withFields(fb.string("somename", "somevalue"));
loggerWithContext.info(condition, "message");
```

or just on the statement.

```java
loggerWithContextAndCondition.info("some message");
```
