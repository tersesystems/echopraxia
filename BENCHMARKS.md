# Benchmarks

All benchmarks were run on a Dell XPS 15 running Linux, using Amazon Corretto 11.0.9.

```
OpenJDK 64-Bit Server VM Corretto-11.0.9.11.1 (build 11.0.9+11-LTS, mixed mode)
```

Implementations are set up with a null / no-op appender, so only the time going through the API and the implementation. 

## Logstash Implementation

Uses logback 1.2.10 and logstash-logback-encoder 7.0.1.

`LoggerBenchmarks` shows the main `Logger` API.  

```
Benchmark                                         Mode  Cnt    Score    Error  Units
LoggerBenchmarks.info                             avgt    5   54.470 ±  4.644  ns/op
LoggerBenchmarks.infoWithAlways                   avgt    5   53.655 ±  3.365  ns/op
LoggerBenchmarks.infoWithContextString            avgt    5  150.495 ±  1.702  ns/op
LoggerBenchmarks.infoWithErrorCondition           avgt    5    4.880 ±  0.020  ns/op
LoggerBenchmarks.infoWithException                avgt    5  174.080 ± 14.744  ns/op
LoggerBenchmarks.infoWithFieldBuilder             avgt    5   53.058 ±  3.091  ns/op
LoggerBenchmarks.infoWithNever                    avgt    5    0.500 ±  0.004  ns/op
LoggerBenchmarks.infoWithParameterizedString      avgt    5   76.599 ±  0.670  ns/op
LoggerBenchmarks.infoWithStringArg                avgt    5   76.026 ±  2.674  ns/op
LoggerBenchmarks.isInfoEnabled                    avgt    5    5.329 ±  0.033  ns/op
LoggerBenchmarks.traceWithParameterizedString     avgt    5    3.505 ±  0.037  ns/op
```

`CoreLoggerBenchmarks` shows the CoreLogger SPI.  

```
CoreLoggerBenchmarks.info                         avgt    5   49.824 ±  0.361  ns/op
CoreLoggerBenchmarks.infoWithContext              avgt    5  149.689 ± 11.909  ns/op
CoreLoggerBenchmarks.infoWithException            avgt    5  155.725 ±  9.717  ns/op
CoreLoggerBenchmarks.infoWithParameterizedString  avgt    5   75.807 ±  0.115  ns/op
CoreLoggerBenchmarks.isEnabled                    avgt    5    4.324 ±  0.025  ns/op
```

`SLF4JLoggerBenchmarks` show the SLF4J API being called directly for comparison.

```
SLF4JLoggerBenchmarks.info                        avgt    5   47.712 ±  0.289  ns/op
SLF4JLoggerBenchmarks.infoWithArgument            avgt    5   54.529 ±  0.399  ns/op
SLF4JLoggerBenchmarks.infoWithArrayArgs           avgt    5   71.651 ±  1.938  ns/op
SLF4JLoggerBenchmarks.infoWithException           avgt    5  168.201 ±  1.559  ns/op
SLF4JLoggerBenchmarks.isInfoEnabled               avgt    5    3.055 ±  0.040  ns/op
```

## Log4J Implementation

Uses Log4J 2.17.1 with layout-template-json.

`LoggerBenchmarks` shows the main `Logger` API.

```
Benchmark                                         Mode  Cnt    Score    Error  Units
LoggerBenchmarks.info                             avgt    5  215.260 ± 47.948  ns/op
LoggerBenchmarks.infoWithAlways                   avgt    5  203.128 ± 43.440  ns/op
LoggerBenchmarks.infoWithContextString            avgt    5  340.848 ±  1.354  ns/op
LoggerBenchmarks.infoWithErrorCondition           avgt    5   30.136 ±  0.544  ns/op
LoggerBenchmarks.infoWithException                avgt    5  217.102 ± 43.788  ns/op
LoggerBenchmarks.infoWithFieldBuilder             avgt    5  204.477 ± 50.422  ns/op
LoggerBenchmarks.infoWithNever                    avgt    5    0.514 ±  0.002  ns/op
LoggerBenchmarks.infoWithParameterizedString      avgt    5  436.302 ±  2.945  ns/op
LoggerBenchmarks.infoWithStringArg                avgt    5  273.925 ± 12.845  ns/op
LoggerBenchmarks.isInfoEnabled                    avgt    5    6.308 ±  0.118  ns/op
LoggerBenchmarks.traceWithParameterizedString     avgt    5  218.015 ±  1.255  ns/op
```

`CoreLoggerBenchmarks` shows the CoreLogger SPI.

```
CoreLoggerBenchmarks.info                         avgt    5  187.063 ± 39.891  ns/op
CoreLoggerBenchmarks.infoWithException            avgt    5  186.978 ± 56.031  ns/op
CoreLoggerBenchmarks.infoWithParameterizedString  avgt    5  420.514 ±  8.937  ns/op
CoreLoggerBenchmarks.isEnabled                    avgt    5    5.791 ±  0.013  ns/op
```

`Log4JBenchmarks` show the Log4J API being called directly for comparison.

```
Log4JBenchmarks.info                              avgt    5  160.233 ±  3.984  ns/op
Log4JBenchmarks.infoWithArgument                  avgt    5  151.566 ±  1.011  ns/op
Log4JBenchmarks.infoWithArrayArgs                 avgt    5  155.833 ±  1.308  ns/op
Log4JBenchmarks.infoWithException                 avgt    5  149.460 ±  1.868  ns/op
Log4JBenchmarks.isInfoEnabled                     avgt    5    3.083 ±  0.066  ns/op
```

## Fluent Logger

The Fluent Logger uses the same configuration as above.  It is slower on average because object instantiation has some built-in costs, and takes 20 nanoseconds as a [baseline](https://shipilev.net/jvm/anatomy-quarks/6-new-object-stages/).

Using Logback:

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

Using Logback:

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

## Scripting

Scripts can be read from file system or directly from memory.

```java
public class ScriptingBenchmarks {
    private static final Path path = Paths.get("src/jmh/tweakflow/condition.tf");

    private static final Path watchedDir = Paths.get("src/jmh/tweakflow");

    public static String buildScript() {
        StringBuilder b = new StringBuilder("");
        b.append("library echopraxia {");
        b.append("  function evaluate: (string level, dict fields) ->");
        b.append("    level == \"INFO\";");
        b.append("}");
        return b.toString();
    }

    private static final Condition fileCondition =
            ScriptCondition.create(false, path, Throwable::printStackTrace);

    private static final Condition stringCondition =
            ScriptCondition.create(false, buildScript(), Throwable::printStackTrace);

    private static final ScriptWatchService scriptWatchService = new ScriptWatchService(watchedDir);

    private static final ScriptHandle watchedScript =
            scriptWatchService.watchScript(
                    watchedDir.resolve("condition.tf"), Throwable::printStackTrace);

    private static final Condition watchedCondition = ScriptCondition.create(false, watchedScript);

    @Benchmark
    public void testFileConditionMatch(Blackhole blackhole) {
        // ScriptingBenchmarks.testFileConditionMatch     avgt    5  127.251 ± 0.816  ns/op
        blackhole.consume(fileCondition.test(Level.INFO, LogstashLoggingContext.empty()));
    }

    @Benchmark
    public void testStringConditionMatch(Blackhole blackhole) {
        // ScriptingBenchmarks.testStringConditionMatch   avgt    5  122.905 ± 3.440  ns/op
        blackhole.consume(stringCondition.test(Level.INFO, LogstashLoggingContext.empty()));
    }

    @Benchmark
    public void testFileConditionFail(Blackhole blackhole) {
        // ScriptingBenchmarks.testFileConditionFail      avgt    5  112.629 ± 2.951  ns/op
        blackhole.consume(fileCondition.test(Level.DEBUG, LogstashLoggingContext.empty()));
    }

    @Benchmark
    public void testStringConditionFail(Blackhole blackhole) {
        // ScriptingBenchmarks.testStringConditionFail    avgt    5  118.323 ± 4.296  ns/op
        blackhole.consume(stringCondition.test(Level.DEBUG, LogstashLoggingContext.empty()));
    }

    @Benchmark
    public void testWatchedConditionMatch(Blackhole blackhole) {
        // ScriptingBenchmarks.testWatchedConditionMatch  avgt    5  134.601 ± 2.325  ns/op
        blackhole.consume(watchedCondition.test(Level.INFO, LogstashLoggingContext.empty()));
    }

    @Benchmark
    public void testWatchedConditionFail(Blackhole blackhole) {
        // ScriptingBenchmarks.testWatchedConditionFail   avgt    5  125.652 ± 5.286  ns/op
        blackhole.consume(watchedCondition.test(Level.DEBUG, LogstashLoggingContext.empty()));
    }
}
```

where the `condition.tf` script is 

```
library echopraxia {
  function evaluate: (string level, dict fields) ->
     level == "INFO";
}
```