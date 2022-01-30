# Echopraxia

[Echopraxia](https://github.com/tersesystems/echopraxia) is a Java logging API designed around structured logging, rich context, and conditional logging.  There are Logback and Log4J2 implementations, but Echopraxia's API is completely dependency-free, meaning it can be implemented with any logging API, i.e. jboss-logging, JUL, JEP 264, or even directly.

Echopraxia is a sequel to the Scala logging API [Blindsight](https://github.com/tersesystems/blindsight), hence the name: "Echopraxia is the involuntary repetition or imitation of an observed action."

Echopraxia is based around several main concepts that build and leverage on each other:

* Structured Logging (API based around structured fields and values)
* Contextual Logging (API based around building state in loggers)
* Conditions (API based around context-aware functions and dynamic scripting)
* Semantic Logging (API based around typed arguments)
* Fluent Logging (API based around log entry builder)

For a worked example, see this [Spring Boot Project](https://github.com/tersesystems/echopraxia-spring-boot-example).

Although Echopraxia is tied on the backend to an implementation, it is designed to hide implementation details from you, just as SLF4J hides the details of the logging implementation.  For example, `logstash-logback-encoder` provides `Markers` or `StructuredArguments`, but you will not see them in the API.  Instead, Echopraxia works with independent `Field` and `Value` objects that are converted by a `CoreLogger` provided by an implementation.

Please see the [blog posts](https://tersesystems.com/category/logging/) for more background on logging stuff.

## Statement of Intent

**Echopraxia is not a replacement for SLF4J**.  It is not an attempt to compete with Log4J2 API, JUL, commons-logging for the title of "one true logging API" and restart the [logging mess](https://techblog.bozho.net/the-logging-mess/).  SLF4J won that fight [a long time ago](https://www.semanticscholar.org/paper/Studying-the-Use-of-Java-Logging-Utilities-in-the-Chen-Jiang/be39720a72f04c92b9aece9548171d5fa3a627e6).

Echopraxia is a structured logging API.  It is an appropriate solution **when you control the logging implementation** and have decided you're going to do structured logging, e.g. a web application where you've decided to use [logstash-logback-encoder](https://github.com/logfellow/logstash-logback-encoder) already. 

SLF4J is an appropriate solution **when you do not control the logging output**, e.g. in an open-source library that could be used in arbitrary situations by anybody.  

Echopraxia is best described as a specialization or augmentation for application code -- as you're building framework support code for your application and build up your domain objects, you can write custom field builders, then log everywhere in your application with a consistent schema.

## Benchmarks

Benchmarks show [performance inline with straight calls to the implementation](BENCHMARKS.md).  

Please be aware that how fast and how much you can log is [dramatically impacted](https://tersesystems.com/blog/2019/06/03/application-logging-in-java-part-6/) by your use of an asynchronous appender, your available I/O, your storage, and your ability to manage and process logs.  

Logging can be categorized as either diagnostic (DEBUG/TRACE) or operational (INFO/WARN/ERROR).

If you are doing significant diagnostic logging, consider using an appender optimized for fast local logging, such as [Blacklite](https://github.com/tersesystems/blacklite/), and consider writing to `tmpfs`.

If you are doing significant operational logging, you should commit to a budget for operational costs i.e. storage, indexing, centralized logging infrastructure.  It is very likely that you will run up against budget constraints long before you ever need to optimize your logging for greater throughput.

## Logstash

There is a Logback implementation based around [logstash-logback-encoder](https://github.com/logfellow/logstash-logback-encoder) implementation of [event specific custom fields](https://github.com/logfellow/logstash-logback-encoder#event-specific-custom-fields).

Maven:

```
<dependency>
  <groupId>com.tersesystems.echopraxia</groupId>
  <artifactId>logstash</artifactId>
  <version>1.1.3</version>
</dependency>
```

Gradle:

```
implementation "com.tersesystems.echopraxia:logstash:1.1.3" 
```

## Log4J

There is a Log4J implementation that works with the [JSON Template Layout](https://logging.apache.org/log4j/2.x/manual/json-template-layout.html).

Maven:

```
<dependency>
  <groupId>com.tersesystems.echopraxia</groupId>
  <artifactId>log4j</artifactId>
  <version>1.1.3</version>
</dependency>
```

Gradle:

```
implementation "com.tersesystems.echopraxia:log4j:1.1.3" 
```

You will need to integrate the `com.tersesystems.echopraxia.log4j.layout` package into your `log4j2.xml` file, e.g. by using the `packages` attribute, and add an `EventTemplateAdditionalField` element:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN" packages="com.tersesystems.echopraxia.log4j.layout">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT" follow="true">
            <JsonTemplateLayout eventTemplateUri="classpath:LogstashJsonEventLayoutV1.json">
                <EventTemplateAdditionalField
                        key="fields"
                        format="JSON"
                        value='{"$resolver": "echopraxiaFields"}'/>
            </JsonTemplateLayout>
        </Console>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Console" />
        </Root>
    </Loggers>
</Configuration>
```

If you want to separate the context fields from the argument fields, you can define them separately:

```xml
<JsonTemplateLayout eventTemplateUri="classpath:LogstashJsonEventLayoutV1.json">
    <EventTemplateAdditionalField
            key="arguments"
            format="JSON"
            value='{"$resolver": "echopraxiaArgumentFields"}'/>
    <EventTemplateAdditionalField
            key="context"
            format="JSON"
            value='{"$resolver": "echopraxiaContextFields"}'/>
</JsonTemplateLayout>
```

Unfortunately, I don't know of a way to "flatten" fields so that they show up on the root object instead of under an additional field.  If you know how to do this, let me know!

## Basic Usage

For almost all use cases, you will be working with the API which is a single import:

```
import com.tersesystems.echopraxia.*;
```

First you define a logger (usually in a controller or singleton -- `getClass()` is particularly useful for abstract controllers):

```java
final Logger<?> basicLogger = LoggerFactory.getLogger(getClass());
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
basicLogger.info("Message name {} age {}", fb -> fb.list(
  fb.string("name", "value"),
  fb.number("age", 13)
));
```

You can specify a single field using `only`:

```java
basicLogger.info("Message name {}", fb -> fb.only(fb.string("name", "value")));
```

And there are some shortcut methods like `onlyString` that combine `only` and `string`:

```java
basicLogger.info("Message name {}", fb -> fb.onlyString("name", "value"));
```

You can log multiple arguments and include the exception if you want the stack trace:

```java
basicLogger.info("Message name {}", fb -> fb.list(
  fb.string("name", "value"),
  fb.exception(e)
));
```

So far so good. But logging strings and numbers can get tedious.  Let's go into custom field builders.  

### Custom Field Builders

Echopraxia lets you specify custom field builders whenever you want to log domain objects:

```java
public class BuilderWithDate implements Field.Builder {
  public BuilderWithDate() {}

  // Renders a date as an ISO 8601 string.
  public StringValue dateValue(Date date) {
    return Value.string(DateTimeFormatter.ISO_INSTANT.format(date.toInstant()));
  }

  public Field date(String name, Date date) {
    return string(name, dateValue(date));
  }

  // Renders a date using the `only` idiom returning a list of `Field`.
  // This is a useful shortcut when you only have one field you want to add.
  public List<Field> onlyDate(String name, Date date) {
    return only(date(name, date));
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
public class PersonBuilder implements Field.Builder {

  // Renders a `Person` as an object field.
  public Field person(String fieldName, Person p) {
    return keyValue(fieldName, personValue(p));
  }

  public Value<?> personValue(Person p) {
    if (p == null) {
      return Value.nullValue();
    }
    Field name = string("name", p.name());
    Field age = number("age", p.age());
    // optional returns either an object value or null value, keyValue is untyped
    Field father = keyValue("father", Value.optional(p.getFather().map(this::personValue)));
    Field mother = keyValue("mother", Value.optional(p.getMother().map(this::personValue)));
    Field interests = array("interests", p.interests());
    return Value.object(name, age, father, mother, interests);
  }
}
```

And then you can do the same by calling `fb.person`:

```java
Person user = ...
Logger<PersonBuilder> personLogger = basicLogger.withFieldBuilder(PersonBuilder.class);
personLogger.info("Person {}", fb -> fb.only(fb.person("user", user)));
```

If you are using a particular set of field builders for your domain and want them available by default, the `Logger` class is designed to be easy to subclass.

```java
public class MyLogger extends Logger<PersonBuilder> {
  protected MyLogger(CoreLogger core, PersonBuilder fieldBuilder) {
    super(core, fieldBuilder);
  }
}

public class MyLoggerFactory {
  private static final PersonBuilder PERSON_BUILDER_SINGLETON = new PersonBuilder();
  
  public static MyLogger getLogger() {
    return new MyLogger(CoreLoggerFactory.getLogger(), PERSON_BUILDER_SINGLETON);
  }
}

MyLogger myLogger = MyLoggerFactory.getLogger();
```

Subclassing the logger will also remove the type parameter from your code, so you don't have to type `Logger<?>` everywhere.

### Nulls and Exceptions

By default, values are `@NotNull`, and passing in `null` to values is not recommended.  If you want to handle nulls, you can extend the field builder as necessary:

```java
public interface NullableFieldBuilder extends Field.Builder {
  // extend as necessary
  default Field nullableString(String name, String nullableString) {
    Value<?> nullableValue = (value == null) ? Value.nullValue() : Value.string(nullableString);
    return keyValue(name, nullableValue);
  }
}
```

Field names are never allowed to be null.  If a field name is null, it will be replaced at runtime with `unknown-echopraxia-N` where N is an incrementing number.

```java
logger.info("Message name {}", fb -> 
  fb.only(fb.string(null, "some-value")) // null field names not allowed
);
```

In addition, `fb.only()` will return an empty list if a null field is passed in:


```java
logger.info("Message name {}", fb -> 
  Field field = null;
  return fb.only(field); // returns an empty list of fields.
);
```

Because a field builder function runs in a closure, if an exception occurs it will be caught by the default thread exception handler.  If it's the main thread, it will print to console and terminate the JVM, but other threads .  Consider setting a [default thread exception handler](https://www.logicbig.com/tutorials/core-java-tutorial/java-se-api/default-uncaught-exception-handler.html) that additionally logs, and avoid uncaught exceptions in field builder closures:

```java
logger.info("Message name {}", fb -> {
  String name = methodThatThrowsException(); // BAD
  return fb.only(fb.string(name, "some-value"));
});
```

Instead, only call field builder methods inside the closure and keep any construction logic outside:

```java
String name = methodThatThrowsException(); // GOOD
logger.info("Message name {}", fb -> {
  return fb.only(fb.string(name, "some-value"));
});
```

## Context

You can also add fields directly to the logger using `logger.withFields` for contextual logging:

```java
Logger<?> loggerWithFoo = basicLogger.withFields(fb -> fb.onlyString("foo", "bar"));
loggerWithFoo.info("JSON field will log automatically") // will log "foo": "bar" field in a JSON appender.
```

This works very well for HTTP session and request data such as correlation ids.

One thing to be aware of that the popular idiom of using `public static final Logger<?> logger` can be limiting in cases where you want to include context data.  For example, if you have a number of objects with their own internal state, it may be more appropriate to create a logger field on the object.

```java
public class PlayerData {

  // the date is scoped to an instance of this player
  private Date lastAccessedDate = new Date();

  // logger is not static because lastAccessedDate is an instance variable
  private final Logger<BuilderWithDate> logger =
      LoggerFactory.getLogger()
          .withFieldBuilder(BuilderWithDate.class)
          .withFields(fb -> fb.onlyDate("last_accessed_date", lastAccessedDate));

}
```

### Thread Context

You can also resolve any fields in Mapped Diagnostic Context (MDC) into fields, using `logger.withThreadContext()`.  This method provides a pre-built function that calls `fb.string` for each entry in the map.

Because MDC is thread local, if you pass the logger between threads or use asynchronous processing i.e. `CompletionStage/CompletableFuture`, you may have inconsistent results.

```java
org.slf4j.MDC.put("mdckey", "mdcvalue");
myLogger.withThreadContext().info("This statement has MDC values in context");
```

### Thread Safety

Thread safety is something to be aware of when using context fields.  While fields are thread-safe and using a context is far more convenient than using MDC, you do still have to be aware when you are accessing non-thread safe state.

For example, `SimpleDateFormat` is infamously not thread-safe, and so the following code is not safe to use in a multi-threaded context:

```java
private final static DateFormat df = new SimpleDateFormat("yyyyMMdd");

// UNSAFE EXAMPLE
private static final Logger<?> logger =
        LoggerFactory.getLogger()
        .withFields(fb -> fb.onlyString("unsafe_date", df.format(new Date())));
```

## Conditions

Logging conditions can be handled gracefully using `Condition` functions.  A `Condition` will take a `Level` and a `LoggingContext` which will return the fields of the logger.

```java
final Condition mustHaveFoo = (level, context) ->
        context.getFields().stream().anyMatch(field -> field.name().equals("foo"));
```

Conditions can be used either on the logger, on the statement, or against the predicate check.

> **NOTE**: Conditions are a great way to manage diagnostic logging in your application with more flexibility than global log levels can provide.
> 
> Consider enabling setting your application logging to `DEBUG` i.e. `<logger name="your.application.package" level="DEBUG"/>` and using [conditions to turn on and off debugging as needed](https://tersesystems.com/blog/2019/07/22/targeted-diagnostic-logging-in-production/).

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

### Predicates

Conditions can also be used in predicate blocks for expensive objects.

```java
if (logger.isInfoEnabled(condition)) {
  // only true if condition and is info  
}
```

### Handling Expensive Conditions with Asynchronous Logging

By default, conditions are evaluated in the running thread.  This can be a problem if conditions rely on external elements such as network calls or database lookups, or involve resources with locks.

Echopraxia provides an `AsyncLogger` that will evaluate conditions and log using another executor, so that the main business logic thread is not blocked on execution.  An `AsyncLogger` is created when calling the `withExecutor` method.

In `AsyncLogger`, the argument is a `Consumer` of `LoggerHandle`.  `LoggerHandle` takes the same arguments as described above, i.e.

```java
AsyncLogger<?> logger = LoggerFactory.getLogger().withExecutor(loggingExecutor);
logger.info(h -> h.log("Message template {}", fb -> fb.onlyString("foo", "bar")));
```

One important detail is that the logging executor should be a daemon thread, so that it does not block JVM exit:

```java
private static final Executor loggingExecutor =
  Executors.newSingleThreadExecutor(
      r -> {
        Thread t = new Thread(r);
        t.setDaemon(true); // daemon means the thread won't stop JVM from exiting
        t.setName("logging-thread");
        return t;
      });
```

Putting it all together:

```java
public class Async {
  private static final ExecutorService loggingExecutor =
      Executors.newSingleThreadExecutor(
          r -> {
            Thread t = new Thread(r);
            t.setDaemon(true); // daemon means the thread won't stop JVM from exiting
            t.setName("echopraxia-logging-thread");
            return t;
          });

  private static final Condition expensiveCondition = new Condition() {
    @Override
    public boolean test(Level level, LoggingContext context) {
      try {
        Thread.sleep(1000L);
        return true;
      } catch (InterruptedException e) {
        return false;
      }
    };
  };

  private static final AsyncLogger<?> logger = LoggerFactory.getLogger()
    .withExecutor(loggingExecutor)
    .withCondition(expensiveCondition);

  public static void main(String[] args) throws InterruptedException {
    System.out.println("BEFORE logging block");
    for (int i = 0; i < 10; i++) {
      // This should take no time on the rendering thread :-)
      logger.info(h -> h.log("Prints out after expensive condition"));
    }
    System.out.println("AFTER logging block");
    System.out.println("Sleeping so that the JVM stays up");
    Thread.sleep(1001L * 10L);
  }
}
```

Note that because logging is asynchronous, you must be very careful when accessing thread local state.  Thread local state associated with logging, i.e. MDC / ThreadContext is automatically carried through, but in some cases you may need to do additional work.

For example, if you are using Spring Boot and are using `RequestContextHolder.getRequestAttributes()` when constructing context fields, you must call `RequestContextHolder.setRequestAttributes(requestAttributes)` so that the attributes are available to the thread:

```java
public class GreetingController {
  private static final String template = "Hello, %s!";
  private final AtomicLong counter = new AtomicLong();

  private static final AsyncLogger<HttpRequestFieldBuilder> logger =
    LoggerFactory.getLogger()
      .withFieldBuilder(HttpRequestFieldBuilder.class)
      .withExecutor(ForkJoinPool.commonPool())
      .withFields(
        fb -> {
          // Any fields that you set in context you can set conditions on later,
          // i.e. on the URI path, content type, or extra headers.
          HttpServletRequest request =
            ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
              .getRequest();
          return fb.requestFields(request);
        });

  @GetMapping("/greeting")
  public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
    logger.info(wrap(h -> h.log("This logs asynchronously with HTTP request fields")));
    return new Greeting(counter.incrementAndGet(), String.format(template, name));
  }

  private Consumer<LoggerHandle<HttpRequestFieldBuilder>> wrap(Consumer<LoggerHandle<HttpRequestFieldBuilder>> c) {
    // Because this takes place in the fork-join common pool, we need to set request
    // attributes in the thread before logging so we can get request fields.
    final RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
    return h -> {
      try {
        RequestContextHolder.setRequestAttributes(requestAttributes);
        c.accept(h);
      } finally {
        RequestContextHolder.resetRequestAttributes();
      }
    };
  }
}
```

There are other ways to handle this wrapping, for example [extending executors](https://medium.com/asyncparadigm/logging-in-a-multithreaded-environment-and-with-completablefuture-construct-using-mdc-1c34c691cef0), but that's a bigger topic.

### Dynamic Conditions with Scripts

One of the limitations of logging is that it's not that easy to change logging levels in an application at run-time.  In modern applications, you typically have complex inputs and may want to enable logging for some very specific inputs without turning on your logging globally.

Script Conditions lets you tie your conditions to scripts that you can change and re-evaluate at runtime.

The security concerns surrounding Groovy or Javascript make them unsuitable in a logging environment.  Fortunately, Echopraxia provides a [Tweakflow](https://twineworks.github.io/tweakflow) script integration that lets you evaluate logging statements **safely**.  Tweakflow comes with a [VS Code integration](https://marketplace.visualstudio.com/items?itemName=twineworks.tweakflow), a [reference guide](https://twineworks.github.io/tweakflow/reference.html), and a [standard library](https://twineworks.github.io/tweakflow/modules/std.html) that contains useful regular expression and date manipulation logic.

Because Scripting has a dependency on Tweakflow, it is broken out into a distinct library that you must add to your build.

Maven:

```
<dependency>
  <groupId>com.tersesystems.echopraxia</groupId>
  <artifactId>scripting</artifactId>
  <version>1.1.3</version>
</dependency>
```

Gradle:

```
implementation "com.tersesystems.echopraxia:scripting:1.1.3" 
```

#### String Based Scripts

You also have the option of passing in a string directly:

```java
StringBuilder b = new StringBuilder("");
b.append("library echopraxia {");
b.append("  function evaluate: (string level, dict fields) ->");
b.append("    level == \"INFO\";");
b.append("}");
String scriptString = b.toString();  
Condition c = ScriptCondition.create(false, scriptString, Throwable::printStackTrace);
```

#### File Based Scripts

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

#### Watched Scripts

You can also change file based scripts while the application is running, if they are in a directory watched by `ScriptWatchService`.  

To configure `ScriptWatchService`, pass it the directory that contains your script files:

```java
final Path watchedDir = Paths.get("/your/script/directory");
ScriptWatchService watchService = new ScriptWatchService(watchedDir);

Path filePath = watchedDir.resolve("myscript.tf");

Logger logger = LoggerFactory.getLogger();

final ScriptHandle watchedHandle = watchService.watchScript(filePath, 
        e -> logger.error("Script compilation error", e));
final Condition condition = ScriptCondition.create(watchedHandle);

logger.info(condition, "Statement only logs if condition is met!")
        
// After that, you can edit myscript.tf and the condition will 
// re-evaluate the script as needed automatically!
        
// You can delete the file, but doing so will log a warning from `ScriptWatchService`
// Recreating a deleted file will trigger an evaluation, same as modification.

// Note that the watch service creates a daemon thread to watch the directory.
// To free up the thread and stop watching, you should call close() as appropriate:
watchService.close();
```

#### Custom Source Scripts

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
        p -> fb -> fb.list(fb.string("name", p.name), fb.number("age", p.age)));

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
  <version>1.1.3</version>
</dependency>
```

Gradle:

```
implementation "com.tersesystems.echopraxia:semantic:1.1.3" 
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
    .argument(sfb -> sfb.string("name", person.name)) // note only a single field
    .argument(sfb -> sfb.number("age", person.age))
    .log();
```

### Installation

Fluent Loggers have a dependency on the `api` module, but do not have any implementation dependencies.

Maven:

```
<dependency>
  <groupId>com.tersesystems.echopraxia</groupId>
  <artifactId>fluent</artifactId>
  <version>1.1.3</version>
</dependency>
```

Gradle:

```
implementation "com.tersesystems.echopraxia:fluent:1.1.3" 
```

## Core Logger 

Because Echopraxia provides its own implementation independent API, some implementation features are not exposed normally.  If you want to use implementation specific features like markers, you will need to use a core logger.

### Logstash API

First, import the `logstash` package and the `core` package.  This gets you access to the `CoreLoggerFactory` and  `CoreLogger`, which can be cast to `LogstashCoreLogger`:

```java
import com.tersesystems.echopraxia.logstash.*;
import com.tersesystems.echopraxia.core.*;

LogstashCoreLogger core = (LogstashCoreLogger) CoreLoggerFactory.getLogger();
```

The `LogstashCoreLogger` has a `withMarkers` method that takes an SLF4J marker:

```java
Logger<?> logger = LoggerFactory.getLogger(
      core.withMarkers(MarkerFactory.getMarker("SECURITY")), Field.Builder.instance);
```

If you have markers set as context, you can evaluate them in a condition through casting to `LogstashLoggingContext`:

```java
Condition hasAnyMarkers = (level, context) -> {
   LogstashLoggingContext c = (LogstashLoggingContext) context;
   List<org.slf4j.Marker> markers = c.getMarkers();
   return markers.size() > 0;
};
```

If you need to get at the SLF4J logger from a core logger, you can cast and call `core.logger()`:

```java
Logger<?> baseLogger = LoggerFactory.getLogger();
LogstashCoreLogger core = (LogstashCoreLogger) baseLogger.core();
org.slf4j.Logger slf4jLogger = core.logger();
```

### Log4J

Similar to Logstash, you can get access to Log4J specific features by importing 

```java
import com.tersesystems.echopraxia.log4j.*;
import com.tersesystems.echopraxia.core.*;

Log4JCoreLogger core = (Log4JCoreLogger) CoreLoggerFactory.getLogger();
```

The `Log4JCoreLogger` has a `withMarker` method that takes a Log4J marker:

```java
final Marker securityMarker = MarkerManager.getMarker("SECURITY");
Logger<?> logger = LoggerFactory.getLogger(
      core.withMarker(securityMarker), Field.Builder.instance);
```

If you have a marker set as context, you can evaluate it in a condition through casting to `Log4JLoggingContext`:

```java
Condition hasAnyMarkers = (level, context) -> {
   Log4JLoggingContext c = (Log4JLoggingContext) context;
   Marker m = c.getMarker();
   return securityMarker.equals(m);
};
```

If you need to get the Log4j logger from a core logger, you can cast and call `core.logger()`:

```java
Logger<?> baseLogger = LoggerFactory.getLogger();
Log4JCoreLogger core = (Log4JCoreLogger) baseLogger.core();
org.apache.logging.log4j.Logger log4jLogger = core.logger();
```
