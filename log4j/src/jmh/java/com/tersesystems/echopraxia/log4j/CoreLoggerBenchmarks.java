package com.tersesystems.echopraxia.log4j;

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
    logger.log(Level.INFO, "Message");
  }

  @Benchmark
  public void isEnabled() {
    logger.isEnabled(Level.INFO);
  }

  @Benchmark
  public void infoWithParameterizedString() {
    logger.log(Level.INFO, "Message {}", fb -> fb.onlyString("foo", "bar"), builder);
  }

  @Benchmark
  public void infoWithException() {
    logger.log(Level.INFO, "Message", exception);
  }
}
