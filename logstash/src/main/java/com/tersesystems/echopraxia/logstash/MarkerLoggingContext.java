package com.tersesystems.echopraxia.logstash;

import static com.tersesystems.echopraxia.api.Utilities.memoize;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.echopraxia.api.Field;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class MarkerLoggingContext extends AbstractEventLoggingContext {

  private final Supplier<List<Field>> markerFields;

  public MarkerLoggingContext(@NotNull ILoggingEvent event) {
    this.markerFields = memoize(() -> fieldMarkers(event));
  }

  @Override
  public @NotNull List<Field> getFields() {
    return getLoggerFields();
  }

  @Override
  public @NotNull List<Field> getLoggerFields() {
    return markerFields.get();
  }

  @Override
  public @NotNull List<Field> getArgumentFields() {
    return Collections.emptyList();
  }
}
