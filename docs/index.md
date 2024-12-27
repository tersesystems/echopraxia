# Echopraxia

[Echopraxia](https://github.com/tersesystems/echopraxia) is a Java logging API designed around structured logging, rich context, and conditional logging.  There are Logback, Log4J2, and JUL implementations, but Echopraxia's API is completely dependency-free, meaning it can be implemented with any logging API, i.e. jboss-logging, JEP 264, or even your own custom implementation.

What this means is that all arguments in a logging statement have a name and a value, for example:

```java
var fb = FieldBuilder.instance();
logger.info("arg1 is {} and arg2 is {}", fb.list(
  fb.string("name", "value"),
  fb.number("age", 13)
));
```

writes out in logfmt as:

```
INFO 13.232 arg1 is name=value and arg2 is age=13
```

and in a JSON format as:

```json
{
  "message": "arg1 is name=value and arg2 is age=13",
  "name": "value",
  "age": 13
}
```

What makes Echopraxia effective -- especially for debugging -- is that you can define your own field builders to map between objects and fields, and then pass in your own objects and render complex objects.  For example, we can render a `Person` object:

```java
var fb = PersonFieldBuilder.instance();
Logger logger = LoggerFactory.getLogger(getClass());

Person abe = new Person("Abe", 1, "yodelling");
abe.setFather(new Person("Bert", 35, "keyboards"));
abe.setMother(new Person("Candace", 30, "iceskating"));

logger.info("{}", fb.person("abe", abe));
```

And print out the internal state of the `Person` in both logfmt and JSON.

```
INFO 13.223 abe={Abe, 1, father={Bert, 35, father=null, mother=null, interests=[keyboards]}, mother={Candace, 30, father=null, mother=null, interests=[iceskating]}, interests=[yodelling]}
```

Echopraxia also has a "contextual" logging feature that renders fields in JSON:

```java
var fooLogger = logger.withFields(fb.string("foo", "bar"));
fooLogger.info("This logs the 'foo' field automatically in JSON");
```

And has conditional logging based on fields and exceptions using JSONPath:

```java
import static echopraxia.logging.api.*;

JsonPathCondition c = JsonPathCondition.findCondition((level, ctx) ->
    ctx.findString("$.exception.stackTrace[0].methodName")
        .filter(s -> s.endsWith("Foo"))
        .isPresent());
logger.withCondition(c).error("Only render this error if method name ends in Foo", e);
```

And there is also a feature to change logging conditions [dynamically using scripts](https://github.com/tersesystems/smallest-dynamic-logging-example).

## Examples

For the fastest possible way to try out Echopraxia, download and run the [JBang script](https://github.com/tersesystems/smallest-dynamic-logging-example/blob/main/jbang/Script.java).

Simple examples and integrations with [dropwizard metrics](https://metrics.dropwizard.io/4.2.0/) and [OSHI](https://github.com/oshi/oshi) are available at [echopraxia-examples](https://github.com/tersesystems/echopraxia-examples).

For a web application example, see this [Spring Boot Project](https://github.com/tersesystems/echopraxia-spring-boot-example).

## Scala API

There is a Scala API available at [https://github.com/tersesystems/echopraxia-plusscala](https://github.com/tersesystems/echopraxia-plusscala).