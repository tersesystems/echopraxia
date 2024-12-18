package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.api.*;
import com.tersesystems.echopraxia.api.Level;
import com.tersesystems.echopraxia.api.LoggingContext;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class JsonPathBenchmarks {

  private static final Condition streamCondition =
      Condition.valueMatch("some_field", f -> f.raw().equals("testing"));
  private static final Condition pathCondition =
      Condition.jsonPath(
          context ->
              context.findString("$.some_field").filter(f -> f.equals("testing")).isPresent());

  private static final LoggingContext passContext =
      new FakeLoggingContext(Field.value("some_field", Value.string("testing")));

  private static final LoggingContext failContext = new FakeLoggingContext();

  @Benchmark
  public void testStreamConditionPass(Blackhole blackhole) {
    blackhole.consume(streamCondition.test(Level.INFO, passContext));
  }

  @Benchmark
  public void testStreamConditionFail(Blackhole blackhole) {
    blackhole.consume(streamCondition.test(Level.INFO, failContext));
  }

  @Benchmark
  public void testPathConditionPass(Blackhole blackhole) {
    blackhole.consume(pathCondition.test(Level.INFO, passContext));
  }

  @Benchmark
  public void testPathConditionFail(Blackhole blackhole) {
    blackhole.consume(pathCondition.test(Level.INFO, failContext));
  }
}
