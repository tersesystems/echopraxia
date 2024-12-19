package echopraxia.scripting;

import echopraxia.api.Field;
import echopraxia.logging.api.LoggingContext;
import echopraxia.logging.spi.AbstractJsonPathFinder;
import echopraxia.logging.spi.CoreLogger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FakeLoggingContext extends AbstractJsonPathFinder implements LoggingContext {
  private final List<Field> loggerFields;

  public FakeLoggingContext(Field... loggerFields) {
    this.loggerFields = Arrays.asList(loggerFields);
  }

  @Override
  public @Nullable CoreLogger getCore() {
    return null;
  }

  @Override
  public @NotNull List<Field> getFields() {
    return loggerFields;
  }

  @Override
  public List<Field> getArgumentFields() {
    return Collections.emptyList();
  }

  @Override
  public List<Field> getLoggerFields() {
    return loggerFields;
  }
}
