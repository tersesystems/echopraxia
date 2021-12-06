# Echopraxia

[Echopraxia](https://github.com/tersesystems/echopraxia) is a Java logging API that and is designed around structured logging, rich context, and conditional logging.  There is a Logback-based implementation, but Echopraxia's API is completely dependency-free, meaning it can be implemented with Log4J2, JUL, or directly.

Echopraxia is a sequel to the Scala logging API [Blindsight](https://github.com/tersesystems/blindsight), hence the name: "Echopraxia is the involuntary repetition or imitation of an observed action."

Echopraxia is based around three main concepts:

* Structured Arguments (attached to a logging statement)
* Contextual Logging (attached to a logger)
* Conditions (attached to loggers, statement, and enabled checks)

Please see the [blog posts](https://tersesystems.com/category/logging/) for more background.

## Logstash

There is a Logback implementation based around [logstash-logback-encoder](https://github.com/logfellow/logstash-logback-encoder) implementation of [event specific custom fields](https://github.com/logfellow/logstash-logback-encoder#event-specific-custom-fields).

```
MAVEN / GRADLE PATH GOES HERE
```

## Basic Usage

First you get a logger:

```java
import com.tersesystems.echopraxia.*;

Logger<?> basicLogger = LoggerFactory.getLogger(getClass());
```

Logging simple messages and exceptions are done as in SLF4J: 

```java
try {
  ...
  basicLogger.info("Simple message");
} catch (Exception e) {
  basicLogger.error("Error message", e);  
}
```

However, when you log arguments, you pass a function which provides you with a field builder and returns a list of fields:

```java
basicLogger.info("Message name {}", fb -> fb.onlyString("name", "value"));
```

You can log multiple arguments and include the exception if you want the stack trace:

```java
basicLogger.info("Message name {}", fb -> Arrays.asList(
  fb.string("name", "value"),
  fb.exception(e)
));
```

So far so good, but logging strings and numbers can get tedious.  Let's go into custom field builders.  

### Custom Field Builders

Echopraxia lets you specify custom field builders whenever you want to log domain objects:

```java
  public class BuilderWithDate implements Field.Builder {
    public BuilderWithDate() {}

    // Renders a date using the `only` idiom returning a list of `Field`.
    // This is a useful shortcut when you only have one field you want to add.
    public List<Field> onlyDate(String name, Date date) {
      return singletonList(date(name, date));
    }

    // Renders a date as an ISO 8601 string.
    public Field date(String name, Date date) {
      return string(
              name, DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(date.getTime())));
    }
  }
```

And now you can render a date automatically:

```java
Logger<BuilderWithDate> dateLogger = basicLogger.withFieldBuilder(BuilderWithDate.class);
dateLogger.info("Date {}", fb -> fb.onlyDate("creation_date", new Date()));
```

This also applies to more complex objects:

```java
  public class PersonFieldBuilder implements Field.Builder {
    public PersonFieldBuilder() {}
    // Renders a `Person` as an object field.
    // Note that properties must be broken down to the basic JSON types,
    // i.e. a primitive string/number/boolean/null or object/array.
    public Field person(String name, Person person) {
      return object(
              name,
              string("name", person.name()),
              number("age", person.age()),
              array("toys", Field.Value.asList(person.toys(), Field.Value::string)));
    }
  }
```


```java
Person user = ...
Logger<PersonFieldBuilder> personLogger = basicLogger.withFieldBuilder(PersonFieldBuilder.class);
personLogger.info("Person {}", fb -> Arrays.asList(fb.person("user", user)));
```

## Context

You can also add fields directly to the logger using `logger.withFields` for contextual logging:

```java
Logger<?> loggerWithFoo = basicLogger.withFields(fb -> fb.onlyString("foo", "bar"));
loggerWithFoo.info("JSON field will log automatically") // will log "foo": "bar" field in a JSON appender.
```

This works very well for HTTP session and request data such as correlation ids.

Note that in contrast to MDC, logging using context fields will work seamlessly across multiple threads.

## Conditions

Logging conditions can be handled gracefully using `Condition` functions.  A `Condition` will take a `Level` and a `LoggingContext` which will return the fields of the logger.

```java
final Condition mustHaveFoo = (level, context) ->
        context.getFields().stream().anyMatch(field -> field.name().equals("foo"));
```

Conditions should be cheap to evaluate, and should be "safe" - i.e. they should not do things like network calls, database lookups, or rely on locks.

Conditions can be used either on the logger, on the statement, or against the enabled check.

### Logger

You can use conditions in a logger, and statements will only log if the condition is met:

```java
Logger<?> loggerWithCondition = logger.withCondition(condition);
```

You can also build up conditions:

```java
Logger<?> loggerWithAandB = logger.withCondition(conditionA).withCondition(conditionB);
```

### Statement

You can use conditions in an individual statement:

```java
logger.info(mustHaveFoo, "Only log if foo is present");
```

### Enabled

Conditions can also be used in blocks for expensive objects.

```java
if (logger.isInfoEnabled(condition)) {
  // only true if condition and is info  
}
```

## SLF4J API

The SLF4J API are not enabled as part of context.  If you want to use markers specifically, you will need to cast and create loggers by hand, rather than going through `LoggerFactory`.

First, import the `logstash` package:

```java
import com.tersesystems.echopraxia.logstash.*;
```

The `LogstashCoreLogger` has a `withMarkers` method that can be used, and can be passed through:

```java
Logger<?> baseLogger = getLogger();
LogstashCoreLogger core = (LogstashCoreLogger) baseLogger.core();
Logger<?> logger = new Logger<>(core.withMarkers(MarkerFactory.getMarker("SECURITY")), baseLogger.fieldBuilder());
```

Likewise, you need to get at the SLF4J API, you can cast and call `core.logger()`:

```java
Logger<?> baseLogger = getLogger();
LogstashCoreLogger core = (LogstashCoreLogger) baseLogger.core();
org.slf4j.Logger slf4jLogger = core.logger();
```

If you have markers set as context, you can evaluate them in a condition through casting to `LogstashLoggingContext`:

```java
Condition hasAnyMarkers = (level, context) -> {
   LogstashLoggingContext c = (LogstashLoggingContext) context;
   List<org.slf4j.Marker> markers = c.getMarkers();
   return markers.size() > 0;
};
```
