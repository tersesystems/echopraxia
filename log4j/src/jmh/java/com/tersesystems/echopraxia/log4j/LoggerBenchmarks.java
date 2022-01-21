package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

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
    // Log4JBenchmarks.info                         avgt    5  142.433 ± 27.654  ns/op
    logger.info("Message");
  }

  @Benchmark
  public void isInfoEnabled(Blackhole blackhole) {
    // Log4JBenchmarks.isInfoEnabled                avgt    5    5.556 ±  0.056  ns/op
    blackhole.consume(logger.isInfoEnabled());
  }

  @Benchmark
  public void infoWithStringArg() {
    // Log4JBenchmarks.infoWithStringArg            avgt    5  288.263 ±  0.753  ns/op
    logger.info("Message", fb -> fb.onlyString("foo", "bar"));
  }

  @Benchmark
  public void infoWithContextString() {
    // Log4JBenchmarks.infoWithContextString        avgt    5  247.300 ±  2.810  ns/op
    logger.withFields(fb -> fb.onlyString("foo", "bar")).info("Message");
  }

  @Benchmark
  public void infoWithParameterizedString() {
    // Log4JBenchmarks.infoWithParameterizedString  avgt    5  412.990 ± 27.346  ns/op
    logger.info("Message {}", fb -> fb.onlyString("foo", "bar"));
  }

  @Benchmark
  public void infoWithException() {
    // Log4JBenchmarks.infoWithException            avgt    5  238.892 ± 54.381  ns/op
    logger.info("Message", exception);
  }

  @Benchmark
  public void infoWithNever() {
    // Log4JBenchmarks.infoWithNever                avgt    5  176.454 ± 57.493  ns/op
    logger.withCondition(Condition.never()).info("Message");
  }

  @Benchmark
  public void infoWithAlways() {
    // Log4JBenchmarks.infoWithAlways               avgt    5  152.703 ± 52.607  ns/op
    logger.withCondition(Condition.always()).info("Message");
  }

  @Benchmark
  public void infoWithFieldBuilder() {
    // Log4JBenchmarks.infoWithFieldBuilder         avgt    5  156.345 ± 80.493  ns/op
    logger.withFieldBuilder(Field.Builder.instance()).info("Message");
  }
}
