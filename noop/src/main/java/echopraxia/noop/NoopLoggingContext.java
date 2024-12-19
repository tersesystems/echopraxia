package echopraxia.noop;

import echopraxia.api.Field;
import echopraxia.api.LoggingContext;
import echopraxia.spi.AbstractJsonPathFinder;
import echopraxia.spi.CoreLogger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public class NoopLoggingContext extends AbstractJsonPathFinder implements LoggingContext {
  protected final Supplier<List<Field>> loggerFields;
  protected final Supplier<List<Field>> argumentFields;
  private final CoreLogger core;

  protected NoopLoggingContext(
      CoreLogger core, Supplier<List<Field>> loggerFields, Supplier<List<Field>> argumentFields) {
    this.core = core;
    this.loggerFields = loggerFields;
    this.argumentFields = argumentFields;
  }

  public static NoopLoggingContext single(CoreLogger core, Field field) {
    return new NoopLoggingContext(
        core, () -> Collections.singletonList(field), Collections::emptyList);
  }

  public static NoopLoggingContext of(CoreLogger core, Field... fields) {
    return new NoopLoggingContext(core, () -> Arrays.asList(fields), Collections::emptyList);
  }

  public static NoopLoggingContext empty(CoreLogger core) {
    return new NoopLoggingContext(core, Collections::emptyList, Collections::emptyList);
  }

  @Override
  public @NotNull List<Field> getFields() {
    return Stream.concat(loggerFields.get().stream(), argumentFields.get().stream())
        .collect(Collectors.toList());
  }

  @Override
  public List<Field> getArgumentFields() {
    return argumentFields.get();
  }

  @Override
  public List<Field> getLoggerFields() {
    return loggerFields.get();
  }

  public NoopLoggingContext and(NoopLoggingContext context) {
    if (context == null) {
      return this;
    }

    Supplier<List<Field>> lfields =
        joinFields(NoopLoggingContext.this::getLoggerFields, context::getLoggerFields);
    Supplier<List<Field>> afields =
        joinFields(NoopLoggingContext.this::getArgumentFields, context::getArgumentFields);
    return new NoopLoggingContext(this.core, lfields, afields);
  }

  private Supplier<List<Field>> joinFields(
      Supplier<List<Field>> first, Supplier<List<Field>> second) {
    return () -> {
      List<Field> firstFields = first.get();
      List<Field> secondFields = second.get();

      if (firstFields.isEmpty()) {
        return secondFields;
      } else if (secondFields.isEmpty()) {
        return firstFields;
      } else {
        // Stream.concat is actually faster than explicit ArrayList!
        // https://blog.soebes.de/blog/2020/03/31/performance-stream-concat/
        return Stream.concat(firstFields.stream(), secondFields.stream())
            .collect(Collectors.toList());
      }
    };
  }

  public LoggingContext withFields(Supplier<List<Field>> extraFields) {
    Supplier<List<Field>> lfields =
        joinFields(NoopLoggingContext.this::getLoggerFields, extraFields);
    return new NoopLoggingContext(this.core, lfields, this::getArgumentFields);
  }

  @Override
  public CoreLogger getCore() {
    return core;
  }
}
