package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.Level;
import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerFactory;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class CoreLoggerBenchmarks {

  private static final CoreLogger logger = CoreLoggerFactory.getLogger();

  private static final Exception exception = new RuntimeException();

  @Benchmark
  public void info() {
    logger.log(Level.INFO, "Message");
  }

  public void infoWithException() {
    logger.log(Level.INFO, "Message", exception);
  }
}
