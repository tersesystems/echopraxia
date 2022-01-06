package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class LoggerBenchmarks {

  private static final Logger<?> logger = LoggerFactory.getLogger();

  private static final Exception exception = new RuntimeException();

  @Benchmark
  public void info() {
    // LoggerBenchmarks.info                   avgt   25   58.972 ± 1.780  ns/op
    logger.info("Message");
  }

  @Benchmark
  public void infoWithStringArg() {
    // LoggerBenchmarks.infoWithStringArg      avgt   25   90.871 ± 2.052  ns/op
    logger.info("Message", fb -> fb.onlyString("foo", "bar"));
  }

  @Benchmark
  public void infoWithContextString() {
    // LoggerBenchmarks.infoWithContextString  avgt   25  152.985 ± 4.938  ns/op
    logger.withFields(fb -> fb.onlyString("foo", "bar")).info("Message");
  }

  @Benchmark
  public void infoWithParameterizedString() {
    // LoggerBenchmarks.infoWithParameterizedString  avgt   25   74.490 ± 1.555  ns/op
    logger.info("Message {}", fb -> fb.onlyString("foo", "bar"));
  }

  @Benchmark
  public void infoWithException() {
    // LoggerBenchmarks.infoWithException            avgt   25  172.261 ± 4.207  ns/op
    logger.info("Message", exception);
  }

  @Benchmark
  public void infoWithNever() {
    // LoggerBenchmarks.infoWithNever                avgt   25   10.831 ± 0.191  ns/op
    logger.withCondition(Condition.never()).info("Message");
  }

  @Benchmark
  public void infoWithAlways() {
    // LoggerBenchmarks.infoWithAlways               avgt   25   66.180 ± 4.892  ns/op
    logger.withCondition(Condition.always()).info("Message");
  }

  @Benchmark
  public void infoWithFieldBuilder() {
    // LoggerBenchmarks.infoWithFieldBuilder         avgt   25   48.767 ± 0.912  ns/op
    logger.withFieldBuilder(Field.Builder.instance()).info("Message");
  }
}
