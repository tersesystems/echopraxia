package echopraxia.logstash;

import echopraxia.api.FieldBuilder;
import echopraxia.async.AsyncLogger;
import echopraxia.async.AsyncLoggerFactory;
import echopraxia.logging.api.Condition;
import echopraxia.logging.api.Level;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.openjdk.jmh.annotations.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class AsyncLoggerBenchmarks {
  private static final AsyncLogger<FieldBuilder> logger =
      AsyncLoggerFactory.getLogger()
          .withExecutor(
              new Executor() {
                @Override
                public void execute(@NotNull Runnable command) {
                  // do nothing
                }
              });

  private static final Exception exception = new RuntimeException();

  private static final AsyncLogger<?> neverLogger = logger.withCondition(Condition.never());
  private static final AsyncLogger<?> alwaysLogger = logger.withCondition(Condition.always());
  private static final AsyncLogger<?> conditionLogger =
      logger.withCondition((level, context) -> level.equals(Level.ERROR));
  private static final AsyncLogger<?> fieldBuilderLogger =
      logger.withFieldBuilder(FieldBuilder.instance());
  private static final AsyncLogger<?> contextLogger =
      logger.withFields(fb -> fb.string("foo", "bar"));

  @Benchmark
  public void info() {
    logger.info("Message");
  }

  @Benchmark
  public void infoWithNever() {
    neverLogger.info("Message");
  }

  @Benchmark
  public void infoWithAlways() {
    alwaysLogger.info("Message");
  }

  @Benchmark
  public void infoWithFieldBuilder() {
    fieldBuilderLogger.info("Message");
  }

  @Benchmark
  public void infoWithErrorCondition() {
    conditionLogger.info("Message");
  }

  @Benchmark
  public void infoWithStringArg() {
    // No {} in the message template
    logger.info("Message", fb -> fb.string("foo", "bar"));
  }

  @Benchmark
  public void infoWithContextString() {
    contextLogger.info("Message");
  }

  @Benchmark
  public void infoWithParameterizedString() {
    // {} in message template
    logger.info("Message {}", fb -> fb.string("foo", "bar"));
  }

  @Benchmark
  public void infoWithException() {
    logger.info("Message", exception);
  }

  @Benchmark
  public void trace() {
    // should never log
    logger.trace("Trace Message");
  }

  @Benchmark
  public void traceWithParameterizedString() {
    // should never log
    logger.trace("Message {}", fb -> fb.string("foo", "bar"));
  }
}
