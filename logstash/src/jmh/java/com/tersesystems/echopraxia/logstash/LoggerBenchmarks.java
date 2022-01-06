package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class LoggerBenchmarks {
  private static final Logger<?> logger = LoggerFactory.getLogger();
  private static final Exception exception = new RuntimeException();

  @Benchmark
  public void info() {
    // LoggerBenchmarks.info                         avgt    5   47.464 ± 0.414  ns/op
    logger.info("Message");
  }

  @Benchmark
  public void infoWithStringArg() {
    // LoggerBenchmarks.infoWithStringArg            avgt    5   75.672 ± 0.749  ns/op
    logger.info("Message", fb -> fb.onlyString("foo", "bar"));
  }

  @Benchmark
  public void infoWithContextString() {
    // LoggerBenchmarks.infoWithContextString        avgt    5  116.451 ± 6.000  ns/op
    logger.withFields(fb -> fb.onlyString("foo", "bar")).info("Message");
  }

  @Benchmark
  public void infoWithParameterizedString() {
    // LoggerBenchmarks.infoWithParameterizedString  avgt    5   75.732 ± 0.421  ns/op
    logger.info("Message {}", fb -> fb.onlyString("foo", "bar"));
  }

  @Benchmark
  public void infoWithException() {
    // LoggerBenchmarks.infoWithException            avgt    5  175.756 ± 1.843  ns/op
    logger.info("Message", exception);
  }

  @Benchmark
  public void infoWithNever() {
    // LoggerBenchmarks.infoWithNever                avgt    5   11.358 ± 0.165  ns/op
    logger.withCondition(Condition.never()).info("Message");
  }

  @Benchmark
  public void infoWithAlways() {
    // LoggerBenchmarks.infoWithAlways               avgt    5   57.670 ± 0.318  ns/op
    logger.withCondition(Condition.always()).info("Message");
  }

  @Benchmark
  public void infoWithFieldBuilder() {
    // LoggerBenchmarks.infoWithFieldBuilder         avgt    5   51.013 ± 0.436  ns/op
    logger.withFieldBuilder(Field.Builder.instance()).info("Message");
  }
}
