# Benchmarks

All benchmarks were run on a Dell XPS 15 running Linux, using Amazon Corretto 11.0.9.

```
OpenJDK 64-Bit Server VM Corretto-11.0.9.11.1 (build 11.0.9+11-LTS, mixed mode)
```

Implementations are set up with a null / no-op appender, so only the time going through the API and the implementation. 

## Logstash Implementation

Uses logback 1.2.10 and logstash-logback-encoder 7.0.1.

`LoggerBenchmarks` shows the main `Logger` API.  

`CoreLoggerBenchmarks` shows the CoreLogger SPI.  

`SLF4JLoggerBenchmarks` show the SLF4J API being called directly for comparison.

```
Benchmark                                         Mode  Cnt    Score    Error  Units
CoreLoggerBenchmarks.info                         avgt    5   51.434 ±  1.222  ns/op
CoreLoggerBenchmarks.infoWithContext              avgt    5  142.198 ±  7.249  ns/op
CoreLoggerBenchmarks.infoWithException            avgt    5  165.357 ±  3.833  ns/op
CoreLoggerBenchmarks.infoWithParameterizedString  avgt    5   74.048 ±  0.583  ns/op
CoreLoggerBenchmarks.isEnabled                    avgt    5    4.181 ±  0.047  ns/op
LoggerBenchmarks.info                             avgt    5   50.968 ±  1.053  ns/op
LoggerBenchmarks.infoWithAlways                   avgt    5   52.356 ±  3.655  ns/op
LoggerBenchmarks.infoWithContextString            avgt    5  151.646 ±  5.972  ns/op
LoggerBenchmarks.infoWithErrorCondition           avgt    5    4.965 ±  0.452  ns/op
LoggerBenchmarks.infoWithException                avgt    5  183.519 ± 98.789  ns/op
LoggerBenchmarks.infoWithFieldBuilder             avgt    5   55.997 ±  5.033  ns/op
LoggerBenchmarks.infoWithNever                    avgt    5    0.520 ±  0.010  ns/op
LoggerBenchmarks.infoWithParameterizedString      avgt    5   76.596 ±  7.575  ns/op
LoggerBenchmarks.infoWithStringArg                avgt    5   77.015 ±  3.494  ns/op
LoggerBenchmarks.isInfoEnabled                    avgt    5    5.350 ±  0.157  ns/op
LoggerBenchmarks.traceWithParameterizedString     avgt    5    3.339 ±  0.025  ns/op
SLF4JLoggerBenchmarks.info                        avgt    5   47.127 ±  1.358  ns/op
SLF4JLoggerBenchmarks.infoWithArgument            avgt    5   53.454 ±  1.314  ns/op
SLF4JLoggerBenchmarks.infoWithArrayArgs           avgt    5   68.225 ±  0.674  ns/op
SLF4JLoggerBenchmarks.infoWithException           avgt    5  166.585 ±  1.972  ns/op
SLF4JLoggerBenchmarks.isInfoEnabled               avgt    5    2.824 ±  0.012  ns/op
```

## Log4J Implementation

Uses Log4J 2.17.1 with layout-template-json.

`LoggerBenchmarks` shows the main `Logger` API.

`CoreLoggerBenchmarks` shows the CoreLogger SPI.

`SLF4JLoggerBenchmarks` show the Log4J API being called directly for comparison.

```
Benchmark                                         Mode  Cnt    Score    Error  Units
CoreLoggerBenchmarks.info                         avgt    5  198.648 ± 33.391  ns/op
CoreLoggerBenchmarks.infoWithException            avgt    5  199.548 ± 32.006  ns/op
CoreLoggerBenchmarks.infoWithParameterizedString  avgt    5  356.200 ± 60.041  ns/op
CoreLoggerBenchmarks.isEnabled                    avgt    5    6.810 ±  0.389  ns/op
Log4JBenchmarks.info                              avgt    5  175.318 ±  1.622  ns/op
Log4JBenchmarks.infoWithArgument                  avgt    5  169.173 ±  0.762  ns/op
Log4JBenchmarks.infoWithArrayArgs                 avgt    5  174.232 ±  4.343  ns/op
Log4JBenchmarks.infoWithException                 avgt    5  173.121 ±  1.406  ns/op
Log4JBenchmarks.isInfoEnabled                     avgt    5    3.731 ±  0.037  ns/op
LoggerBenchmarks.info                             avgt    5  213.721 ± 12.908  ns/op
LoggerBenchmarks.infoWithAlways                   avgt    5  217.051 ± 11.712  ns/op
LoggerBenchmarks.infoWithContextString            avgt    5  412.614 ±  1.549  ns/op
LoggerBenchmarks.infoWithException                avgt    5  226.292 ±  7.383  ns/op
LoggerBenchmarks.infoWithFieldBuilder             avgt    5  218.845 ±  6.774  ns/op
LoggerBenchmarks.infoWithNever                    avgt    5   36.835 ±  0.431  ns/op
LoggerBenchmarks.infoWithParameterizedString      avgt    5  380.021 ± 43.017  ns/op
LoggerBenchmarks.infoWithStringArg                avgt    5  287.335 ± 53.580  ns/op
LoggerBenchmarks.isInfoEnabled                    avgt    5    7.747 ±  0.249  ns/op
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