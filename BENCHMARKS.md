# Benchmarks

All benchmarks were run on a Dell XPS 15 running Linux, using Amazon Corretto 11.0.9.

```
OpenJDK 64-Bit Server VM Corretto-11.0.9.11.1 (build 11.0.9+11-LTS, mixed mode)
```

## SLF4J Logger

As a baseline, here are the benchmarks for a straight SLF4J logger.  

The average time is in comments.  For example, the baseline CPU time for a `logger.info("message")` is around 49 nanoseconds per operation, plus or minus 3 nanoseconds.

```java
public class SLF4JLoggerBenchmarks {
  private static final Logger logger =
      org.slf4j.LoggerFactory.getLogger(SLF4JLoggerBenchmarks.class);

  private static final Exception exception = new RuntimeException();

  @Benchmark
  public void info() {
    // SLF4JLoggerBenchmarks.info                    avgt    5   49.033 ± 3.101  ns/op
    logger.info("message");
  }

  @Benchmark
  public void infoWithArgument() {
    // SLF4JLoggerBenchmarks.infoWithArgument        avgt    5   49.749 ± 2.394  ns/op
    logger.info("message {}", "string");
  }

  @Benchmark
  public void infoWithArrayArgs() {
    // SLF4JLoggerBenchmarks.infoWithArrayArgs       avgt    5   51.215 ± 2.638  ns/op
    logger.info("message {} {} {} {}", "one", "two", "three", "four");
  }

  @Benchmark
  public void infoWithException() {
    // SLF4JLoggerBenchmarks.infoWithException       avgt    5  175.896 ± 0.807  ns/op
    logger.info("Message", exception);
  }
}
```

## Logstash Core Logger

The Logstash core logger wraps an SLF4J logger, backed by logback, and uses Logstash markers and structured arguments for fields.  This is not exposed directly, but is used internally.

Here, the `logback.xml` uses a no-op appender as follows:

```xml
<configuration>
    <statusListener class="ch.qos.logback.core.status.NopStatusListener"/>

    <appender name="NOP" class="ch.qos.logback.core.helpers.NOPAppender">
    </appender>

    <root level="DEBUG">
        <appender-ref ref="NOP" />
    </root>
</configuration>
```

The benchmarks are as follows:

```java
public class CoreLoggerBenchmarks {
  private static final CoreLogger logger = CoreLoggerFactory.getLogger();
  private static final Exception exception = new RuntimeException();
  private static final Field.Builder builder = Field.Builder.instance();

  @Benchmark
  public void info() {
    // CoreLoggerBenchmarks.info                     avgt    5   45.618 ± 0.965  ns/op
    logger.log(Level.INFO, "Message");
  }

  @Benchmark
  public void infoWithParameterizedString() {
    // CoreLoggerBenchmarks.infoWithParameterizedString  avgt    5   76.189 ± 2.388  ns/op
    logger.log(Level.INFO, "Message {}", fb -> fb.onlyString("foo", "bar"), builder);
  }

  @Benchmark
  public void infoWithException() {
    // CoreLoggerBenchmarks.infoWithException            avgt    5  174.242 ± 7.510  ns/op
    logger.log(Level.INFO, "Message", exception);
  }
}
```

## Logger 

This is the main public facing Logger API.  It uses a core logger internally, and has the same `logback.xml` configuration as the core logger.

```java
public class LoggerBenchmarks {
  private static final Logger<?> logger = LoggerFactory.getLogger();
  private static final Exception exception = new RuntimeException();

  @Benchmark
  public void info() {
    // LoggerBenchmarks.info                         avgt    5   47.464 ± 0.414  ns/op
    logger.info("Message");
  }

  @Benchmark
  public void infoWithStringArg() {
    // LoggerBenchmarks.infoWithStringArg            avgt    5   75.672 ± 0.749  ns/op
    logger.info("Message", fb -> fb.onlyString("foo", "bar"));
  }

  @Benchmark
  public void infoWithContextString() {
    // LoggerBenchmarks.infoWithContextString        avgt    5  116.451 ± 6.000  ns/op
    logger.withFields(fb -> fb.onlyString("foo", "bar")).info("Message");
  }

  @Benchmark
  public void infoWithParameterizedString() {
    // LoggerBenchmarks.infoWithParameterizedString  avgt    5   75.732 ± 0.421  ns/op
    logger.info("Message {}", fb -> fb.onlyString("foo", "bar"));
  }

  @Benchmark
  public void infoWithException() {
    // LoggerBenchmarks.infoWithException            avgt    5  175.756 ± 1.843  ns/op
    logger.info("Message", exception);
  }

  @Benchmark
  public void infoWithNever() {
    // LoggerBenchmarks.infoWithNever                avgt    5   11.358 ± 0.165  ns/op
    logger.withCondition(Condition.never()).info("Message");
  }

  @Benchmark
  public void infoWithAlways() {
    // LoggerBenchmarks.infoWithAlways               avgt    5   57.670 ± 0.318  ns/op
    logger.withCondition(Condition.always()).info("Message");
  }

  @Benchmark
  public void infoWithFieldBuilder() {
    // LoggerBenchmarks.infoWithFieldBuilder         avgt    5   51.013 ± 0.436  ns/op
    logger.withFieldBuilder(Field.Builder.instance()).info("Message");
  }
}
```

## Fluent Logger

The Fluent Logger uses the same configuration as above.  It is slower on average because object instantiation has some built-in costs, and takes 20 nanoseconds as a [baseline](https://shipilev.net/jvm/anatomy-quarks/6-new-object-stages/).

```java
public class FluentBenchmarks {
  private static final FluentLogger<?> logger = FluentLoggerFactory.getLogger();
  private static final Exception exception = new RuntimeException();

  @Benchmark
  public void info() {
    // FluentBenchmarks.info                     avgt   25  103.478 ±  2.631  ns/op
    logger.atInfo().message("Message").log();
  }

  @Benchmark
  public void infoWithArgument() {
    // FluentBenchmarks.infoWithArgument         avgt   25  196.968 ±  4.884  ns/op
    logger.atInfo().message("Message {}").argument(fb -> fb.string("foo", "bar")).log();
  }

  @Benchmark
  public void infoWithArgAndException() {
    // FluentBenchmarks.infoWithArgAndException  avgt   25  331.651 ± 15.591  ns/op
    logger
        .atInfo()
        .message("Message {}")
        .argument(fb -> fb.string("foo", "bar"))
        .exception(exception)
        .log();
  }

  @Benchmark
  public void infoWithException() {
    // FluentBenchmarks.infoWithException        avgt   25  351.918 ±  7.124  ns/op
    logger.atInfo().message("Message").exception(exception).log();
  }
}
```

## Semantic Logger

The semantic logger configuration is the same as above.  There is only one kind of call you can make.

```java
public class SemanticLoggerBenchmarks {

  private static final SemanticLogger<String> logger =
      SemanticLoggerFactory.getLogger(
          String.class, s -> "Message {}", s -> b -> b.onlyString("name", s));

  @Benchmark
  public void info() {
    // SemanticLoggerBenchmarks.info  avgt   25  97.199 ± 3.323  ns/op
    logger.info("string");
  }
}
```