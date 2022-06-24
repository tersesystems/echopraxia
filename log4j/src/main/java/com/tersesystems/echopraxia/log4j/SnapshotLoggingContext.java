package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.api.AbstractLoggingContext;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.Utilities;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.apache.logging.log4j.Marker;
import org.jetbrains.annotations.NotNull;

class SnapshotLoggingContext extends AbstractLoggingContext implements MarkerLoggingContext {

  private final Supplier<List<Field>> arguments;
  private final Supplier<List<Field>> fields;

  private final Log4JLoggingContext context;

  public SnapshotLoggingContext(Log4JLoggingContext context, Supplier<List<Field>> arguments) {
    // Defers and memoizes the arguments and fields for a single logging statement.
    this.context = context;
    this.arguments = Utilities.memoize(arguments);
    this.fields =
        Utilities.memoize(Log4JLoggingContext.joinFields(context::getFields, this.arguments));
  }

  public SnapshotLoggingContext(Log4JLoggingContext context) {
    this(context, Collections::emptyList);
  }

  @Override
  public @NotNull List<Field> getFields() {
    return fields.get();
  }

  public List<Field> arguments() {
    return arguments.get();
  }

  @Override
  public @NotNull Marker getMarker() {
    return context.getMarker();
  }
}
