# Benchmarks

The benchmarks here measure latency -- the amount of time spent in processing a single statement.  This is because we are interested in the difference between running a "raw" logging statement and running an Echopraxia logging statement that wraps around it.  Implementations are set up with a null / no-op appender, so the appender time is not measured.  This does not measure throughput, and does not measure output through an appender at all -- please be aware that other frameworks may have benchmarks that show throughput rather than latency, which is not a great comparison.

Another thing to note is that a nanosecond is a very short amount of time.   1000 nanoseconds is 0.001 millisecond, and creating a single `new Object()` will take around 15 nanoseconds from [initialization](https://shipilev.net/jvm/anatomy-quarks/6-new-object-stages/) [costs](https://shipilev.net/jvm/anatomy-quarks/7-initialization-costs/).

**NOTE**: when running benchmarks, please ensure you are dealing with an appropriately cooled system!  Benchmarks become surprisingly inaccurate when your computer is hot enough, as the CPU will turn on thermal throttling to manage excess temperatures.

## Setup

All benchmarks were run on a Dell XPS 15 running Linux.

```
OpenJDK 64-Bit Server VM Corretto-11.0.9.11.1 (build 11.0.9+11-LTS, mixed mode)
```

The JDK is Corretto 17:

```
sdk install java 17.0.3.6.1-amzn
```

The JMH options are:

```
args = ['-prof', 'gc', '-rf', 'json']
```

## Logstash Implementation

Uses logback 1.2.11 and logstash-logback-encoder 7.2.

`LoggerBenchmarks` shows the main `Logger` API.

The core logger resolves arguments only if an `isEnabled(marker, level)` check is passed.  All arguments are passed into the condition, and if the condition passes then the logger sends it to various appenders.

`CoreLoggerBenchmarks` shows the CoreLogger SPI.  
`CoreLoggerBenchmarks` shows the CoreLogger SPI.

`SLF4JLoggerBenchmarks` show the SLF4J API being called directly for comparison -- what you'd see if you were using straight Logback.

`JsonPathBenchmarks` measures the JSON Path lookups.

### Running

```scala
./gradlew logstash:jmh
LOGDATE=$(date +%Y%m%dT%H%M%S)
mv logstash/jmh-result.json logstash/benchmarks/$LOGDATE.json
```

### Results

[20220715T101003.json](https://jmh.morethan.io/?source=https://raw.githubusercontent.com/tersesystems/echopraxia/main/logstash/benchmarks/17.0.3.6.1-amzn/20220715T101003.json)

## Log4J Implementation

Uses Log4J 2.18.0 with layout-template-json.

`LoggerBenchmarks` shows the main `Logger` API.

Note that Log4J has multiple options for `isEnabled` that include passing the message and exception through filters.

Echopraxia does *not* do this, because it requires resolving all arguments.  Instead, the core logger only calls `isEnabled(marker, level)` and then resolves arguments when testing the condition.

`CoreLoggerBenchmarks` shows the CoreLogger SPI.

`Log4JBenchmarks` show the Log4J API being called directly for comparison.

### Running

```bash
./gradlew log4j:jmh
LOGDATE=$(date +%Y%m%dT%H%M%S)
mv log4j/jmh-result.json log4j/benchmarks/17.0.3.6.1-amzn/$LOGDATE.json
```

### Results

[20220715T110146.json](https://jmh.morethan.io/?source=https://raw.githubusercontent.com/tersesystems/echopraxia/main/log4j/benchmarks/17.0.3.6.1-amzn/20220715T110146.json)

## Fluent Logger

The Fluent Logger uses the same configuration as above.

### Running

```bash
./gradlew fluent:jmh
LOGDATE=$(date +%Y%m%dT%H%M%S)
mv fluent/jmh-result.json fluent/benchmarks/17.0.3.6.1-amzn/$LOGDATE.json
```

### Results

[20220715T115509.json](https://jmh.morethan.io/?source=https://raw.githubusercontent.com/tersesystems/echopraxia/main/fluent/benchmarks/17.0.3.6.1-amzn/20220715T115509.json)

## Semantic Logger

The semantic logger configuration is the same as above.  There is only one kind of call you can make.

### Running

```bash
./gradlew semantic:jmh
LOGDATE=$(date +%Y%m%dT%H%M%S)
mv semantic/jmh-result.json semantic/benchmarks/17.0.3.6.1-amzn/$LOGDATE.json
```

### Results

[20220715T115730.json](https://jmh.morethan.io/?source=https://raw.githubusercontent.com/tersesystems/echopraxia/main/semantic/benchmarks/17.0.3.6.1-amzn/20220715T115730.json)

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


### Running

```bash
./gradlew semantic:jmh
LOGDATE=$(date +%Y%m%dT%H%M%S)
mv semantic/jmh-result.json semantic/benchmarks/17.0.3.6.1-amzn/$LOGDATE.json
```

### Results

[20220715T115730.json](https://jmh.morethan.io/?source=https://raw.githubusercontent.com/tersesystems/echopraxia/main/semantic/benchmarks/17.0.3.6.1-amzn/20220715T115730.json)
