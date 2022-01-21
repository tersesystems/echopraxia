package com.tersesystems.echopraxia.logstash;

import static net.logstash.logback.argument.StructuredArguments.kv;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
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
    logger.info("message");
  }

  @Benchmark
  public void isInfoEnabled(Blackhole blackhole) {
    blackhole.consume(logger.isInfoEnabled());
  }

  @Benchmark
  public void infoWithArgument() {
    logger.info("message {}", kv("key", "value"));
  }

  @Benchmark
  public void infoWithArrayArgs() {
    logger.info(
        "message {} {} {} {}",
        kv("key1", "one"),
        kv("key2", "two"),
        kv("key3", "three"),
        kv("key4", "four"));
  }

  @Benchmark
  public void infoWithException() {
    logger.info("Message", exception);
  }
}
