package com.tersesystems.echopraxia.scripting;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Level;
import com.tersesystems.echopraxia.logstash.LogstashLoggingContext;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class ScriptingBenchmarks {
  private static final Path path = Paths.get("src/jmh/tweakflow/condition.tf");
  private static final Condition condition =
      ScriptCondition.create(false, path, Throwable::printStackTrace);

  @Benchmark
  public void testConditionMatch(Blackhole blackhole) {
    // ScriptingBenchmarks.testConditionMatch  avgt    5  2.313 ± 0.222  us/op
    blackhole.consume(condition.test(Level.INFO, LogstashLoggingContext.empty()));
  }

  @Benchmark
  public void testConditionFail(Blackhole blackhole) {
    // ScriptingBenchmarks.testConditionFail   avgt    5  2.166 ± 0.039  us/op
    blackhole.consume(condition.test(Level.DEBUG, LogstashLoggingContext.empty()));
  }
}
