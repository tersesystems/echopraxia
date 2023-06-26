# Field Builders

To do most useful things in Echopraxia, you'll want to define field builders.  This page explains the overall API of fields and values, what a field builder does, and how to use it.

## Imports

Start by importing the API package.  Everything relevant to field building will be in there.

```java
import com.tersesystems.echopraxia.api.*;
```

## Fields and Values

Structured logging in Echopraxia is defined using `Field` and `Value`.  A `Field` consists of a `name()` that is a `String`, and a `value()` of type `Value<?>`.

### Values

A `Value` corresponds mostly to the JSON infoset. It can be a *primitive*: a string, a number, a boolean, a null, or `java.lang.Throwable`.  Or, it can be *complex*: an array that contains a list of values, or an object which contains a list of fields.

To create a primitive value, you call a static factory method:

* `Value.string("string value")` creates a `Value<String>`
* `Value.nullValue()` create a `Value<Void>`
* `Value.number(intValue)` create a `Value<Number>`
* `Value.bool(true)` creates a `Value<Boolean>`
* `Value.exception(throwable)` creates a `Value<Throwable>`

For complex objects, there are some utility methods:

* `Value.array(valueList)` takes `Value` or the known primitives 
* `Value.array(function, valueList)` will map the elements of `valueList` into `Value` using `function`.
* `Value.object(fields)` takes a list of fields
* `Value.object(function, objectList)` will map the elements of `objectList` into `Field` using `function`.

In addition, there is `Value.optional` which takes `Optional<Value<V>>` and returns `Value<?>` where `nullValue` is used if `Optional.empty()` is found. 

### Fields

The `Field` interface has some static methods that are the primary way to create fields:

* `Field.keyValue(name, value)` returns a `Field` with the given name and value set, displaying as "name=value" in text.
* `Field.keyValue(name, value, PresentationField.class)` returns a `PresentationField` that has more methods on it.

You can also define and extend `Field` with your own implementation, although that is outside the scope of this section.

You can also create a field using `Field.value(name, value)` or `Field.value(name, value, PresentationField.class)`, which creates a `Field` with the presentation attribute `asValueOnly` set.

## Field Presentation

There are times when the default field presentation is awkward, and you'd like to cut down on the amount of information displayed in the message.  You can do this by adding presentation hints to the field.

The `PresentationField` interface implements the `Field` interface and also provides some extra methods for customizing the presentation of the fields in a line oriented text format.  These presentation hints are provided by field attributes and are used by the `toString` formatter.

For the examples, we'll assume that `PresentationFieldBuilder` is being used here and therefore `keyValue` returns `PresentationField`.

### asValueOnly

The `asValueOnly` method has the effect of turning a "key=value" field into a "value" field in text format, just like the value method:

```java
// same as Field valueField = value(name, value);
Field valueField = keyValue("onlyValue", Value.string("someText")).asValueOnly();
valueField.toString() // renders someText
```

### asCardinal

The `asCardinal` method, when used on a field with an array value or on a string, displays the number of elements in the array bracketed by "|" characters in text format:

```java
var cardinalField = keyValue("elements", Value.array(1,2,3).asCardinal();
cardinalField.toString(); // renders elements=|3|
```

### withDisplayName

The `withDisplayName` method shows a human readable string in text format bracketed in quotes:

```java
var readableField = keyValue("json_field", Value.number(1)).withDisplayName("human readable name");
readableField.toString() // renders "human readable name"=1
```

### abbreviateAfter

The `abbreviateAfter` method will truncate an array or string that is very long and replace the rest with ellipsis:

```java
var abbrField = keyValue("abbreviatedField", Value.string(veryLongString)).abbreviateAfter(5);
abbrField.toString() // renders abbreviatedField=12345...
```

### asElided

The `asElided` method will elide the field so that it is passed over and does not show in text format:

```java
var abbrField = keyValue("abbreviatedField", Value.string(veryLongString)).asElided();
abbrField.toString() // renders ""
```

This is particularly useful in objects that have elided children that you don't need to see in the message:

```java
Field first = keyValue("first", string("bar")).asElided();
Field second = keyValue("second", string("bar"));
Field third = keyValue("third", string("bar")).asElided();
List<Field> fields = List.of(first, second, third);
Field object = keyValue("object", Value.object(fields));
assertThat(object.toString()).isEqualTo("object={second=bar}");
```

## Defining Field Builders

The `FieldBuilder` interface provides some convenience methods around `Field` and `Value`.  

* `keyValue`: renders a field with `name=value` when rendered in logfmt line oriented text.
* `value`: renders a field with `value` when rendered in logfmt line oriented text.

`FieldBuilder` comes with some additional methods for common types, i.e.

* `fb.string`: creates a field with a string as a value, same as `fb.keyValue(name, Value.string(str))`.
* `fb.number`: creates a field with a number as a value, same as `fb.keyValue(name, Value.number(num))`.
* `fb.bool`: creates a field with a boolean as a value, same as `fb.keyValue(name, Value.bool(b))`.
* `fb.nullValue`: creates a field with a null as a value, same as `fb.keyValue(name, Value.nullValue())`
* `fb.array`: creates a field with an array as a value, same as `fb.keyValue(name, Value.array(arr))`
* `fb.obj`: creates a field with an object as a value, same as `fb.keyValue(name, Value.``object``(o))`
* `fb.exception`: creates a field with a throwable as a value, same as `fb.keyValue(name, Value.exception(t))`.

The `PresentationFieldBuilder` interface is the same as `FieldBuilder` but returns `PresentationField` by default.

To create a field builder, you start with an interface (typically using `FieldBuilder` or `PresentationFieldBuilder` as a base) and then pass that field builder into your `Logger` using `withFieldBuilder`.  Although convenient, you are not required to extend `FieldBuilder` or `PresentationFieldBuilder`, and can use `Field` and `Value` methods directly to create your own builders (useful if you don't want to expose field names directly).

You can then create custom methods on your field builder that will render your class.  In this case, we'll create a field builder that can handle a `java.util.Date`, and create `date` and `dateValue` methods for it.

```java
import com.tersesystems.echopraxia.api.*;
import java.util.Date;

public interface BuilderWithDate implements PresentationFieldBuilder {
  static BuilderWithDate instance = new BuilderWithDate() {};
  
  default PresentationField date(Date date) {
    return value("date", dateValue(date)); // use a default name
  }

  default PresentationField date(String name, Date date) {
    return value(name, dateValue(date));
  }

  // Renders a date as an ISO 8601 string.
  default Value.StringValue dateValue(Date date) {
    return Value.string(DateTimeFormatter.ISO_INSTANT.format(date.toInstant()));
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

## Field Names

Allowing the end user to define field names directly can be nice, but also introduces additional complexity.

The advantage to using a field name is that it allows a user to distinguish ad-hoc inputs.  For example, if you have a start date and an end date:

```
logger.info("{} to {}", fb.list(
  fb.date("start_date", startDate),
  fb.date("end_date", endDate)
));
```

This is more convenient than explicitly setting up the field builder with two extra methods and then having to call them:

```java
interface BuilderWithDate {
  default startDate(Date date) {
    return date("start_date", date);
  }

  default endDate(Date date) {
    return date("end_date", date);
  }
}

logger.info("{} to {}", fb.list(
  fb.startDate(startDate),
  fb.endDate(endDate)
));
```

However, there are downsides to defining names directly in statements, especially when using centralized logging.

The first issue is that you may have to sanitize or validate the input name depending on your centralized logging.  For example, ElasticSearch does not support [field names containing a . (dot) character](https://www.elastic.co/blog/introducing-the-de_dot-filter), so if you do not convert or reject invalid field names.  

A broader issue is that field names are not scoped by the logger name.  Centralized logging does not know that in the `FooLogger` a field name may be a string, but in the `BarLogger`, the same field name will be a number.  

This can cause issues in centralized logging -- ElasticSearch will attempt to define a schema based on dynamic mapping, meaning that if two log statements in the same index have the same field name but different types, i.e. `"error": 404` vs `"error": "not found"` then Elasticsearch will render `mapper_parsing_exception` and may reject log statements if you do not have [ignore_malformed](https://www.elastic.co/guide/en/elasticsearch/reference/current/ignore-malformed.html) turned on.  

Even if you turn `ignore_malformed` on or have different mappings, a change in a mapping across indexes will be enough to stop ElasticSearch from querying correctly.  ElasticSearch will also flatten field names, which can cause more confusion as conflicts will only come when objects have both the same field name and property, i.e. they are both called `error` and are objects that work fine, but fail when an optional `code` property is added.

Likewise, field names are not automatically scoped by context.  You may have collision cases where two different fields have the same name in the same statement:

```java
logger.withFields(fb -> fb.keyValue("user_id", userId)).info("{}", fb -> fb.keyValue("user_id", otherUserId));
```

This will produce a statement that has two `user_id` fields with two different values -- which is technically valid JSON, but may not be what centralized logging expects.  You can qualify your arguments by adding a [nested](https://github.com/logfellow/logstash-logback-encoder#nested-json-provider), or add logic that will validate/reject/clean invalid fields, but it may be simpler to explicitly pass in distinct names or namespace with `fb.object` or `Value.object`.

## Managing Null Values

At some point you will have a value that you want to render and the Java API will return `null`.

I recommend using [Jetbrains annotations](https://www.jetbrains.com/help/idea/annotating-source-code.html#jetbrains-annotations) which includes a `@NotNull` annotation.

You can defensively program against this by explicitly checking against nulls in the field builder, by explicitly checking against `null`.

```java
import java.time.Duration;
import org.jetbrains.annotations.NotNull;

public interface NullableFieldBuilder extends FieldBuilder {
  default Value<?> durationValue(@NotNull Duration duration) {
    return (deadline != null) ? Value.string(duration.toString()) : Value.nullValue();
  }
}
```

Field names are never allowed to be null.  If a field name is null, it will be replaced at runtime with `unknown-echopraxia-N` where N is an incrementing number.

```java
logger.info("Message name {}", fb -> 
  fb.string(null, "some-value") // null field names not allowed
);
```

## Complex Objects

The value of a field builder compounds as you build up complex objects from simple ones.

In the [custom field builder example](https://github.com/tersesystems/echopraxia-examples/blob/main/custom-field-builder/README.md), the `Person` class is rendered using a custom field builder:

```java
public interface PersonFieldBuilder extends FieldBuilder {

  // Renders a `Person` as an object field.
  default Field person(String fieldName, Person p) {
    return keyValue(fieldName, personValue(p));
  }

  default Value<?> personValue(Person p) {
    if (p == null) return Value.nullValue();
    // Note that properties must be broken down to the basic JSON types,
    // i.e. a primitive string/number/boolean/null or object/array.
    Field name = string("name", p.name());
    Field age = number("age", p.age());
    Field father = keyValue("father", personValue(p.getFather()));
    Field mother = keyValue("mother", personValue(p.getMother()));
    Field interests = array("interests", p.interests());
    return Value.object(name, age, father, mother, interests);
  }

  default Value<?> personValue(Optional<Person> p) {
    return Value.optional(p.map(this::personValue));
  }
}
```

And then you can render a person:

```java
Person user = ...
Logger<PersonFieldBuilder> personLogger = basicLogger.withFieldBuilder(PersonFieldBuilder.instance);
personLogger.info("Person {}", fb -> fb.person("user", user));
```

## Packages and Modules

As you scale up structured logging, you'll eventually reach a point where you'll have mappings for classes that are used in different packages and in different modules.  
The best practice here is to create a field builder per package, and extend field builder logic by extending interfaces, and use a domain specific `Logging` interface to manage set up for the end users.

For example, you may have a [domain driven design](https://en.wikipedia.org/wiki/Domain-driven_design) that defines classes entities and value objects in a domain layer, and these classes are then specialized and added to in subsequent modules.  Say you have a classic e-commerce application.  You might have several different modules:

* A `user` module containing customer information with address, payment, and authentication details.
* An `order` module containing order's components like line items, promotions, and checkout logic, depends on `user`

You would naturally have domain classes organized by package in each module, i.e. the user module would have `com.mystore.user.User`, and an order would be `com.mystore.order.Order` and would have a `User` attached to it.

So, define a field builder per package, and add a `Logging` abstract class that exposes a logger with the appropriate field builder:

```java
package com.mystore.user;

// field builder for the user package:
interface UserFieldBuilder extends PresentationFieldBuilder {
  UserFieldBuilder instance = new UserFieldBuilder() {};
  
  default PresentationField user(User user) {
    // ...
  }
}

public abstract class LoggingBase {
  protected static final Logger<UserFieldBuilder> logger =
    LoggerFactory.getLogger(this.getClass(), UserFieldBuilder.instance);
}

public class SomeUserService extends LoggingBase {
  public void someMethod(User user) {
    logger.trace("someMethod: {}", fb -> fb.user(user));
  }
}
```

Because the `order` package depends on the `user` package, and an `Order` contains a `User`, you want to extend `OrderFieldBuilder` with `UserFieldBuilder`

```java
package com.mystore.order;

// field builder for the order package
interface OrderFieldBuilder extends UserFieldBuilder {
  OrderFieldBuilder instance = new OrderFieldBuilder() {};
  
  default PresentationField orderId(OrderId id) {
    return (id == null) ? nullValue("order_id") : keyValue("order_id", id);
  }
  
  default PresentationField order(Order order) {
    if (order == null) 
      return nullField("order");
    else 
      return object("order", orderId(order.id), user(order.user) /* ...more fields */);
  }
}

public abstract class LoggingBase {
  protected static Logger<OrderFieldBuilder> logger = LoggerFactory.getLogger(this.getClass(), OrderFieldBuilder.instance);
}

public class SomeOrderService extends LoggingBase {
  public void someMethod(Order order) {
    logger.trace("someMethod: {}", fb -> fb.order(order));
  }
}
```

This way, you can have your loggers automatically "know" their domain classes and build on each other without exposing the underlying machinery to end users.

## Exception Handling

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

## StructuredFormat

Using the `withStructuredFormat` method with a field visitor will allow the JSON output to contain different fields when you want to provide extra type information that isn't relevant in text.

This can be used to show "human friendly" fields in a text based format, while showing more machine-readable output in JSON.  For example, you may want to render a duration in days:

```java
Field durationField = fb.duration("duration", Duration.ofDays(1));

assertThat(durationField.toString()).isEqualTo("1 day");
assertThatJson(durationField).inPath("$.duration").asString().isEqualTo("PT24H");
```

To do this, you would add a field as follows:

```java
public class MyFieldBuilder extends PresentationFieldBuilder {
  public PresentationField duration(String name, Duration duration) {
    Field structuredField = string(name, duration.toString());
    return string(name, duration.toDays() + " day")
            .asValueOnly()
            .withStructuredFormat(new SimpleFieldVisitor() {
              @Override
              public @NotNull Field visitString(@NotNull Value<String> stringValue) {
                return structuredField;
              }
            });
  } 
}
```

This is especially relevant for numeric fields where you may want to indicate [units](https://erikerlandson.github.io/blog/2020/04/26/your-data-type-is-a-unit/) -- for example, a retry may indicate a numeric value of seconds, and a cache size may indicate bytes, kilobytes, or gigabytes.  Unless you have a pre-defined schema or a consistent naming convention i.e. adding `_second` suffixes, the unit information is lost and comparing numbers with different units is needlessly complicated.

Type information is also useful in string contexts.  For example, imagine you want to render a `java.lang.Instant` in JSON as having an explicit `@type` of `http://www.w3.org/2001/XMLSchema#dateTime` alongside the value, but don't want to needlessly complicate your output.  Using `withStructuredFormat` with a class extending `SimpleFieldVisitor`, you can intercept and override field processing in JSON:

```java
public class InstantFieldBuilder implements PresentationFieldBuilder {

  private static final FieldVisitor instantVisitor = new InstantFieldVisitor();

  public PresentationField instant(String name, Instant instant) {
    return string(name, instant.toString()).withStructuredFormat(instantVisitor);
  }

  class InstantFieldVisitor extends SimpleFieldVisitor {
    @Override
    public @NotNull Field visitString(@NotNull Value<String> stringValue) {
      return typedInstant(name, stringValue);
    }

    PresentationField typedInstant(String name, Value<String> v) {
      return object(name, typedInstantValue(v));
    }

    Value.ObjectValue typedInstantValue(Value<String> v) {
      return Value.object(
        string("@type", "http://www.w3.org/2001/XMLSchema#dateTime"), keyValue("@value", v));
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
{
  "startTime": {
    "@type":"http://www.w3.org/2001/XMLSchema#dateTime",
    "@value":"1970-01-01T00:00:00Z"
  }
}
```

This also applies to Java durations using `ISO-8601` which you could mark with a `"@type": "https://schema.org/Duration"` and so on.