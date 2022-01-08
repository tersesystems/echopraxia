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
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class ScriptingBenchmarks {
  private static final Path path = Paths.get("src/jmh/tweakflow/condition.tf");

  public static String buildScript() {
    StringBuilder b = new StringBuilder("library echopraxia {");
    b.append("  function evaluate: (string level, dict fields) ->");
    b.append("    fields[:correlation_id] == \"match\";");
    b.append("}");
    return b.toString();
  }

  private static final Condition fileCondition =
      ScriptCondition.create(false, path, Throwable::printStackTrace);

  private static final Condition stringCondition =
      ScriptCondition.create(false, buildScript(), Throwable::printStackTrace);

  @Benchmark
  public void testFileConditionMatch(Blackhole blackhole) {
    // ScriptingBenchmarks.testFileConditionMatch    avgt    5  197.870 ±  1.252  ns/op
    blackhole.consume(fileCondition.test(Level.INFO, LogstashLoggingContext.empty()));
  }

  @Benchmark
  public void testStringConditionMatch(Blackhole blackhole) {
    // ScriptingBenchmarks.testFileConditionMatch    avgt    5  197.870 ±  1.252  ns/op
    blackhole.consume(stringCondition.test(Level.INFO, LogstashLoggingContext.empty()));
  }

  @Benchmark
  public void testFileConditionFail(Blackhole blackhole) {
    // ScriptingBenchmarks.testFileConditionFail     avgt    5  188.610 ±  0.351  ns/op
    blackhole.consume(fileCondition.test(Level.DEBUG, LogstashLoggingContext.empty()));
  }

  @Benchmark
  public void testStringConditionFail(Blackhole blackhole) {
    // ScriptingBenchmarks.testStringConditionFail   avgt    5  209.430 ± 11.467  ns/op
    blackhole.consume(stringCondition.test(Level.DEBUG, LogstashLoggingContext.empty()));
  }
}
