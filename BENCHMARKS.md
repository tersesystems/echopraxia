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
CoreLoggerBenchmarks.info                         avgt    5   46.427 ±  0.317  ns/op
CoreLoggerBenchmarks.infoWithException            avgt    5  163.281 ±  5.510  ns/op
CoreLoggerBenchmarks.infoWithParameterizedString  avgt    5   66.173 ±  0.588  ns/op
CoreLoggerBenchmarks.isEnabled                    avgt    5    4.661 ±  0.033  ns/op
LoggerBenchmarks.info                             avgt    5   46.382 ±  0.995  ns/op
LoggerBenchmarks.infoWithAlways                   avgt    5   46.426 ±  0.894  ns/op
LoggerBenchmarks.infoWithContextString            avgt    5  209.072 ±  2.842  ns/op
LoggerBenchmarks.infoWithErrorCondition           avgt    5    2.417 ±  0.018  ns/op
LoggerBenchmarks.infoWithException                avgt    5  150.731 ±  1.923  ns/op
LoggerBenchmarks.infoWithFieldBuilder             avgt    5   47.481 ±  0.239  ns/op
LoggerBenchmarks.infoWithNever                    avgt    5    0.523 ±  0.005  ns/op
LoggerBenchmarks.infoWithParameterizedString      avgt    5   75.764 ±  0.940  ns/op
LoggerBenchmarks.infoWithStringArg                avgt    5   72.751 ±  0.633  ns/op
LoggerBenchmarks.isInfoEnabled                    avgt    5    6.091 ±  0.093  ns/op
LoggerBenchmarks.traceWithParameterizedString     avgt    5    5.049 ±  0.134  ns/op
SLF4JLoggerBenchmarks.info                        avgt    5   47.214 ±  2.359  ns/op
SLF4JLoggerBenchmarks.infoWithArgument            avgt    5   54.518 ±  0.723  ns/op
SLF4JLoggerBenchmarks.infoWithArrayArgs           avgt    5   75.887 ±  1.005  ns/op
SLF4JLoggerBenchmarks.infoWithException           avgt    5  166.074 ± 21.561  ns/op
SLF4JLoggerBenchmarks.isInfoEnabled               avgt    5    3.135 ±  0.024  ns/op
```

## Log4J Implementation

Uses Log4J 2.17.1 with layout-template-json.

`LoggerBenchmarks` shows the main `Logger` API.

`CoreLoggerBenchmarks` shows the CoreLogger SPI.

`SLF4JLoggerBenchmarks` show the Log4J API being called directly for comparison.

```
Benchmark                                         Mode  Cnt    Score     Error  Units
CoreLoggerBenchmarks.info                         avgt    5  234.448 ±  51.178  ns/op
CoreLoggerBenchmarks.infoWithException            avgt    5  231.395 ±  45.475  ns/op
CoreLoggerBenchmarks.infoWithParameterizedString  avgt    5  317.741 ±  54.581  ns/op
CoreLoggerBenchmarks.isEnabled                    avgt    5    4.917 ±   0.039  ns/op
Log4JBenchmarks.info                              avgt    5  187.168 ±   6.510  ns/op
Log4JBenchmarks.infoWithArgument                  avgt    5  397.714 ±   6.262  ns/op
Log4JBenchmarks.infoWithArrayArgs                 avgt    5  940.645 ± 166.913  ns/op
Log4JBenchmarks.infoWithException                 avgt    5  214.765 ±  45.950  ns/op
Log4JBenchmarks.isInfoEnabled                     avgt    5    3.105 ±   0.457  ns/op
LoggerBenchmarks.info                             avgt    5  226.581 ±  91.389  ns/op
LoggerBenchmarks.infoWithAlways                   avgt    5  252.319 ±  77.834  ns/op
LoggerBenchmarks.infoWithContextString            avgt    5  335.941 ±  13.313  ns/op
LoggerBenchmarks.infoWithException                avgt    5  220.949 ±  61.092  ns/op
LoggerBenchmarks.infoWithFieldBuilder             avgt    5  254.228 ±  50.595  ns/op
LoggerBenchmarks.infoWithNever                    avgt    5    4.867 ±   0.033  ns/op
LoggerBenchmarks.infoWithParameterizedString      avgt    5  379.208 ±  43.844  ns/op
LoggerBenchmarks.infoWithStringArg                avgt    5  250.326 ±  43.937  ns/op
LoggerBenchmarks.isInfoEnabled                    avgt    5    6.167 ±   0.036  ns/op
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