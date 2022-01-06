package com.tersesystems.echopraxia.semantic;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
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
