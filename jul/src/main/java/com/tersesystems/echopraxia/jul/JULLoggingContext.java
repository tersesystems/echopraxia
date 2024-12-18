package com.tersesystems.echopraxia.jul;

import static com.tersesystems.echopraxia.spi.Utilities.joinFields;
import static com.tersesystems.echopraxia.spi.Utilities.memoize;

import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.LoggingContext;
import com.tersesystems.echopraxia.spi.AbstractJsonPathFinder;
import com.tersesystems.echopraxia.spi.CoreLogger;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class JULLoggingContext extends AbstractJsonPathFinder implements LoggingContext {
  private final Supplier<List<Field>> argumentFields;
  private final Supplier<List<Field>> loggerFields;
  private final Supplier<List<Field>> joinedFields;
  private final CoreLogger core;

  // Allow an empty context for testing
  public JULLoggingContext(CoreLogger core) {
    this.core = core;
    this.argumentFields = Collections::emptyList;
    this.loggerFields = Collections::emptyList;
    this.joinedFields = Collections::emptyList;
  }

  public JULLoggingContext(
      CoreLogger core, JULLoggerContext context, Supplier<List<Field>> arguments) {
    // Defers and memoizes the arguments and context fields for a single logging statement.
    this.core = core;
    this.argumentFields = memoize(arguments);
    this.loggerFields = memoize(context::getLoggerFields);
    this.joinedFields = memoize(joinFields(this.loggerFields, this.argumentFields));
  }

  public JULLoggingContext(CoreLogger core, JULLoggerContext context) {
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

  @Override
  public CoreLogger getCore() {
    return core;
  }
}
