package com.tersesystems.echopraxia.logstash;

import static com.tersesystems.echopraxia.api.Utilities.memoize;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.echopraxia.api.Field;
import java.util.*;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class ArgumentLoggingContext extends AbstractEventLoggingContext {

  private final Supplier<List<Field>> argumentFields;

  public ArgumentLoggingContext(@NotNull ILoggingEvent event) {
    this.argumentFields = memoize(() -> fieldArguments(event));
  }

  @Override
  public @NotNull List<Field> getFields() {
    return argumentFields.get();
  }

  @Override
  public @NotNull List<Field> getLoggerFields() {
    return Collections.emptyList();
  }

  @Override
  public @NotNull List<Field> getArgumentFields() {
    return argumentFields.get();
  }
}
