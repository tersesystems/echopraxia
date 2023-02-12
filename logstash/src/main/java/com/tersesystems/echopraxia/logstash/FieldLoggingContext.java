package com.tersesystems.echopraxia.logstash;

import static com.tersesystems.echopraxia.api.Utilities.memoize;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.echopraxia.api.Field;
import java.util.*;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class FieldLoggingContext extends AbstractEventLoggingContext {

  private final Supplier<List<Field>> argumentFields;
  private final Supplier<List<Field>> markerFields;

  private final Supplier<List<Field>> fields;

  public FieldLoggingContext(@NotNull ILoggingEvent event) {
    this.argumentFields = memoize(() -> fieldArguments(event));
    this.markerFields = memoize(() -> fieldMarkers(event));
    this.fields =
        memoize(
            () -> {
              List<Field> fields = new ArrayList<>();
              fields.addAll(getArgumentFields()); // argument fields should take precedence
              fields.addAll(getLoggerFields());
              return fields;
            });
  }

  @Override
  public @NotNull List<Field> getFields() {
    return fields.get();
  }

  @Override
  public @NotNull List<Field> getLoggerFields() {
    return markerFields.get();
  }

  @Override
  public @NotNull List<Field> getArgumentFields() {
    return argumentFields.get();
  }
}
