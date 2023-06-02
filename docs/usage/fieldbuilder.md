
### Custom Field Builders

Echopraxia lets you specify custom field builders whenever you want to log domain objects:

```java
import com.tersesystems.echopraxia.api.*;

public class BuilderWithDate implements FieldBuilder {
  private BuilderWithDate() {}
  public static final BuilderWithDate instance = new BuilderWithDate();

  // Renders a date as an ISO 8601 string.
  public Value.StringValue dateValue(Date date) {
    return Value.string(DateTimeFormatter.ISO_INSTANT.format(date.toInstant()));
  }

  public Field date(String name, Date date) {
    return value(name, dateValue(date));
  }
}
```

And now you can render a date automatically:

```java
Logger<BuilderWithDate> dateLogger = basicLogger.withFieldBuilder(BuilderWithDate.instance);
dateLogger.info("Date {}", fb -> fb.date("creation_date", new Date()));
```

This also applies to more complex objects.  In the [custom field builder example](https://github.com/tersesystems/echopraxia-examples/blob/main/custom-field-builder/README.md), the `Person` class is rendered using a custom field builder:

```java
public class PersonFieldBuilder implements FieldBuilder {
  private PersonFieldBuilder() {}
  public static final PersonFieldBuilder instance = new PersonFieldBuilder();

  // Renders a `Person` as an object field.
  public Field keyValue(String fieldName, Person p) {
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

And then you can do the same by calling `fb.keyValue` with `Person`:

```java
Person user = ...
Logger<PersonFieldBuilder> personLogger = basicLogger.withFieldBuilder(PersonFieldBuilder.instance);
personLogger.info("Person {}", fb -> fb.keyValue("user", user));
```

### Nulls and Exceptions

By default, values are `@NotNull`, and passing in `null` to values is not recommended.  If you want to handle nulls, you can extend the field builder as necessary:

```java
public interface NullableFieldBuilder extends FieldBuilder {
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
  fb.string(null, "some-value") // null field names not allowed
);
```

Because a field builder function runs in a closure, if an exception occurs it will be caught by the default thread exception handler.  If it's the main thread, it will print to console and terminate the JVM, but other threads [will swallow the exception whole](https://stackoverflow.com/questions/24834702/do-errors-thrown-within-uncaughtexceptionhandler-get-swallowed).  Consider setting a [default thread exception handler](https://www.logicbig.com/tutorials/core-java-tutorial/java-se-api/default-uncaught-exception-handler.html) that additionally logs, and avoid uncaught exceptions in field builder closures:

```java
logger.info("Message name {}", fb -> {
  String name = methodThatThrowsException(); // BAD
  return fb.string(name, "some-value");
});
```

Instead, only call field builder methods inside the closure and keep any construction logic outside:

```java
String name = methodThatThrowsException(); // GOOD
logger.info("Message name {}", fb -> {
  return fb.string(name, "some-value");
});
```
