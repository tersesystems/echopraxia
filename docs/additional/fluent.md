

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
    .argument(fb -> fb.string("name", person.name))
    .argument(fb -> fb.number("age", person.age))
    .log();
```

### Installation

Fluent Loggers have a dependency on the `api` module, but do not have any implementation dependencies.

Maven:

```
<dependency>
  <groupId>com.tersesystems.echopraxia</groupId>
  <artifactId>fluent</artifactId>
  <version><VERSION></version>
</dependency>
```

Gradle:

```
implementation "com.tersesystems.echopraxia:fluent:<VERSION>" 
```