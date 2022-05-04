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

```
Benchmark                                         Mode  Cnt    Score    Error  Units
CoreLoggerBenchmarks.info                         avgt    5   63.815 ± 19.880  ns/op
CoreLoggerBenchmarks.infoWithContext              avgt    5  153.997 ± 17.262  ns/op
CoreLoggerBenchmarks.infoWithException            avgt    5  289.367 ± 27.352  ns/op
CoreLoggerBenchmarks.infoWithParameterizedString  avgt    5  121.523 ± 16.718  ns/op
CoreLoggerBenchmarks.isEnabled                    avgt    5    7.310 ±  0.174  ns/op
JsonPathBenchmarks.testPathConditionFail          avgt    5  450.137 ±  4.849  ns/op
JsonPathBenchmarks.testPathConditionPass          avgt    5  482.654 ± 21.240  ns/op
JsonPathBenchmarks.testStreamConditionFail        avgt    5   52.876 ±  5.958  ns/op
JsonPathBenchmarks.testStreamConditionPass        avgt    5   63.851 ±  9.778  ns/op
LoggerBenchmarks.info                             avgt    5   62.522 ±  1.786  ns/op
LoggerBenchmarks.infoWithAlways                   avgt    5   59.604 ±  7.411  ns/op
LoggerBenchmarks.infoWithContextString            avgt    5  146.456 ±  4.007  ns/op
LoggerBenchmarks.infoWithErrorCondition           avgt    5    6.671 ±  0.276  ns/op
LoggerBenchmarks.infoWithException                avgt    5  315.486 ±  5.418  ns/op
LoggerBenchmarks.infoWithFieldBuilder             avgt    5   58.617 ±  2.849  ns/op
LoggerBenchmarks.infoWithNever                    avgt    5    0.649 ±  0.061  ns/op
LoggerBenchmarks.infoWithParameterizedString      avgt    5  131.175 ± 12.014  ns/op
LoggerBenchmarks.infoWithStringArg                avgt    5  127.228 ±  5.561  ns/op
LoggerBenchmarks.isInfoEnabled                    avgt    5    8.169 ±  0.116  ns/op
LoggerBenchmarks.traceWithParameterizedString     avgt    5   30.047 ±  2.690  ns/op
SLF4JLoggerBenchmarks.info                        avgt    5   51.840 ±  2.581  ns/op
SLF4JLoggerBenchmarks.infoWithArgument            avgt    5   66.246 ±  3.563  ns/op
SLF4JLoggerBenchmarks.infoWithArrayArgs           avgt    5   84.652 ±  4.196  ns/op
SLF4JLoggerBenchmarks.infoWithException           avgt    5  242.431 ± 21.231  ns/op
SLF4JLoggerBenchmarks.isInfoEnabled               avgt    5    3.665 ±  0.105  ns/op
```

`CoreLoggerBenchmarks` shows the CoreLogger SPI.  

`SLF4JLoggerBenchmarks` show the SLF4J API being called directly for comparison.

`JsonPathBenchmarks` measures the JSON Path lookups.

## Log4J Implementation

Uses Log4J 2.17.1 with layout-template-json.

`LoggerBenchmarks` shows the main `Logger` API.

```
Benchmark                                         Mode  Cnt    Score    Error  Units
CoreLoggerBenchmarks.info                         avgt    5  180.189 ± 56.272  ns/op
CoreLoggerBenchmarks.infoWithException            avgt    5  277.375 ± 15.761  ns/op
CoreLoggerBenchmarks.infoWithParameterizedString  avgt    5  404.823 ±  4.577  ns/op
CoreLoggerBenchmarks.isEnabled                    avgt    5    6.182 ±  0.244  ns/op
Log4JBenchmarks.info                              avgt    5  178.914 ± 10.641  ns/op
Log4JBenchmarks.infoWithArgument                  avgt    5  177.725 ±  4.816  ns/op
Log4JBenchmarks.infoWithArrayArgs                 avgt    5  179.679 ±  9.706  ns/op
Log4JBenchmarks.infoWithException                 avgt    5  178.184 ±  8.453  ns/op
Log4JBenchmarks.isInfoEnabled                     avgt    5    5.710 ±  0.110  ns/op
LoggerBenchmarks.info                             avgt    5  188.446 ± 56.959  ns/op
LoggerBenchmarks.infoWithAlways                   avgt    5  179.080 ± 50.268  ns/op
LoggerBenchmarks.infoWithContextString            avgt    5  180.849 ± 57.038  ns/op
LoggerBenchmarks.infoWithErrorCondition           avgt    5   24.605 ±  1.458  ns/op
LoggerBenchmarks.infoWithException                avgt    5  267.924 ± 12.968  ns/op
LoggerBenchmarks.infoWithFieldBuilder             avgt    5  184.514 ± 60.005  ns/op
LoggerBenchmarks.infoWithNever                    avgt    5    0.600 ±  0.018  ns/op
LoggerBenchmarks.infoWithParameterizedString      avgt    5  405.257 ± 24.864  ns/op
LoggerBenchmarks.infoWithStringArg                avgt    5  273.970 ± 11.897  ns/op
LoggerBenchmarks.isInfoEnabled                    avgt    5    6.956 ±  0.300  ns/op
LoggerBenchmarks.traceWithParameterizedString     avgt    5  217.130 ± 10.793  ns/op
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
