# Installation

Echopraxia is divided into two sections: the user logger APIs (`simple` and `logger`) and an 
underlying `CoreLogger` implementation which is tied to the logging framework (JUL, Logback, or Log4J2).

You will need to install both a logger module (usually `simple`) and a framework module (usually `logstash` or `logback`).

## User API Loggers

The user API loggers provide slightly different wrappers around the core loggers.  They are both oriented around structured logging, and so will take `Field` arguments, although they do so in slightly different ways.

### Simple

The `simple` module provides an `echopraxia.simple.LoggerFactory` and `echopraxia.simple.Logger`.

Maven:

```xml
<dependency>
  <groupId>com.tersesystems.echopraxia</groupId>
  <artifactId>simple</artifactId>
  <version>VERSION</version>
</dependency>
```

Gradle:

```gradle
implementation "com.tersesystems.echopraxia:simple:<VERSION>" 
```

### Logger

The `logger` module provides `echopraxia.logger.LoggerFactory` and `echopraxia.logger.Logger`.

The `Logger` makes use of field builder functions, which means that a fieldbuilder does not have to be in scope.  Practically speaking, it means you can do this:

```java
logger.debug("Create a field with no fieldbuilder in scope {}", fb -> fb.string("argname", "argvalue"));
```

Where `fb` is a fieldbuilder attached to the logger.  This means that the field is only created if the function is called, which is handy for expensive debug and trace statements, and it's neater because the fieldbuilder is automatically in scope.

Maven:

```xml
<dependency>
  <groupId>com.tersesystems.echopraxia</groupId>
  <artifactId>logger</artifactId>
  <version>VERSION</version>
</dependency>
```

Gradle:

```gradle
implementation "com.tersesystems.echopraxia:logger:<VERSION>" 
```

## Core Loggers

There are core loggers for Logback, Log4J2, and JUL.

### Logstash Core Logger

There is a Logback implementation based around [logstash-logback-encoder](https://github.com/logfellow/logstash-logback-encoder).  This library does not provide a front end logger API, so you must pick (or create) one yourself, i.e. normal, async, fluent, or semantic.  

Maven:

```xml
<dependency>
  <groupId>com.tersesystems.echopraxia</groupId>
  <artifactId>logstash</artifactId>
  <version><VERSION></version>
</dependency>
```

Gradle:

```gradle
implementation "com.tersesystems.echopraxia:logstash:<VERSION>"
```

For SLF4J 2.0.x and for `logstash-logback-encoder`, you will also want the following:

```gradle
implementation "ch.qos.logback:logback-classic:1.5.15"
implementation 'net.logstash.logback:logstash-logback-encoder:8.0'
```

and an appropriate appender:

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <jsonGeneratorDecorator class="net.logstash.logback.decorate.PrettyPrintingJsonGeneratorDecorator"/>
            <providers>
                <timestamp>
                    <timeZone>UTC</timeZone>
                </timestamp>
                <message/>
                <loggerName/>
                <threadName/>
                <logLevel/>
                <stackHash/>
                <mdc/>
                <logstashMarkers/>
                <arguments/>
                <stackTrace/>
            </providers>
        </encoder>
    </appender>
</configuration>
```

### Log4J Core Logger

There is a Log4J implementation that works with the [JSON Template Layout](https://logging.apache.org/log4j/2.x/manual/json-template-layout.html).  This provides a core logger implementation but does not provide a user visible logging API.

Maven:

```xml
<dependency>
  <groupId>com.tersesystems.echopraxia</groupId>
  <artifactId>log4j</artifactId>
  <version><VERSION></version>
</dependency>
```

Gradle:

```gradle
implementation "com.tersesystems.echopraxia:log4j:<VERSION>" 
```

You should explicitly define the Log4J dependencies as well:

```gradle
implementation "org.apache.logging.log4j:log4j-core:$log4j2Version"
implementation "org.apache.logging.log4j:log4j-api:$log4j2Version"
implementation "org.apache.logging.log4j:log4j-layout-template-json:$log4j2Version"
```

You will need to integrate the `com.tersesystems.echopraxia.log4j.layout` package into your `log4j2.xml` file, e.g. by using the `packages` attribute, and add an `EventTemplateAdditionalField` element:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN" packages="com.tersesystems.echopraxia.log4j.layout">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT" follow="true">
            <JsonTemplateLayout eventTemplateUri="classpath:LogstashJsonEventLayoutV1.json">
                <EventTemplateAdditionalField
                        key="fields"
                        format="JSON"
                        value='{"$resolver": "echopraxiaFields"}'/>
            </JsonTemplateLayout>
        </Console>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Console" />
        </Root>
    </Loggers>
</Configuration>
```

If you want to separate the context fields from the argument fields, you can define them separately:

```xml
<JsonTemplateLayout eventTemplateUri="classpath:LogstashJsonEventLayoutV1.json">
    <EventTemplateAdditionalField
            key="arguments"
            format="JSON"
            value='{"$resolver": "echopraxiaArgumentFields"}'/>
    <EventTemplateAdditionalField
            key="context"
            format="JSON"
            value='{"$resolver": "echopraxiaContextFields"}'/>
</JsonTemplateLayout>
```

Unfortunately, I don't know of a way to "flatten" fields so that they show up on the root object instead of under an additional field.  If you know how to do this, let me know!

### JUL (java.util.logging) Core Logger

There is a JUL implementation.

Maven:

```xml
<dependency>
  <groupId>com.tersesystems.echopraxia</groupId>
  <artifactId>jul</artifactId>
  <version><VERSION></version>
</dependency>
```

Gradle:

```gradle
implementation "com.tersesystems.echopraxia:jul:<VERSION>" 
```

You will probably want to configure JUL by calling `logManager.readConfiguration`:

```java
InputStream is;
try {
  is = getClass().getClassLoader().getResourceAsStream("logging.properties");
  LogManager manager = LogManager.getLogManager();
  manager.reset();
  manager.readConfiguration(is);
} finally {
  if (is != null) is.close();
}
```

JSON output is managed using a custom formatter `com.tersesystems.echopraxia.jul.JULJSONFormatter`:

```properties
handlers=java.util.logging.ConsoleHandler,java.util.logging.FileHandler

.level=FINEST

com.tersesystems.echopraxia.jul.JULJSONFormatter.use_slf4j_level_names=true

java.util.logging.FileHandler.formatter=com.tersesystems.echopraxia.jul.JULJSONFormatter
java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter
```

The `use_slf4j_level_names` property will map from JUL's levels to SLF4J, mapping `FINE` and `FINER` to `DEBUG` and `FINEST` to `TRACE`.

JUL's default class/method inference is disabled as it is not useful here and needlessly slows down logging.

