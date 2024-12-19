package echopraxia.logback;

import static echopraxia.logging.spi.Utilities.memoize;

import ch.qos.logback.classic.spi.ILoggingEvent;
import echopraxia.api.Field;
import echopraxia.logging.spi.CoreLogger;
import java.util.*;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArgumentLoggingContext extends AbstractEventLoggingContext {

  private final Supplier<List<Field>> argumentFields;
  private final CoreLogger core;

  public ArgumentLoggingContext(@Nullable CoreLogger core, @NotNull ILoggingEvent event) {
    this.core = core;
    this.argumentFields = memoize(() -> fieldArguments(event));
  }

  @Override
  public CoreLogger getCore() {
    return core;
  }

  @Override
  public @NotNull List<Field> getFields() {
    return argumentFields.get();
  }

  @Override
  public @NotNull List<Field> getLoggerFields() {
    return Collections.emptyList();
  }

  @Override
  public @NotNull List<Field> getArgumentFields() {
    return argumentFields.get();
  }
}
