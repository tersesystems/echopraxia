# Benchmarks

All benchmarks were run on a Dell XPS 15 running Linux, using Amazon Corretto 11.0.9.

```
OpenJDK 64-Bit Server VM Corretto-11.0.9.11.1 (build 11.0.9+11-LTS, mixed mode)
```

Implementations are set up with a null / no-op appender, so only the time going through the API and the implementation. 

**NOTE**: when running benchmarks, please ensure you are dealing with an appropriately cooled system!  Benchmarks become surprisingly inaccurate when your computer is hot enough, as the CPU will turn on thermal throttling to manage excess temperatures.

## Logstash Implementation

Uses logback 1.2.10 and logstash-logback-encoder 7.0.1.

`LoggerBenchmarks` shows the main `Logger` API.  

```
Benchmark                                         Mode  Cnt    Score    Error  Units
CoreLoggerBenchmarks.info                         avgt    5   56.128 ±  0.980  ns/op
CoreLoggerBenchmarks.infoWithContext              avgt    5  155.291 ±  6.272  ns/op
CoreLoggerBenchmarks.infoWithException            avgt    5  280.680 ±  7.445  ns/op
CoreLoggerBenchmarks.infoWithParameterizedString  avgt    5  137.542 ±  1.517  ns/op
CoreLoggerBenchmarks.isEnabled                    avgt    5    8.436 ±  0.070  ns/op
JsonPathBenchmarks.testPathConditionFail          avgt    5  456.893 ± 12.185  ns/op
JsonPathBenchmarks.testPathConditionPass          avgt    5  524.633 ± 78.791  ns/op
JsonPathBenchmarks.testStreamConditionFail        avgt    5   64.805 ± 11.530  ns/op
JsonPathBenchmarks.testStreamConditionPass        avgt    5   71.453 ±  0.832  ns/op
LoggerBenchmarks.info                             avgt    5   66.369 ±  7.402  ns/op
LoggerBenchmarks.infoWithAlways                   avgt    5   64.321 ±  0.629  ns/op
LoggerBenchmarks.infoWithContextString            avgt    5  173.154 ± 33.208  ns/op
LoggerBenchmarks.infoWithErrorCondition           avgt    5    7.473 ±  0.278  ns/op
LoggerBenchmarks.infoWithException                avgt    5  275.581 ±  3.196  ns/op
LoggerBenchmarks.infoWithFieldBuilder             avgt    5   66.661 ±  3.907  ns/op
LoggerBenchmarks.infoWithNever                    avgt    5    0.717 ±  0.017  ns/op
LoggerBenchmarks.infoWithParameterizedString      avgt    5  138.944 ± 13.325  ns/op
LoggerBenchmarks.infoWithStringArg                avgt    5  139.971 ±  9.339  ns/op
LoggerBenchmarks.isInfoEnabled                    avgt    5    8.381 ±  0.048  ns/op
LoggerBenchmarks.traceWithParameterizedString     avgt    5   33.058 ±  4.906  ns/op
SLF4JLoggerBenchmarks.info                        avgt    5   57.610 ±  0.855  ns/op
SLF4JLoggerBenchmarks.infoWithArgument            avgt    5   69.459 ±  0.836  ns/op
SLF4JLoggerBenchmarks.infoWithArrayArgs           avgt    5   89.485 ±  0.856  ns/op
SLF4JLoggerBenchmarks.infoWithException           avgt    5  210.398 ±  3.741  ns/op
SLF4JLoggerBenchmarks.isInfoEnabled               avgt    5    4.173 ±  0.125  ns/op

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

The scripting implementation uses Tweakflow, and is turned into an abstract syntax tree when compiled.  

Here's the simplest boolean condition:

```
library echopraxia {
  function evaluate: (string level, dict ctx) ->
     true;
}
```

The runtime cost for evaluating the simplest "boolean condition" is around 670 +/- 109 nanoseconds, which comes mainly from setting up a function map to `findBoolean`, `findString` etc.

Here's the full benchmarks:

```
Benchmark                                      Mode  Cnt     Score     Error  Units
ScriptingBenchmarks.testBooleanConditionMatch  avgt    5   670.523 ± 109.664  ns/op
ScriptingBenchmarks.testFileConditionFail      avgt    5  1467.818 ± 155.201  ns/op
ScriptingBenchmarks.testFileConditionMatch     avgt    5  1390.808 ± 126.744  ns/op
ScriptingBenchmarks.testInfoConditionMatch     avgt    5   808.190 ±  83.414  ns/op
ScriptingBenchmarks.testStringConditionFail    avgt    5  1590.013 ± 152.896  ns/op
ScriptingBenchmarks.testStringConditionMatch   avgt    5  1581.162 ±  46.193  ns/op
ScriptingBenchmarks.testWatchedConditionFail   avgt    5  1657.719 ± 177.949  ns/op
ScriptingBenchmarks.testWatchedConditionMatch  avgt    5  1836.656 ± 205.378  ns/op
```
