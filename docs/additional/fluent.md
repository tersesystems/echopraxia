# Fluent Logger

Fluent logging is done using a `FluentLoggerFactory`.  

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

It is useful in situations where arguments may need to be built up over time.

```java


FluentLogger<FieldBuilder> logger = FluentLoggerFactory.getLogger(getClass());

Person person = new Person("Eloise", 1);

logger
        .

atInfo()
    .

message("name = {}, age = {}")
    .

argument(fb ->fb.

string("name",person.name))
        .

argument(fb ->fb.

number("age",person.age))
        .

log();
```
