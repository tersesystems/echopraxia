package com.tersesystems.echopraxia.fluent;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class FluentBenchmarks {

  private static final FluentLogger<?> logger = FluentLoggerFactory.getLogger();

  private static final Exception exception = new RuntimeException();

  @Benchmark
  public void info() {
    // FluentBenchmarks.info                     avgt   25  103.478 ±  2.631  ns/op
    logger.atInfo().message("Message").log();
  }

  @Benchmark
  public void infoWithArgument() {
    // FluentBenchmarks.infoWithArgument         avgt   25  196.968 ±  4.884  ns/op
    logger.atInfo().message("Message {}").argument(fb -> fb.string("foo", "bar")).log();
  }

  @Benchmark
  public void infoWithArgAndException() {
    // FluentBenchmarks.infoWithArgAndException  avgt   25  331.651 ± 15.591  ns/op
    logger
        .atInfo()
        .message("Message {}")
        .argument(fb -> fb.string("foo", "bar"))
        .exception(exception)
        .log();
  }

  @Benchmark
  public void infoWithException() {
    // FluentBenchmarks.infoWithException        avgt   25  351.918 ±  7.124  ns/op
    logger.atInfo().message("Message").exception(exception).log();
  }
}
