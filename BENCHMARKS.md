# Benchmarks

All benchmarks were run on a Dell XPS 15 running Linux, using Amazon Corretto 11.0.9.

```
OpenJDK 64-Bit Server VM Corretto-11.0.9.11.1 (build 11.0.9+11-LTS, mixed mode)
```

Implementations are set up with a null / no-op appender, so only the time going through the API and the implementation. 

**NOTE**: when running benchmarks, please ensure you are dealing with an appropriately cooled system!  Benchmarks become surprisingly inaccurate when your computer is hot enough, as the CPU will turn on thermal throttling to manage excess temperatures.

## Logstash Implementation

Uses logback 1.2.12 and logstash-logback-encoder 7.1.1.

`LoggerBenchmarks` shows the main `Logger` API.  

The core logger resolves arguments only if an `isEnabled(marker, level)` check is passed.  All arguments are passed into the condition, and if the condition passes then the logger sends it to various appenders.

```
Benchmark                                         Mode  Cnt    Score    Error  Units
CoreLoggerBenchmarks.info                         avgt    5   50.958 ±  0.133  ns/op
CoreLoggerBenchmarks.infoWithContext              avgt    5  128.230 ±  1.063  ns/op
CoreLoggerBenchmarks.infoWithException            avgt    5  239.123 ±  4.404  ns/op
CoreLoggerBenchmarks.infoWithParameterizedString  avgt    5  100.529 ±  0.638  ns/op
CoreLoggerBenchmarks.isEnabled                    avgt    5    6.089 ±  0.065  ns/op
JsonPathBenchmarks.testPathConditionFail          avgt    5  408.728 ± 36.893  ns/op
JsonPathBenchmarks.testPathConditionPass          avgt    5  436.639 ± 56.149  ns/op
JsonPathBenchmarks.testStreamConditionFail        avgt    5   53.560 ±  3.956  ns/op
JsonPathBenchmarks.testStreamConditionPass        avgt    5   66.593 ±  8.432  ns/op
LoggerBenchmarks.info                             avgt    5   58.448 ±  0.471  ns/op
LoggerBenchmarks.infoWithAlways                   avgt    5   58.283 ±  0.437  ns/op
LoggerBenchmarks.infoWithContextString            avgt    5  153.189 ±  6.785  ns/op
LoggerBenchmarks.infoWithErrorCondition           avgt    5    6.349 ±  0.857  ns/op
LoggerBenchmarks.infoWithException                avgt    5  286.119 ±  3.218  ns/op
LoggerBenchmarks.infoWithFieldBuilder             avgt    5   60.912 ±  5.781  ns/op
LoggerBenchmarks.infoWithNever                    avgt    5    0.590 ±  0.015  ns/op
LoggerBenchmarks.infoWithParameterizedString      avgt    5  121.430 ±  0.912  ns/op
LoggerBenchmarks.infoWithStringArg                avgt    5  128.637 ±  0.793  ns/op
LoggerBenchmarks.isInfoEnabled                    avgt    5    7.715 ±  0.060  ns/op
LoggerBenchmarks.traceWithParameterizedString     avgt    5    5.068 ±  0.328  ns/op
SLF4JLoggerBenchmarks.info                        avgt    5   52.870 ±  0.263  ns/op
SLF4JLoggerBenchmarks.infoWithArgument            avgt    5   66.311 ±  4.312  ns/op
SLF4JLoggerBenchmarks.infoWithArrayArgs           avgt    5   81.676 ±  0.429  ns/op
SLF4JLoggerBenchmarks.infoWithException           avgt    5  236.811 ±  0.590  ns/op
SLF4JLoggerBenchmarks.isInfoEnabled               avgt    5    3.407 ±  0.229  ns/op
```

Benchmark                                             Mode  Cnt    Score    Error  Units
LoggerBenchmarks.info                                 avgt    5   56.929 ±  0.950  ns/op
LoggerBenchmarks.info:·async                          avgt           NaN             ---
LoggerBenchmarks.infoWithAlways                       avgt    5   54.330 ±  1.807  ns/op
LoggerBenchmarks.infoWithAlways:·async                avgt           NaN             ---
LoggerBenchmarks.infoWithContextChain                 avgt    5  238.810 ± 39.113  ns/op
LoggerBenchmarks.infoWithContextChain:·async          avgt           NaN             ---
LoggerBenchmarks.infoWithContextString                avgt    5   61.978 ±  0.351  ns/op
LoggerBenchmarks.infoWithContextString:·async         avgt           NaN             ---
LoggerBenchmarks.infoWithErrorCondition               avgt    5    4.480 ±  0.506  ns/op
LoggerBenchmarks.infoWithErrorCondition:·async        avgt           NaN             ---
LoggerBenchmarks.infoWithException                    avgt    5  294.817 ± 33.051  ns/op
LoggerBenchmarks.infoWithException:·async             avgt           NaN             ---
LoggerBenchmarks.infoWithFieldBuilder                 avgt    5   61.851 ±  0.174  ns/op
LoggerBenchmarks.infoWithFieldBuilder:·async          avgt           NaN             ---
LoggerBenchmarks.infoWithNever                        avgt    5    0.426 ±  0.009  ns/op
LoggerBenchmarks.infoWithNever:·async                 avgt           NaN             ---
LoggerBenchmarks.infoWithParameterizedString          avgt    5  122.649 ± 16.556  ns/op
LoggerBenchmarks.infoWithParameterizedString:·async   avgt           NaN             ---
LoggerBenchmarks.infoWithStringArg                    avgt    5  122.520 ± 12.063  ns/op
LoggerBenchmarks.infoWithStringArg:·async             avgt           NaN             ---
LoggerBenchmarks.isInfoEnabled                        avgt    5    3.974 ±  0.899  ns/op
LoggerBenchmarks.isInfoEnabled:·async                 avgt           NaN             ---
LoggerBenchmarks.trace                                avgt    5    3.425 ±  0.184  ns/op
LoggerBenchmarks.trace:·async                         avgt           NaN             ---
LoggerBenchmarks.traceWithContextChain                avgt    5   58.407 ±  8.117  ns/op
LoggerBenchmarks.traceWithContextChain:·async         avgt           NaN             ---
LoggerBenchmarks.traceWithParameterizedString         avgt    5    3.477 ±  0.051  ns/op
LoggerBenchmarks.traceWithParameterizedString:·async  avgt           NaN             ---



`CoreLoggerBenchmarks` shows the CoreLogger SPI.  

`SLF4JLoggerBenchmarks` show the SLF4J API being called directly for comparison -- what you'd see if you were using straight Logback.

`JsonPathBenchmarks` measures the JSON Path lookups.

## Log4J Implementation

Uses Log4J 2.17.1 with layout-template-json.

`LoggerBenchmarks` shows the main `Logger` API.

Note that Log4J has multiple options for `isEnabled` that include passing the message and exception through filters.

Echopraxia does *not* do this, because it requires resolving all arguments.  Instead, the core logger only calls `isEnabled(marker, level)` and then resolves arguments when testing the condition.

```
Benchmark                                         Mode  Cnt    Score    Error  Units
CoreLoggerBenchmarks.info                         avgt    5  161.837 ± 42.936  ns/op
CoreLoggerBenchmarks.infoWithException            avgt    5  235.812 ±  1.514  ns/op
CoreLoggerBenchmarks.infoWithParameterizedString  avgt    5  339.815 ±  2.867  ns/op
CoreLoggerBenchmarks.isEnabled                    avgt    5    5.537 ±  0.133  ns/op
Log4JBenchmarks.info                              avgt    5  154.929 ±  1.063  ns/op
Log4JBenchmarks.infoWithArgument                  avgt    5  150.435 ±  1.593  ns/op
Log4JBenchmarks.infoWithArrayArgs                 avgt    5  151.563 ±  0.473  ns/op
Log4JBenchmarks.infoWithException                 avgt    5  155.602 ±  2.264  ns/op
Log4JBenchmarks.isInfoEnabled                     avgt    5    3.073 ±  0.031  ns/op
LoggerBenchmarks.info                             avgt    5  173.613 ± 45.365  ns/op
LoggerBenchmarks.infoWithAlways                   avgt    5  166.468 ± 45.758  ns/op
LoggerBenchmarks.infoWithContextString            avgt    5  170.711 ± 47.910  ns/op
LoggerBenchmarks.infoWithErrorCondition           avgt    5    6.629 ±  0.136  ns/op
LoggerBenchmarks.infoWithException                avgt    5  256.339 ±  7.894  ns/op
LoggerBenchmarks.infoWithFieldBuilder             avgt    5  184.510 ± 55.733  ns/op
LoggerBenchmarks.infoWithNever                    avgt    5    0.551 ±  0.006  ns/op
LoggerBenchmarks.infoWithParameterizedString      avgt    5  396.134 ±  2.202  ns/op
LoggerBenchmarks.infoWithStringArg                avgt    5  289.114 ±  1.598  ns/op
LoggerBenchmarks.isInfoEnabled                    avgt    5    6.534 ±  0.498  ns/op
LoggerBenchmarks.traceWithParameterizedString     avgt    5    5.683 ±  0.054  ns/op
```

`CoreLoggerBenchmarks` shows the CoreLogger SPI.

`Log4JBenchmarks` show the Log4J API being called directly for comparison.

## Fluent Logger

The Fluent Logger uses the same configuration as above.  It is slower on average because object instantiation has some built-in costs, and takes 20 nanoseconds as a [baseline](https://shipilev.net/jvm/anatomy-quarks/6-new-object-stages/).

Using Logback:

```
Benchmark                                 Mode  Cnt    Score    Error  Units
FluentBenchmarks.info                     avgt    5  100.128 ± 10.407  ns/op
FluentBenchmarks.infoWithArgAndException  avgt    5  477.786 ± 15.277  ns/op
FluentBenchmarks.infoWithArgument         avgt    5  172.235 ±  9.508  ns/op
FluentBenchmarks.infoWithException        avgt    5  303.638 ±  6.435  ns/op
```

## Semantic Logger

The semantic logger configuration is the same as above.  There is only one kind of call you can make.

```
Benchmark                               Mode  Cnt    Score   Error  Units
SemanticLoggerBenchmarks.info           avgt    5  108.967 ± 3.063  ns/op
SemanticLoggerBenchmarks.isInfoEnabled  avgt    5    6.337 ± 0.160  ns/op
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
