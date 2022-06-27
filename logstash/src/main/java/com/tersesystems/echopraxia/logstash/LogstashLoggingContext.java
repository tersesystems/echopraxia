package com.tersesystems.echopraxia.logstash;

import static com.tersesystems.echopraxia.api.Utilities.joinFields;
import static com.tersesystems.echopraxia.api.Utilities.memoize;

import com.tersesystems.echopraxia.api.AbstractJsonPathFinder;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.LoggingContext;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;

public class LogstashLoggingContext extends AbstractJsonPathFinder implements LoggingContext {

  private final Supplier<List<Field>> argumentFields;
  private final Supplier<List<Field>> loggerFields;

  private final LogstashCoreLogger.Context context;
  private final Supplier<List<Field>> fields;

  public LogstashLoggingContext(LogstashCoreLogger.Context context) {
    this(context, Collections::emptyList);
  }

  public LogstashLoggingContext(
      LogstashCoreLogger.Context context, Supplier<List<Field>> arguments) {
    this.context = context;
    this.argumentFields = memoize(arguments);
    this.loggerFields = memoize(context::getLoggerFields);
    this.fields = memoize(joinFields(this.loggerFields, this.argumentFields));
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

  public @NotNull List<Marker> getMarkers() {
    return context.getMarkers();
  }
}
