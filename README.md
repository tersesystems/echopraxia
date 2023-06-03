
<!---freshmark shields
output = [
	link(shield('mvnrepository', 'mvnrepository', '{{group}}', 'blue'), 'https://mvnrepository.com/artifact/{{group}}'),
	link(shield('License Apache', 'license', 'Apache', 'blue'), 'https://tldrlegal.com/license/apache-license-2.0-(apache-2.0)'),
	].join('\n')
-->
[![mvnrepository](https://img.shields.io/badge/mvnrepository-com.tersesystems.echopraxia-blue.svg)](https://mvnrepository.com/artifact/com.tersesystems.echopraxia)
[![License Apache](https://img.shields.io/badge/license-Apache-blue.svg)](https://tldrlegal.com/license/apache-license-2.0-(apache-2.0))
<!---freshmark /shields -->
# Echopraxia

[Echopraxia](https://github.com/tersesystems/echopraxia) is a Java logging API designed around structured logging.  

What this means is that all arguments in a logging statement have a name and a value, for example:

```java
logger.info("arg1 is {} and arg2 is {}", fb -> fb.list(
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

What makes Echopraxia effective -- especially for debugging -- is that you can define your own mappings, and then pass in your own objects and render complex objects.  For example, we can render a `Person` object:

```java
Logger<PersonFieldBuilder> logger = LoggerFactory.getLogger(getClass()).withFieldBuilder(PersonFieldBuilder.instance());

Person abe = new Person("Abe", 1, "yodelling");
abe.setFather(new Person("Bert", 35, "keyboards"));
abe.setMother(new Person("Candace", 30, "iceskating"));

logger.info("{}", fb -> fb.person("abe", abe));
```

And print out the internal state of the `Person` in both logfmt and JSON.

```
INFO 13.223 abe={Abe, 1, father={Bert, 35, father=null, mother=null, interests=[keyboards]}, mother={Candace, 30, father=null, mother=null, interests=[iceskating]}, interests=[yodelling]}
```

## Documentation

Please see the [online documentation](https://tersesystems.github.io/echopraxia).

## Examples

For the fastest possible way to try out Echopraxia, download and run the [JBang script](https://github.com/tersesystems/smallest-dynamic-logging-example/blob/main/jbang/Script.java).

Simple examples and integrations with [dropwizard metrics](https://metrics.dropwizard.io/4.2.0/) and [OSHI](https://github.com/oshi/oshi) are available at [echopraxia-examples](https://github.com/tersesystems/echopraxia-examples).

For a web application example, see this [Spring Boot Project](https://github.com/tersesystems/echopraxia-spring-boot-example).

## Scala API

There is a Scala API available at [https://github.com/tersesystems/echopraxia-plusscala](https://github.com/tersesystems/echopraxia-plusscala).

## Benchmarks

Benchmarks are available at [BENCHMARKS.md](BENCHMARKS.md).
