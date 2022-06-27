package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.api.AbstractJsonPathFinder;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.LoggingContext;
import com.tersesystems.echopraxia.api.Utilities;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.apache.logging.log4j.Marker;
import org.jetbrains.annotations.NotNull;

class Log4JLoggingContext extends AbstractJsonPathFinder implements LoggingContext {
  private final Supplier<List<Field>> argumentFields;
  private final Supplier<List<Field>> loggerFields;
  private final Supplier<List<Field>> joinedFields;
  private final Log4JCoreLogger.Context context;

  public Log4JLoggingContext(Log4JCoreLogger.Context context, Supplier<List<Field>> arguments) {
    // Defers and memoizes the arguments and context fields for a single logging statement.
    this.context = context;
    this.argumentFields = Utilities.memoize(arguments);
    this.loggerFields = Utilities.memoize(context::getLoggerFields);
    this.joinedFields =
        Utilities.memoize(
            Log4JCoreLogger.Context.joinFields(this.loggerFields, this.argumentFields));
  }

  public Log4JLoggingContext(Log4JCoreLogger.Context context) {
    this(context, Collections::emptyList);
  }

  @Override
  public @NotNull List<Field> getFields() {
    return joinedFields.get();
  }

  @Override
  public List<Field> getArgumentFields() {
    return argumentFields.get();
  }

  @Override
  public List<Field> getLoggerFields() {
    return loggerFields.get();
  }

  public @NotNull Marker getMarker() {
    return context.getMarker();
  }
}
