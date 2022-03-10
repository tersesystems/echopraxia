package com.tersesystems.echopraxia.support;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.tersesystems.echopraxia.EchopraxiaJsonProvider;
import com.tersesystems.echopraxia.EchopraxiaMappingProvider;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.LoggingContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractLoggingContext implements LoggingContext {

  private static final JsonProvider jsonProvider = new EchopraxiaJsonProvider();
  private static final MappingProvider mappingProvider = new EchopraxiaMappingProvider();

  private static final Configuration configuration =
      Configuration.builder()
          .jsonProvider(jsonProvider)
          .mappingProvider(mappingProvider)
          .options(Option.DEFAULT_PATH_LEAF_TO_NULL)
          .build();

  @Override
  public @NotNull Optional<String> findString(@NotNull String jsonPath) {
    final String s = JsonPath.parse(this, configuration).read(jsonPath, String.class);
    return Optional.ofNullable(s);
  }

  @Override
  @NotNull
  public Optional<Boolean> findBoolean(@NotNull String jsonPath) {
    final Boolean b = JsonPath.parse(this, configuration).read(jsonPath, Boolean.class);
    return Optional.ofNullable(b);
  }

  @Override
  @NotNull
  public Optional<Number> findNumber(@NotNull String jsonPath) {
    final Number n = JsonPath.parse(this, configuration).read(jsonPath, Number.class);
    return Optional.ofNullable(n);
  }

  public boolean findNull(@NotNull String jsonPath) {
    Object o = JsonPath.parse(this, configuration).read(jsonPath);
    return o instanceof Field.Value.NullValue;
  }

  @Override
  @NotNull
  public Optional<Throwable> findThrowable(@NotNull String jsonPath) {
    final Throwable t = JsonPath.parse(this, configuration).read(jsonPath, Throwable.class);
    return Optional.ofNullable(t);
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
    return Optional.ofNullable(JsonPath.parse(this, configuration).read(jsonPath, Map.class));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> @NotNull Optional<Map<String, T>> findObject(
      @NotNull String jsonPath, Predicate... predicates) {
    return Optional.ofNullable(
        JsonPath.parse(this, configuration).read(jsonPath, Map.class, predicates));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> @NotNull List<T> findList(@NotNull String jsonPath) {
    return (List<T>) JsonPath.parse(this, configuration).read(jsonPath, List.class);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> @NotNull List<T> findList(@NotNull String jsonPath, Predicate... predicates) {
    return (List<T>) JsonPath.parse(this, configuration).read(jsonPath, List.class, predicates);
  }
}
