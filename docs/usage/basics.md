
# Basic Usage

Echopraxia is simple and easy to use, and looks very similar to SLF4J.

Add the import:

```java
import echopraxia.api.*;
import echopraxia.logger.*;
```

Define a logger (usually in a controller or singleton -- `getClass()` is particularly useful for abstract controllers):

```java
import echopraxia.simple.*;

final Logger basicLogger = LoggerFactory.getLogger(getClass());
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

However, when you log arguments, you pass a function which provides you with a customizable field builder and returns a `FieldBuilderResult` -- a `Field` is a `FieldBuilderResult`, so you can do:

```java
var fb = FieldBuilder.instance();
basicLogger.info("Message name {}", fb.string("name", "value"));
```

If you are returning multiple fields, then using `fb.list` will return a `FieldBuilderResult`:

```java
basicLogger.info("Message name {} age {}", fb.list(
  fb.string("name", "value"),
  fb.number("age", 13)
));
```

And `fb.list` can take many inputs as needed, for example a stream:

```java
var arrayOfFields = { fb.string("name", "value") };
basicLogger.info("Message name {}", fb.list(arrayOfFields));
```

The field builder is customizable, so you can (and should!) define your own methods to construct fields out of complex objects:

```java
class OrderFieldBuilder extends FieldBuilder {
    // Use apply to render order as a Field
    public Field apply(Order order) {
        // assume apply methods for line items etc
        return keyValue("order", Value.object(
          apply(order.lineItems), 
          apply(order.paymentInfo),
          apply(order.shippingInfo),
          apply(order.userId)      
        ));
    }
}

var fb = new OrderFieldBuilder();
logger.info("Rendering order {}", fb.apply(order));
```

Please read the [field builder](fieldbuilder.md) section for more information on making your own field builder methods.

You can log multiple arguments and include the exception if you want the stack trace:

```java
basicLogger.info("Message name {}", fb.list(
  fb.string("name", "value"),
  fb.exception(e)
));
```

You can also create the fields yourself and pass them in directly:

```java
var fb = FieldBuilder.instance();
var nameField = fb.string("name", "value");
var ageField = fb.number("age", 13);
var exceptionField = fb.exception(e);
logger.info(nameField, ageField, exceptionField);
```

Note that unlike SLF4J, you don't have to worry about including the exception as an argument "swallowing" the stacktrace.  If an exception is present, it's always applied to the underlying logger.
 