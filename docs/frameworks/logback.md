### Logstash API

First, import the `logstash` package.  This gets you access to the `CoreLoggerFactory` and  `CoreLogger`, which can be cast to `LogstashCoreLogger`:

```java
import com.tersesystems.echopraxia.logstash.*;
import com.tersesystems.echopraxia.api.*;

LogstashCoreLogger core = (LogstashCoreLogger) CoreLoggerFactory.getLogger();
```

The `LogstashCoreLogger` has a `withMarkers` method that takes an SLF4J marker:

```java
Logger<?> logger = LoggerFactory.getLogger(
      core.withMarkers(MarkerFactory.getMarker("SECURITY")), FieldBuilder.instance);
```

If you have markers set as context, you can evaluate them in a condition through casting to `LogstashLoggingContext`:

```java
Condition hasAnyMarkers = (level, context) -> {
   LogstashLoggingContext c = (LogstashLoggingContext) context;
   List<org.slf4j.Marker> markers = c.getMarkers();
   return markers.size() > 0;
};
```

If you need to get at the SLF4J logger from a core logger, you can cast and call `core.logger()`:

```java
Logger<?> baseLogger = LoggerFactory.getLogger();
LogstashCoreLogger core = (LogstashCoreLogger) baseLogger.core();
org.slf4j.Logger slf4jLogger = core.logger();
```

### Direct Logback / SLF4J API

There will be times when the application uses an SLF4J logger, and it's not feasible to use an Echopraxia Logger.  This is not a problem: you can pass Echopraxia fields directly as arguments through SLF4J, and they will be rendered as expected.  You'll need to have a field builder in scope:

```java
FieldBuilder fb = FieldBuilder.instance();
org.slf4j.Logger slf4jLogger = org.slf4j.LoggerFactory.getLogger("com.example.Main");
slf4jLogger.info("SLF4J message {}", fb.string("foo", "bar"));
```

You can pass arguments in either individually (do not use `fb.list`):

```java
slf4jLogger.info("SLF4J message string {} number {}", fb.string("foo", "bar"), fb.number("count", 1));
```

Note that if you want to use exceptions in conditions, you pass exceptions through twice, once for the argument and again for the exception itself -- it must be the last argument, and must *not* have a message parameter to register as an exception (this is the SLF4J convention, it will eat exceptions otherwise):

```java
Exception e = new RuntimeException();
slf4jLogger.error("SLF4J exception {}", fb.exception(e), e);
```

SLF4J has no direct support for conditions, but we can fake it with a `ConditionMarker`:

```java
import com.tersesystems.echopraxia.logback.*;

Marker marker = ConditionMarker.apply(condition);
slf4jLogger.info(marker, "SLF4J message string {} number {}", fb.string("foo", "bar"), fb.number("count", 1));
```

You may want to represent session specific information as "logger context" field, which correspond to logstash markers.  If you want to use a context field, you can wrap a field in `FieldMarker` and then pass it in directly or use `Markers.aggregate` with a condition:

```java
FieldBuilder fb = FieldBuilder.instance();
FieldMarker fields = FieldMarker.apply(
  fb.list(
    fb.string("sessionId", "value"), 
    fb.number("correlationId", 1)
  )
); 
ConditionMarker conditionMarker = ConditionMarker.apply(
  Condition.stringMatch("sessionId", s -> s.raw().equals("value")))
);

logger.info(Markers.aggregate(fieldMarker, conditionMarker), "condition and marker");
```

To integrate this with Logback, you will need to have a `ConditionTurboFilter` which will evaluate conditions wrapped in `ConditionMarker`, and a `LogstashFieldAppender` that turns the fields into logstash markers and structured arguments for use with `LogstashEncoder` (note that this directly mutates the logging event, so don't have multiple async appenders going on with this):

```xml
<configuration> <!-- logback.xml -->
    
    <!-- evaluates conditions -->
    <turboFilter class="com.tersesystems.echopraxia.logback.ConditionTurboFilter"/>

    <appender name="ASYNC_JSON" class="net.logstash.logback.appender.LoggingEventAsyncDisruptorAppender">
        <!-- replaces fields with logstash markers and structured arguments -->
        <appender class="com.tersesystems.echopraxia.logstash.LogstashFieldAppender">
            <appender class="ch.qos.logback.core.FileAppender">
                <file>application.log</file>
                <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
            </appender>    
        </appender>
    </appender>

    <root level="INFO">
        <appender-ref ref="ASYNC_JSON"/>
    </root>
</configuration>
```



## Logback Converters

If you want to extract some fields directly in a line oriented context, you can use `FieldConverter`, `ArgumentFieldConverter`, or `LoggerFieldConverter` to extract fields using a JSON path.

For example, if you log something with a field called `book`:

```java
logger.info("{}", fb -> fb.string("book", "Interesting Book"));
```

Then you can use `%fields{$.book}` to extract the `book` field from the event and render it.  In most cases you will want to use `FieldConverter`, which searches for fields in both arguments and logger context, but if you want to isolate for one or the other, you can respectively use `ArgumentFieldConverter` or `LoggerFieldConverter`.

```xml
<configuration>

    <!-- Search both arguments and context, arguments takes precedence -->
    <conversionRule conversionWord="fields" converterClass="com.tersesystems.echopraxia.logback.FieldConverter"/>
    
    <!-- Search fields defined as arguments logger.info("{}", fb -> ...) -->
    <conversionRule conversionWord="argctx" converterClass="com.tersesystems.echopraxia.logback.ArgumentFieldConverter"/>
    
    <!-- Search fields defined in logger.withFields(...) -->
    <conversionRule conversionWord="loggerctx" converterClass="com.tersesystems.echopraxia.logback.LoggerFieldConverter"/>
    
    <root>
        <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>
                    %-4relative [%thread] %-5level [%fields{$.book}] [%loggerctx{$.book}] [%argctx{$.book}] %logger - %msg%n
                </pattern>
            </encoder>
        </appender>
    </root>
</configuration>
```

