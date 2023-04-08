package com.tersesystems.echopraxia.logback;

import static com.tersesystems.echopraxia.api.Utilities.memoize;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.echopraxia.api.CoreLogger;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.logback.AbstractEventLoggingContext;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class MarkerLoggingContext extends AbstractEventLoggingContext {

  private final CoreLogger core;

  private final Supplier<List<Field>> markerFields;

  public MarkerLoggingContext(CoreLogger core, @NotNull ILoggingEvent event) {
    this.core = core;
    this.markerFields = memoize(() -> fieldMarkers(event));
  }

  @Override
  public CoreLogger getCore() {
    return this.core;
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
