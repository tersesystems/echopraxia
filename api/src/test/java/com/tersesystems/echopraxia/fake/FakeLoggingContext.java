package com.tersesystems.echopraxia.fake;

import com.tersesystems.echopraxia.api.AbstractLoggingContext;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.LoggingContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public class FakeLoggingContext extends AbstractLoggingContext implements LoggingContext {
  private static final FakeLoggingContext EMPTY =
      new FakeLoggingContext(Collections::emptyList, Collections::emptyList);
  protected final Supplier<List<Field>> loggerFields;
  protected final Supplier<List<Field>> argumentFields;

  protected FakeLoggingContext(
      Supplier<List<Field>> loggerFields, Supplier<List<Field>> argumentFields) {
    this.loggerFields = loggerFields;
    this.argumentFields = argumentFields;
  }

  public static FakeLoggingContext single(Field field) {
    return new FakeLoggingContext(() -> Collections.singletonList(field), Collections::emptyList);
  }

  public static FakeLoggingContext of(Field... fields) {
    return new FakeLoggingContext(() -> Arrays.asList(fields), Collections::emptyList);
  }

  public static FakeLoggingContext empty() {
    return EMPTY;
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

  public FakeLoggingContext and(FakeLoggingContext context) {
    if (context == null) {
      return this;
    }

    Supplier<List<Field>> lfields =
        joinFields(FakeLoggingContext.this::getLoggerFields, context::getLoggerFields);
    Supplier<List<Field>> afields =
        joinFields(FakeLoggingContext.this::getArgumentFields, context::getArgumentFields);
    return new FakeLoggingContext(lfields, afields);
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
}
