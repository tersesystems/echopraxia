package echopraxia.log4j;

import echopraxia.api.FieldBuilder;
import echopraxia.logger.Logger;
import echopraxia.logging.api.Level;
import echopraxia.logging.spi.CoreLogger;
import echopraxia.logging.spi.CoreLoggerFactory;
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
      CoreLoggerFactory.getLogger(Logger.class.getName(), CoreLoggerBenchmarks.class.getName());
  private static final Exception exception = new RuntimeException();
  private static final FieldBuilder builder = FieldBuilder.instance();

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
  public void infoWithException() {
    logger.log(Level.INFO, "Message", fb -> fb.exception(exception), builder);
  }
}
