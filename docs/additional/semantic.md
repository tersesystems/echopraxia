
# Semantic Logger

Semantic Loggers are strongly typed, and will only log a particular kind of argument.  All the work of field building and setting up a message is done from setup.

## Installation

Semantic Loggers have a dependency on the `api` module, but do not have any implementation dependencies.

Maven:

```
<dependency>
  <groupId>com.tersesystems.echopraxia</groupId>
  <artifactId>semantic</artifactId>
  <version><VERSION></version>
</dependency>
```

Gradle:

```
implementation "com.tersesystems.echopraxia:semantic:<VERSION>" 
```

## Basic Usage

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

## Conditions

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

## Context

Semantic loggers can add fields to context in the same way other loggers do.

```java
SemanticLogger<Person> loggerWithContext =
  logger.withFields(fb -> fb.string("some_context_field", contextValue));
```
