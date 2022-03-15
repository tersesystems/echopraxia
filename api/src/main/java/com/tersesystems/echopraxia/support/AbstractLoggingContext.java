package com.tersesystems.echopraxia.support;

import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.tersesystems.echopraxia.EchopraxiaJsonProvider;
import com.tersesystems.echopraxia.EchopraxiaMappingProvider;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.LoggingContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractLoggingContext implements LoggingContext {

  private static final JsonProvider jsonProvider = new EchopraxiaJsonProvider();
  private static final MappingProvider mappingProvider = new EchopraxiaMappingProvider();

  private static final Configuration configuration =
      Configuration.builder()
          .jsonProvider(jsonProvider)
          .mappingProvider(mappingProvider)
          .options(Option.DEFAULT_PATH_LEAF_TO_NULL)
          .options(Option.SUPPRESS_EXCEPTIONS)
          .build();

  private final Supplier<DocumentContext> supplier =
      new Utilities.MemoizingSupplier<>(() -> JsonPath.parse(this, configuration));

  @Override
  public @NotNull Optional<String> findString(@NotNull String jsonPath) {
    final String s = getDocumentContext().read(jsonPath, String.class);
    return Optional.ofNullable(s);
  }

  @Override
  @NotNull
  public Optional<Boolean> findBoolean(@NotNull String jsonPath) {
    final Boolean b = getDocumentContext().read(jsonPath, Boolean.class);
    return Optional.ofNullable(b);
  }

  @Override
  @NotNull
  public Optional<Number> findNumber(@NotNull String jsonPath) {
    final Number n = getDocumentContext().read(jsonPath, Number.class);
    return Optional.ofNullable(n);
  }

  public boolean findNull(@NotNull String jsonPath) {
    Object o = getDocumentContext().read(jsonPath);
    return o instanceof Field.Value.NullValue;
  }

  @Override
  @NotNull
  public Optional<Throwable> findThrowable(@NotNull String jsonPath) {
    try {
      final Throwable t = getDocumentContext().read(jsonPath, Throwable.class);
      return Optional.ofNullable(t);
    } catch (PathNotFoundException pe) {
      return Optional.empty();
    }
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
    return Optional.ofNullable(getDocumentContext().read(jsonPath, Map.class));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> @NotNull Optional<Map<String, T>> findObject(
      @NotNull String jsonPath, Predicate... predicates) {
    return Optional.ofNullable(getDocumentContext().read(jsonPath, Map.class, predicates));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> @NotNull List<T> findList(@NotNull String jsonPath) {
    return (List<T>) getDocumentContext().read(jsonPath, List.class);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> @NotNull List<T> findList(@NotNull String jsonPath, Predicate... predicates) {
    return (List<T>) getDocumentContext().read(jsonPath, List.class, predicates);
  }

  private DocumentContext getDocumentContext() {
    return supplier.get();
  }
}