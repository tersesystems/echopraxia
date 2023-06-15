package com.tersesystems.echopraxia.logback;

import static com.tersesystems.echopraxia.spi.Utilities.joinFields;
import static com.tersesystems.echopraxia.spi.Utilities.memoize;

import com.tersesystems.echopraxia.api.CoreLogger;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.LoggingContext;
import com.tersesystems.echopraxia.spi.AbstractJsonPathFinder;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;

/**
 * The logging context composes the "logger context" (markers/fields associated with the logger) and
 * the field arguments associated with the individual logging event.
 */
public class LogbackLoggingContext extends AbstractJsonPathFinder implements LoggingContext {

  private final Supplier<List<Field>> argumentFields;
  private final Supplier<List<Field>> loggerFields;

  private final LogbackLoggerContext loggerContext;
  private final Supplier<List<Field>> fields;
  private final CoreLogger core;

  public LogbackLoggingContext(CoreLogger core, LogbackLoggerContext loggerContext) {
    this(core, loggerContext, Collections::emptyList);
  }

  public LogbackLoggingContext(
      CoreLogger core, LogbackLoggerContext loggerContext, Supplier<List<Field>> arguments) {
    this.core = core;
    this.loggerContext = loggerContext;
    this.argumentFields = memoize(arguments);
    this.loggerFields = memoize(loggerContext::getLoggerFields);
    this.fields = memoize(joinFields(this.loggerFields, this.argumentFields));
  }

  @Override
  public CoreLogger getCore() {
    return core;
  }

  @Override
  public @NotNull List<Field> getFields() {
    return fields.get();
  }

  @Override
  public List<Field> getLoggerFields() {
    return loggerFields.get();
  }

  @Override
  public List<Field> getArgumentFields() {
    return argumentFields.get();
  }

  public List<Marker> getMarkers() {
    return loggerContext.getMarkers();
  }
}
