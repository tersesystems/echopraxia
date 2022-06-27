package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.api.AbstractLoggingContext;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.LoggingContext;
import com.tersesystems.echopraxia.api.Utilities;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;

public class MemoLoggingContext extends AbstractLoggingContext
    implements LoggingContext, MarkerLoggingContext {

  private final Supplier<List<Field>> argumentFields;
  private final Supplier<List<Field>> contextFields;

  private final LogstashLoggingContext context;
  private final Supplier<List<Field>> fields;

  public MemoLoggingContext(LogstashLoggingContext context) {
    this(context, Collections::emptyList);
  }

  public MemoLoggingContext(LogstashLoggingContext context, Supplier<List<Field>> arguments) {
    this.context = context;
    this.argumentFields = Utilities.memoize(arguments);
    this.contextFields = Utilities.memoize(context::getLoggerFields);
    this.fields =
        Utilities.memoize(
            LogstashLoggingContext.joinFields(this.contextFields, this.argumentFields));
  }

  @Override
  public @NotNull List<Field> getFields() {
    return fields.get();
  }

  @Override
  public List<Field> getLoggerFields() {
    return contextFields.get();
  }

  @Override
  public List<Field> getArgumentFields() {
    return argumentFields.get();
  }

  @Override
  public @NotNull List<Marker> getMarkers() {
    return context.getMarkers();
  }
}
