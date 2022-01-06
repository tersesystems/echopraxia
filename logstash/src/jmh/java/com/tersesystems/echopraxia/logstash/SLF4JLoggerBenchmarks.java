package com.tersesystems.echopraxia.logstash;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.slf4j.Logger;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
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
