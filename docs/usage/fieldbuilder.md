# Field Builders

Echopraxia lets you specify field builders whenever you want to log domain objects.

You do this by defining your own implementation of `FieldBuilder` and then pass that field builder into your `Logger` using `withFieldBuilder`.

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

And then create a `Logger<BuilderWithDate>`:

```
Logger<BuilderWithDate> dateLogger = basicLogger.withFieldBuilder(BuilderWithDate.instance);
```

And now you can render a date automatically:

```java
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

## Nulls

At some point you will have a value that you want to render and the Java API will return `null`.

I recommend using [Jetbrains annotations](https://www.jetbrains.com/help/idea/annotating-source-code.html#jetbrains-annotations) which includes a `@NotNull` annotation.

You can defensively program against this by explicitly checking against nulls in the field builder, using `Value.optional` over null, if possible.

```java
public interface NullableFieldBuilder extends FieldBuilder {
  // extend as necessary
  default Field nullableString(String name, String nullableString) {
    Value<?> nullableValue = Value.optional(Optional.ofNullable(nullableString));
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

## Exceptions

Avoid throwing exceptions in a field builder function.  Because a field builder function runs in a closure, if an exception is thrown it will be caught by Echopraxia's error handler which writes the exception to `System.err` by default.  

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

## Field Presentation

 There are times when the default field presentation is awkward, and you'd like to cut down on the amount of information displayed in the message.  You can do this by adding presentation hints to the field.

The `FieldBuilder` interface provides some convenience methods around `Field` and `Value`.  In particular, there are two methods, `keyValue` and `value` that are used to create fields.  The `PresentationField` interface implements the `Field` interface and also provides some extra methods for customizing the presentation of the fields in a line oriented text format.  These presentation hints are provided by field attributes and are used by the `toString` formatter.

You can access these methods by passing in `PresentationField.class` to either `value` or `keyValue`, and then calling the extension methods -- this provides an easy way to construct fields while hiding the implementation outside the field builder.

There is a `PresentationFieldBuilder` interface that you can extend that provides `PresentationField` by default.

### asValueOnly

The `asValueOnly` method has the effect of turning a "key=value" field into a "value" field in text format, just like the value method:

```java
// same as Field valueField = value(name, value);
Field valueField = keyValue("onlyValue", Value.string("someText"), PresentationField.class).asValueOnly();
valueField.toString() // renders someText
```

### asCardinal

The `asCardinal` method, when used on a field with an array value or on a string, displays the number of elements in the array bracketed by "|" characters in text format:

```java
Field cardinalField = keyValue("elements", Value.array(1,2,3), PresentationField.class).asCardinal();
cardinalField.toString(); // renders elements=|3|
```

### withDisplayName

The `withDisplayName` method shows a human readable string in text format bracketed in quotes:

```java
Field readableField = keyValue("json_field", Value.number(1), PresentationField.class).withDisplayName("human readable name");
readableField.toString() // renders "human readable name"=1
```

### abbreviateAfter

The `abbreviateAfter` method will truncate an array or string that is very long and replace the rest with ellipsis:

```java
Field abbrField = keyValue("abbreviatedField", Value.string(veryLongString), PresentationField.class).abbreviateAfter(5);
abbrField.toString() // renders abbreviatedField=12345...
```

## asElided

The `asElided` method will elide the field so that it is passed over and does not show in text format:

```java
Field abbrField = keyValue("abbreviatedField", Value.string(veryLongString), PresentationField.class).asElided();
abbrField.toString() // renders ""
```

This is particularly useful in objects that have elided children that you don't need to see in the message:

```java
Field first = keyValue("first", string("bar"), PresentationField.class).asElided();
Field second = keyValue("second", string("bar"), PresentationField.class);
Field third = keyValue("third", string("bar"), PresentationField.class).asElided();
List<Field> fields = List.of(first, second, third);
Field object = keyValue("object", Value.object(fields), PresentationField.class);
assertThat(object.toString()).isEqualTo("object={second=bar}");
```

### StructuredFormat

Using the `withStructuredFormat` method with a field visitor will allow the JSON output to contain different fields when you want to provide extra type information that isn't relevant in text.

For example, imagine you want to render a `java.lang.Instant` in JSON as having an explicit `@type` of `http://www.w3.org/2001/XMLSchema#dateTime` alongside the value, but don't want to needlessly complicate your output.  Using `withStructuredFormat` with a class extending `SimpleFieldVisitor`, you can intercept and override field processing in JSON:

```java
public class InstantFieldBuilder implements PresentationFieldBuilder {

  static InstantFieldBuilder instance() {
    return new InstantFieldBuilder();
  }

  final FieldVisitor instantVisitor = new InstantFieldVisitor();

  public PresentationField instant(String name, Instant instant) {
    return string(name, instant.toString()).withStructuredFormat(instantVisitor);
  }

  PresentationField typedInstant(String name, Value<String> v) {
    return object(name, typedInstantValue(v));
  }

  Value.ObjectValue typedInstantValue(Value<String> v) {
    return Value.object(
      string("@type", "http://www.w3.org/2001/XMLSchema#dateTime"), keyValue("@value", v));
  }

  class InstantFieldVisitor extends SimpleFieldVisitor {
    @Override
    public @NotNull Field visitString(@NotNull Value<String> stringValue) {
      return typedInstant(name, stringValue);
    }

    @Override
    public @NotNull ArrayVisitor visitArray() {
      return new InstantArrayVisitor();
    }

    class InstantArrayVisitor extends SimpleArrayVisitor {
      @Override
      public void visitStringElement(Value.StringValue stringValue) {
        this.elements.add(typedInstantValue(stringValue));
      }
    }
  }
}
```

This field builder will render `fb.instant("startTime", Instant.ofEpochMillis(0))` as the following in text:

```
startTime=1970-01-01T00:00:00Z
```

But will render JSON as:

```json
{"startTime":{"@type":"http://www.w3.org/2001/XMLSchema#dateTime","@value":"1970-01-01T00:00:00Z"}}
```
