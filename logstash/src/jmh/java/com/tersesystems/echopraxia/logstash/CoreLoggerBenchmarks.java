package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.api.CoreLogger;
import com.tersesystems.echopraxia.api.CoreLoggerFactory;
import com.tersesystems.echopraxia.api.FieldBuilder;
import com.tersesystems.echopraxia.api.Level;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class CoreLoggerBenchmarks {
  private static final CoreLogger logger =
      CoreLoggerFactory.getLogger(CoreLoggerBenchmarks.class.getName(), CoreLoggerBenchmarks.class);
  private static final Exception exception = new RuntimeException();
  private static final FieldBuilder builder = FieldBuilder.instance();

  private static final CoreLogger contextLogger =
      logger.withFields(fb -> fb.string("foo", "bar"), builder);

  @Benchmark
  public void info() {
    logger.log(Level.INFO, "Message");
  }

  @Benchmark
  public void isEnabled(Blackhole blackhole) {
    blackhole.consume(logger.isEnabled(Level.INFO));
  }

  @Benchmark
  public void infoWithParameterizedString() {
    logger.log(Level.INFO, "Message {}", fb -> fb.string("foo", "bar"), builder);
  }

  @Benchmark
  public void infoWithContext() {
    contextLogger.log(Level.INFO, "Message");
  }

  @Benchmark
  public void infoWithException() {
    logger.log(Level.INFO, "Message", fb -> fb.exception(exception), builder);
  }
}
