
### Log4J

Similar to Logstash, you can get access to Log4J specific features.

```java
import com.tersesystems.echopraxia.log4j.*;
import com.tersesystems.echopraxia.api.*;

Log4JCoreLogger core = (Log4JCoreLogger) CoreLoggerFactory.getLogger();
```

The `Log4JCoreLogger` has a `withMarker` method that takes a Log4J marker:

```java
final Marker securityMarker = MarkerManager.getMarker("SECURITY");
Logger<?> logger = LoggerFactory.getLogger(
      core.withMarker(securityMarker), FieldBuilder.instance);
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



### Direct Log4J API

In the event that the Log4J2 API must be used directly, an `EchopraxiaFieldsMessage` can be sent in for JSON rendering.

```java
import com.tersesystems.echopraxia.api.FieldBuilder;
import com.tersesystems.echopraxia.api.FieldBuilderResult;
import com.tersesystems.echopraxia.log4j.layout.EchopraxiaFieldsMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

FieldBuilder fb = FieldBuilder.instance();
Logger logger = LogManager.getLogger();
EchopraxiaFieldsMessage message = structured("echopraxia message {}", fb.string("foo", "bar"));
logger.info(message);

EchopraxiaFieldsMessage structured(String message, FieldBuilderResult args) {
  List<FIeld> loggerFields = Collections.emptyList();
  return new EchopraxiaFieldsMessage(message, loggerFields, result.fields());
}
```

Note that exceptions must also be passed outside the message to be fully processed by Log4J:

```java
Exception e = new RuntimeException();
EchopraxiaFieldsMessage message = structured("exception {}", fb.exception(e));
logger.info(message, e);
```

Unfortunately, I don't understand Log4J internals well enough to make conditions work using the Log4J API.  One option could be to write a [Log4J Filter](https://logging.apache.org/log4j/2.x/manual/filters.html) to work on a message.

