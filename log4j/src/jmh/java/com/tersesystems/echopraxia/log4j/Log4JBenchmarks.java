package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.api.Value;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.KeyValueField;
import com.tersesystems.echopraxia.log4j.layout.EchopraxiaFieldsMessage;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.jetbrains.annotations.NotNull;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class Log4JBenchmarks {
  private static final Logger logger = LogManager.getLogger(Log4JBenchmarks.class);

  private static final Exception exception = new RuntimeException();

  private static final Field field = new MyKeyValueField("name", Value.string("value"));

  private static final List<Field> fields = Arrays.asList(field, field, field, field);

  private static final Message message =
      new EchopraxiaFieldsMessage("message", emptyList(), emptyList());

  private static final Message messageWithArgument =
      new EchopraxiaFieldsMessage("message {}", singletonList(field), emptyList());

  private static final EchopraxiaFieldsMessage fieldsMessage =
      new EchopraxiaFieldsMessage("message {} {} {} {}", fields, emptyList());

  @Benchmark
  public void info() {
    logger.info(message);
  }

  @Benchmark
  public void isInfoEnabled(Blackhole blackhole) {
    blackhole.consume(logger.isInfoEnabled());
  }

  @Benchmark
  public void infoWithArgument() {
    logger.info(messageWithArgument);
  }

  @Benchmark
  public void infoWithArrayArgs() {
    logger.info(fieldsMessage);
  }

  @Benchmark
  public void infoWithException() {
    logger.info(message, exception);
  }

  static class MyKeyValueField implements KeyValueField {
    private final String name;
    private final Value<?> value;

    MyKeyValueField(String name, Value<?> value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public @NotNull String name() {
      return name;
    }

    @Override
    public @NotNull Value<?> value() {
      return value;
    }
  }
}
