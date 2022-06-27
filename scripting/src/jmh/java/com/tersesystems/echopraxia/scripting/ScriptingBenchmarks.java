package com.tersesystems.echopraxia.scripting;

import com.tersesystems.echopraxia.api.*;
import com.tersesystems.echopraxia.api.Level;
import com.tersesystems.echopraxia.logstash.LogstashLoggingContext;
import com.tersesystems.echopraxia.logstash.MemoLoggingContext;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 20, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class ScriptingBenchmarks {
  private static final Path path = Paths.get("src/jmh/tweakflow/condition.tf");

  private static final Path watchedDir = Paths.get("src/jmh/tweakflow");

  public static String buildScript() {
    return "library echopraxia {\n"
        + "  function evaluate: (string level, dict ctx) ->\n"
        + "    let {\n"
        + "      find_number: ctx[:find_number];\n"
        + "    }\n"
        + "    find_number(\"$.some_field\") == 1;\n"
        + "}\n";
  }

  public static String buildBoolean() {
    return "library echopraxia {\n"
        + "  function evaluate: (string level, dict ctx) ->\n"
        + "    true;\n"
        + "}\n";
  }

  public static String buildInfo() {
    return "library echopraxia {\n"
        + "  function evaluate: (string level, dict ctx) ->\n"
        + "    level == 'INFO';\n"
        + "}\n";
  }

  private static final Condition fileCondition =
      ScriptCondition.create(false, path, Throwable::printStackTrace);

  private static final Condition stringCondition =
      ScriptCondition.create(false, buildScript(), Throwable::printStackTrace);

  private static final Condition booleanCondition =
      ScriptCondition.create(false, buildBoolean(), Throwable::printStackTrace);

  private static final Condition infoCondition =
      ScriptCondition.create(false, buildInfo(), Throwable::printStackTrace);

  private static final ScriptWatchService scriptWatchService = new ScriptWatchService(watchedDir);

  private static final ScriptHandle watchedScript =
      scriptWatchService.watchScript(
          watchedDir.resolve("condition.tf"), Throwable::printStackTrace);

  private static final Condition watchedCondition = ScriptCondition.create(false, watchedScript);

  private static final LoggingContext passContext =
      new MemoLoggingContext(
          LogstashLoggingContext.create(Field.value("some_field", Value.number(1))));

  private static final LoggingContext failContext =
      new MemoLoggingContext(LogstashLoggingContext.empty());

  @Benchmark
  public void testFileConditionMatch(Blackhole blackhole) {
    blackhole.consume(fileCondition.test(Level.INFO, passContext));
  }

  @Benchmark
  public void testStringConditionMatch(Blackhole blackhole) {
    blackhole.consume(stringCondition.test(Level.INFO, passContext));
  }

  @Benchmark
  public void testBooleanConditionMatch(Blackhole blackhole) {
    blackhole.consume(booleanCondition.test(Level.INFO, passContext));
  }

  @Benchmark
  public void testInfoConditionMatch(Blackhole blackhole) {
    blackhole.consume(infoCondition.test(Level.INFO, passContext));
  }

  @Benchmark
  public void testFileConditionFail(Blackhole blackhole) {
    blackhole.consume(fileCondition.test(Level.INFO, failContext));
  }

  @Benchmark
  public void testStringConditionFail(Blackhole blackhole) {
    blackhole.consume(stringCondition.test(Level.INFO, failContext));
  }

  @Benchmark
  public void testWatchedConditionMatch(Blackhole blackhole) {
    blackhole.consume(watchedCondition.test(Level.INFO, passContext));
  }

  @Benchmark
  public void testWatchedConditionFail(Blackhole blackhole) {
    blackhole.consume(watchedCondition.test(Level.DEBUG, failContext));
  }
}
