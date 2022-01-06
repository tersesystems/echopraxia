package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Level;
import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerFactory;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class CoreLoggerBenchmarks {

  private static final CoreLogger logger = CoreLoggerFactory.getLogger();

  private static final Exception exception = new RuntimeException();

  private static final Field.Builder builder = Field.Builder.instance();

  @Benchmark
  public void info() {
    // CoreLoggerBenchmarks.info                     avgt    5   45.618 ± 0.965  ns/op
    logger.log(Level.INFO, "Message");
  }

  @Benchmark
  public void infoWithParameterizedString() {
    // CoreLoggerBenchmarks.infoWithParameterizedString  avgt    5   76.189 ± 2.388  ns/op
    logger.log(Level.INFO, "Message {}", fb -> fb.onlyString("foo", "bar"), builder);
  }

  @Benchmark
  public void infoWithException() {
    // CoreLoggerBenchmarks.infoWithException            avgt    5  174.242 ± 7.510  ns/op
    logger.log(Level.INFO, "Message", exception);
  }
}
