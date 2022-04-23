package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Level;
import com.tersesystems.echopraxia.LoggingContext;
import com.tersesystems.echopraxia.ValueField;
import com.tersesystems.echopraxia.logstash.LogstashLoggingContext;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class JsonPathBenchmarks {

  private static final Condition streamCondition = Condition.valueMatch("some_field", f -> f.raw().equals("testing"));
  private static final Condition pathCondition = new Condition() {
    @Override
    public boolean test(com.tersesystems.echopraxia.Level level, LoggingContext context) {
      return context.findString("$.some_field").filter(f -> f.equals("testing")).isPresent();
    }
  };

  private static final LoggingContext passContext = LogstashLoggingContext.create(
    ValueField.create("some_field", Field.Value.string("testing"))
  );

  private static final LoggingContext failContext = LogstashLoggingContext.empty();

  @Benchmark
  public void testStreamConditionPass(Blackhole blackhole) {
    blackhole.consume(streamCondition.test(com.tersesystems.echopraxia.Level.INFO, passContext));
  }

  @Benchmark
  public void testStreamConditionFail(Blackhole blackhole) {
    blackhole.consume(streamCondition.test(com.tersesystems.echopraxia.Level.INFO, failContext));
  }

  @Benchmark
  public void testPathConditionPass(Blackhole blackhole) {
    blackhole.consume(pathCondition.test(com.tersesystems.echopraxia.Level.INFO, passContext));
  }

  @Benchmark
  public void testPathConditionFail(Blackhole blackhole) {
    blackhole.consume(pathCondition.test(com.tersesystems.echopraxia.Level.INFO, failContext));
  }

}
