package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.api.AbstractLoggingContext;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.Utilities;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;

class MemoLoggingContext extends AbstractLoggingContext implements MarkerLoggingContext {

  private final Supplier<List<Field>> arguments;
  private final Supplier<List<Field>> fields;

  private final LogstashLoggingContext context;

  public MemoLoggingContext(LogstashLoggingContext context) {
    this(context, Collections::emptyList);
  }

  public MemoLoggingContext(LogstashLoggingContext context, Supplier<List<Field>> arguments) {
    // Defers and memoizes the arguments and fields for a single logging statement.
    this.context = context;
    this.arguments = Utilities.memoize(arguments);
    this.fields = Utilities.memoize(context::getFields);
  }

  @Override
  public @NotNull List<Field> getFields() {
    List<Field> value = fields.get();
    return value;
  }

  public List<Field> arguments() {
    List<Field> value = arguments.get();
    return value;
  }

  @Override
  public @NotNull List<Marker> getMarkers() {
    return context.getMarkers();
  }
}
