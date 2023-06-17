package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.*;
import com.tersesystems.echopraxia.api.Condition;
import com.tersesystems.echopraxia.api.FieldBuilder;
import com.tersesystems.echopraxia.api.Level;
import com.tersesystems.echopraxia.api.PresentationFieldBuilder;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class LoggerBenchmarks {
  private static final Logger<PresentationFieldBuilder> logger = LoggerFactory.getLogger();
  private static final Exception exception = new RuntimeException();

  private static final Logger<?> neverLogger = logger.withCondition(Condition.never());
  private static final Logger<?> alwaysLogger = logger.withCondition(Condition.always());
  private static final Logger<?> conditionLogger =
      logger.withCondition((level, context) -> level.equals(Level.ERROR));
  private static final Logger<?> fieldBuilderLogger =
      logger.withFieldBuilder(FieldBuilder.instance());
  private static final Logger<?> contextLogger = logger.withFields(fb -> fb.string("foo", "bar"));

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
  public void isInfoEnabled(Blackhole blackhole) {
    blackhole.consume(logger.isInfoEnabled());
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
  public void infoWithContextChain() {
    logger.withFields(fb -> fb.string("foo", "bar")).info("Message");
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

  @Benchmark
  public void traceWithContextChain() {
    logger.withFields(fb -> fb.string("foo", "bar")).trace("Message");
  }
}
