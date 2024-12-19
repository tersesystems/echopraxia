package echopraxia.log4j;

import static echopraxia.logging.spi.Utilities.joinFields;
import static echopraxia.logging.spi.Utilities.memoize;

import echopraxia.api.Field;
import echopraxia.logging.api.LoggingContext;
import echopraxia.logging.spi.CoreLogger;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.apache.logging.log4j.Marker;
import org.jetbrains.annotations.NotNull;

public class Log4JLoggingContext implements LoggingContext {
  private final Supplier<List<Field>> argumentFields;
  private final Supplier<List<Field>> loggerFields;
  private final Supplier<List<Field>> joinedFields;
  private final Log4JCoreLogger.Context context;
  private final CoreLogger core;

  public Log4JLoggingContext(
      CoreLogger core, Log4JCoreLogger.Context context, Supplier<List<Field>> arguments) {
    // Defers and memoizes the arguments and context fields for a single logging statement.
    this.core = core;
    this.context = context;
    this.argumentFields = memoize(arguments);
    this.loggerFields = memoize(context::getLoggerFields);
    this.joinedFields = memoize(joinFields(this.loggerFields, this.argumentFields));
  }

  public Log4JLoggingContext(CoreLogger core, Log4JCoreLogger.Context context) {
    this(core, context, Collections::emptyList);
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

  @Override
  public CoreLogger getCore() {
    return core;
  }
}
