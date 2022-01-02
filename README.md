# Echopraxia

[Echopraxia](https://github.com/tersesystems/echopraxia) is a Java logging API that and is designed around structured logging, rich context, and conditional logging.  There is a Logback-based implementation, but Echopraxia's API is completely dependency-free, meaning it can be implemented with Log4J2, JUL, or directly.

Echopraxia is a sequel to the Scala logging API [Blindsight](https://github.com/tersesystems/blindsight), hence the name: "Echopraxia is the involuntary repetition or imitation of an observed action."

Echopraxia is based around several main concepts that build and leverage on each other:

* Structured Logging (API based around structured fields and values)
* Contextual Logging (API based around building state in loggers)
* Conditions (API based around context-aware functions and dynamic scripting)
* Semantic Logging (API based around typed arguments)
* Fluent Logging (API based around log entry builder)

For a worked example, see this [Spring Boot Project](https://github.com/tersesystems/echopraxia-spring-boot-example).

Although Echopraxia is tied on the backend to an implementation, it is designed to hide implementation details from you, just as SLF4J hides the details of the logging implementation.  For example, `logstash-logback-encoder` provides `Markers` or `StructuredArguments`, but you will not see them in the API.  Instead, Echopraxia works with independent `Field` and `Value` objects that are converted by a `CoreLogger` provided by an implementation.

Please see the [blog posts](https://tersesystems.com/category/logging/) for more background.

## Logstash

There is a Logback implementation based around [logstash-logback-encoder](https://github.com/logfellow/logstash-logback-encoder) implementation of [event specific custom fields](https://github.com/logfellow/logstash-logback-encoder#event-specific-custom-fields).

Maven:

```
<dependency>
  <groupId>com.tersesystems.echopraxia</groupId>
  <artifactId>logstash</artifactId>
  <version>0.0.3</version>
</dependency>
```

Gradle:

```
implementation "com.tersesystems.echopraxia:logstash:0.0.3" 
```

## Basic Usage

For almost all use cases, you will be working with the API which is a single import:

```
import com.tersesystems.echopraxia.*;
```

First you get a logger:

```java
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

## Dynamic Conditions with Scripts

One of the limitations of logging is that it's not that easy to change logging levels in an application at run-time.  In modern applications, you typically have complex inputs and may want to enable logging for some very specific inputs without turning on your logging globally.

Script Conditions lets you tie your conditions to scripts that you can change and re-evaluate at runtime.

The security concerns surrounding Groovy or Javascript make them unsuitable in a logging environment.  Fortunately, Echopraxia provides a [Tweakflow](https://twineworks.github.io/tweakflow) script integration that lets you evaluate logging statements **safely**.

Because Scripting has a dependency on Tweakflow, it is broken out into a distinct library that you must add to your build.

Maven:

```
<dependency>
  <groupId>com.tersesystems.echopraxia</groupId>
  <artifactId>scripting</artifactId>
  <version>0.0.2</version>
</dependency>
```

Gradle:

```
implementation "com.tersesystems.echopraxia:scripting:0.0.2" 
```

### File Based Scripts

Creating a script condition is done with `ScriptCondition.create`:

```java
import com.tersesystems.echopraxia.scripting.*;

Path path = Paths.get("src/test/tweakflow/condition.tf");
Condition condition = ScriptCondition.create(false, path, Throwable::printStackTrace);

Logger<?> logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
```

Where `condition.tf` contains a tweakflow script, e.g.

```tweakflow
import * as std from "std";
alias std.strings as str;

library echopraxia {
  # level: the logging level
  # fields: the dictionary of fields
  function evaluate: (string level, dict fields) ->
    str.lower_case(fields[:person][:name]) == "will";   
}
```

Tweakflow comes with a [VS Code integration](https://marketplace.visualstudio.com/items?itemName=twineworks.tweakflow), a [reference guide](https://twineworks.github.io/tweakflow/reference.html), and a [standard library](https://twineworks.github.io/tweakflow/modules/std.html) that contains useful regular expression and date manipulation logic.

One important thing to note is that creating a script tied to a file will ensure that if the file is touched, the script manager will invalidate the script and recompile it.  This does mean that the condition will check last modified fs metadata on every evaluation, which *should be fine* for most filesystems, but I have not attempted to scale this feature and I vaguely remember something odd happening on Windows NTFS LastModifiedDate.  YMMV.

## String Based Scripts

You also have the option of passing in a string directly, which will never touch last modified date:

```java
Condition c = ScriptCondition.create(false, scriptString, Throwable::printStackTrace);
```

### Custom Source Scripts

You also have the option of creating your own `ScriptHandle` which can be backed by whatever you like, for example you can call out to Consul or a feature flag system for script work:

```groovy
ScriptHandle handle = new ScriptHandle() {
  @Override
  public boolean isInvalid() {
    return callConsulToCheckWeHaveNewest();
  }

  @Override
  public String script() throws IOException {
    return callConsulForScript();
  }
    
  // ...report / path etc 
};
ScriptCondition.create(false, handle);
```

## Semantic Logging

Semantic Loggers are strongly typed, and will only log a particular kind of argument.  All the work of field building and
setting up a message is done from setup.

### Basic Usage

To set up a logger for a `Person` with `name` and `age` properties, you would do the following:

```java
import com.tersesystems.echopraxia.semantic.*;

SemanticLogger<Person> logger =
    SemanticLoggerFactory.getLogger(
        getClass(),
        Person.class,
        person -> "person.name = {}, person.age = {}",
        p -> b -> Arrays.asList(b.string("name", p.name), b.number("age", p.age)));

Person person = new Person("Eloise", 1);
logger.info(person);
```

### Conditions

Semantic loggers take conditions in the same way that other loggers do, either through predicate:

```java
if (logger.isInfoEnabled(condition)) {
  logger.info(person);
}
```

or directly on the method:

```java
logger.info(condition, person);
```

or on the logger:

```java
logger.withCondition(condition).info(person);
```

### Context

Semantic loggers can add fields to context in the same way other loggers do.

```java
SemanticLogger<Person> loggerWithContext =
  logger.withFields(fb -> fb.onlyString("some_context_field", contextValue));
```

### Installation

Semantic Loggers have a dependency on the `api` module, but do not have any implementation dependencies.

Maven:

```
<dependency>
  <groupId>com.tersesystems.echopraxia</groupId>
  <artifactId>semantic</artifactId>
  <version>0.0.3</version>
</dependency>
```

Gradle:

```
implementation "com.tersesystems.echopraxia:semantic:0.0.3" 
```

## Fluent Logging

Fluent logging is done using a `FluentLoggerFactory`.  

It is useful in situations where arguments may need to be built up over time.

```java
import com.tersesystems.echopraxia.fluent.*;

FluentLogger<?> logger = FluentLoggerFactory.getLogger(getClass());

Person person = new Person("Eloise", 1);

logger
    .atInfo()
    .message("name = {}, age = {}")
    .argument(b -> b.string("name", person.name))
    .argument(b -> b.number("age", person.age))
    .log();
```

### Installation

Semantic Loggers have a dependency on the `api` module, but do not have any implementation dependencies.

Maven:

```
<dependency>
  <groupId>com.tersesystems.echopraxia</groupId>
  <artifactId>fluent</artifactId>
  <version>0.0.3</version>
</dependency>
```

Gradle:

```
implementation "com.tersesystems.echopraxia:fluent:0.0.3" 
```

## Core Logger and SLF4J API

The SLF4J API are not enabled as part of context.  If you want to use markers specifically, you will need to use a core logger.

First, import the `logstash` package and the `core` package:

```java
import com.tersesystems.echopraxia.logstash.*;
import com.tersesystems.echopraxia.core.*;
```

This gets you access to the `CoreLogger` and `CoreLoggerFactory`, which is used as a backing logger.

The `LogstashCoreLogger` has a `withMarkers` method that takes an SLF4J marker:

```java
LogstashCoreLogger core = (LogstashCoreLogger) CoreLoggerFactory.getLogger();
Logger<?> logger = LoggerFactory.getLogger(core.withMarkers(MarkerFactory.getMarker("SECURITY")), Field.Builder.instance);
```

Likewise, you need to get at the SLF4J logger from a core logger, you can cast and call `core.logger()`:

```java
Logger<?> baseLogger = LoggerFactory.getLogger();
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
