package echopraxia.logback;

import static echopraxia.spi.Utilities.memoize;

import ch.qos.logback.classic.spi.ILoggingEvent;
import echopraxia.api.Field;
import echopraxia.spi.CoreLogger;
import java.util.*;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class FieldLoggingContext extends AbstractEventLoggingContext {

  private final CoreLogger core;

  private final Supplier<List<Field>> argumentFields;
  private final Supplier<List<Field>> markerFields;

  private final Supplier<List<Field>> fields;

  public FieldLoggingContext(CoreLogger core, @NotNull ILoggingEvent event) {
    this.core = core;
    this.argumentFields = memoize(() -> fieldArguments(event));
    this.markerFields = memoize(() -> fieldMarkers(event));
    this.fields =
        memoize(
            () -> {
              List<Field> fields = new ArrayList<>();
              fields.addAll(getArgumentFields()); // argument fields should take precedence
              fields.addAll(getLoggerFields());
              return fields;
            });
  }

  @Override
  public CoreLogger getCore() {
    return this.core;
  }

  @Override
  public @NotNull List<Field> getFields() {
    return fields.get();
  }

  @Override
  public @NotNull List<Field> getLoggerFields() {
    return markerFields.get();
  }

  @Override
  public @NotNull List<Field> getArgumentFields() {
    return argumentFields.get();
  }
}
