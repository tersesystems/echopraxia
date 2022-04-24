package com.tersesystems.echopraxia.support;

import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.tersesystems.echopraxia.EchopraxiaJsonProvider;
import com.tersesystems.echopraxia.EchopraxiaMappingProvider;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Field.Value.*;
import com.tersesystems.echopraxia.LoggingContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractLoggingContext implements LoggingContext {

  private static final JsonProvider jsonProvider = new EchopraxiaJsonProvider();
  private static final MappingProvider javaMappingProvider = new EchopraxiaMappingProvider();

  private static final Configuration configuration =
      Configuration.builder()
          .jsonProvider(jsonProvider)
          .options(Option.DEFAULT_PATH_LEAF_TO_NULL)
          .options(Option.SUPPRESS_EXCEPTIONS)
          .mappingProvider(javaMappingProvider)
          .build();

  private final Supplier<DocumentContext> supplier =
      new Utilities.MemoizingSupplier<>(() -> JsonPath.parse(this, configuration));

  @Override
  public @NotNull <T extends Field.Value<?>> Optional<T> findValue(
      @NotNull String jsonPath, @NotNull Class<T> valueClass, Predicate... predicates) {
    final Field.Value<?> s = getDocumentContext().read(jsonPath, Field.Value.class, predicates);
    if (valueClass.isInstance(s)) {
      return Optional.of(valueClass.cast(s));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public @NotNull <T extends Field.Value<?>> Optional<T> findValue(
      @NotNull String jsonPath, @NotNull Class<T> valueClass) {
    final Field.Value<?> s = getDocumentContext().read(jsonPath, Field.Value.class);
    if (valueClass.isInstance(s)) {
      return Optional.of(valueClass.cast(s));
    } else {
      return Optional.empty();
    }
  }

  @Override
  @NotNull
  public Optional<String> findString(@NotNull String jsonPath) {
    return findValue(jsonPath, StringValue.class).map(StringValue::raw);
  }

  @Override
  @NotNull
  public Optional<Boolean> findBoolean(@NotNull String jsonPath) {
    return findValue(jsonPath, BooleanValue.class).map(BooleanValue::raw);
  }

  @Override
  @NotNull
  public Optional<Number> findNumber(@NotNull String jsonPath) {
    return findValue(jsonPath, NumberValue.class).map(NumberValue::raw);
  }

  public boolean findNull(@NotNull String jsonPath) {
    Object o = getDocumentContext().read(jsonPath);
    return o instanceof Field.Value.NullValue;
  }

  @Override
  @NotNull
  public Optional<Throwable> findThrowable(@NotNull String jsonPath) {
    return findValue(jsonPath, ExceptionValue.class).map(ExceptionValue::raw);
  }

  @Override
  @NotNull
  public Optional<Throwable> findThrowable() {
    String jsonPath = "$." + Field.Builder.EXCEPTION;
    return findThrowable(jsonPath);
  }

  @SuppressWarnings("unchecked")
  @Override
  @NotNull
  public <T> Optional<Map<String, T>> findObject(@NotNull String jsonPath) {
    Optional<ObjectValue> objectValue = findValue(jsonPath, ObjectValue.class);
    return objectValue.map(obj -> javaMappingProvider.map(obj, Map.class, configuration));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> @NotNull Optional<Map<String, T>> findObject(
      @NotNull String jsonPath, Predicate... predicates) {
    // XXX fix this
    return Optional.ofNullable(getDocumentContext().read(jsonPath, Map.class, predicates));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> @NotNull List<T> findList(@NotNull String jsonPath) {
    Optional<ArrayValue> arrayValue = findValue(jsonPath, Field.Value.ArrayValue.class);
    if (arrayValue.isPresent()) {
      @NotNull List<Field.Value<?>> list = arrayValue.get().raw();
      return javaMappingProvider.map(list, List.class, configuration);
    } else {
      return Collections.emptyList();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> @NotNull List<T> findList(@NotNull String jsonPath, Predicate... predicates) {
    // XXX fix this
    return (List<T>) getDocumentContext().read(jsonPath, List.class, predicates);
  }

  private DocumentContext getDocumentContext() {
    return supplier.get();
  }
}
