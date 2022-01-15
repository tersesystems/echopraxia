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

  private static final Path watchedDir = Paths.get("src/jmh/tweakflow");

  public static String buildScript() {
    StringBuilder b = new StringBuilder("");
    b.append("library echopraxia {");
    b.append("  function evaluate: (string level, dict fields) ->");
    b.append("    level == \"INFO\";");
    b.append("}");
    return b.toString();
  }

  private static final Condition fileCondition =
      ScriptCondition.create(false, path, Throwable::printStackTrace);

  private static final Condition stringCondition =
      ScriptCondition.create(false, buildScript(), Throwable::printStackTrace);

  private static final ScriptWatchService scriptWatchService = new ScriptWatchService(watchedDir);

  private static final ScriptHandle watchedScript =
      scriptWatchService.watchScript(
          watchedDir.resolve("condition.tf"), Throwable::printStackTrace);

  private static final Condition watchedCondition = ScriptCondition.create(false, watchedScript);

  @Benchmark
  public void testFileConditionMatch(Blackhole blackhole) {
    // ScriptingBenchmarks.testFileConditionMatch    avgt    5  208.966 ± 15.136  ns/op
    blackhole.consume(fileCondition.test(Level.INFO, LogstashLoggingContext.empty()));
  }

  @Benchmark
  public void testStringConditionMatch(Blackhole blackhole) {
    // ScriptingBenchmarks.testFileConditionMatch    avgt    5  208.966 ± 15.136  ns/op
    blackhole.consume(stringCondition.test(Level.INFO, LogstashLoggingContext.empty()));
  }

  @Benchmark
  public void testFileConditionFail(Blackhole blackhole) {
    // ScriptingBenchmarks.testFileConditionFail     avgt    5  203.641 ±  0.959  ns/op
    blackhole.consume(fileCondition.test(Level.DEBUG, LogstashLoggingContext.empty()));
  }

  @Benchmark
  public void testStringConditionFail(Blackhole blackhole) {
    // ScriptingBenchmarks.testStringConditionFail   avgt    5  198.490 ±  1.657  ns/op
    blackhole.consume(stringCondition.test(Level.DEBUG, LogstashLoggingContext.empty()));
  }

  @Benchmark
  public void testWatchedConditionMatch(Blackhole blackhole) {
    // ScriptingBenchmarks.testWatchedConditionFail   avgt    5  236.907 ±  9.992  ns/op
    blackhole.consume(watchedCondition.test(Level.INFO, LogstashLoggingContext.empty()));
  }

  @Benchmark
  public void testWatchedConditionFail(Blackhole blackhole) {
    // ScriptingBenchmarks.testWatchedConditionMatch  avgt    5  256.803 ± 25.520  ns/op
    blackhole.consume(watchedCondition.test(Level.DEBUG, LogstashLoggingContext.empty()));
  }
}
