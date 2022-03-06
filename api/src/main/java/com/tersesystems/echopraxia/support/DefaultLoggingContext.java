package com.tersesystems.echopraxia.support;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.LoggingContext;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DefaultLoggingContext extends LoggingContext {

  @Override
  default int size() {
    return getFields().size();
  }

  @Override
  default boolean isEmpty() {
    return size() == 0;
  }

  @Override
  default boolean containsKey(Object key) {
    return getFields().stream().anyMatch(f -> f.name() == key);
  }

  @Override
  default boolean containsValue(Object value) {
    if (!(value instanceof Field.Value<?>)) {
      return false;
    }
    return getFields().stream().anyMatch(f -> f.value().equals(value));
  }

  @Override
  default Field.Value<?> get(Object key) {
    return getFields().stream()
        .filter(f -> f.name() == key)
        .map(Field::value)
        .findFirst()
        .orElse(null);
  }

  @Nullable
  @Override
  default Field.Value<?> put(String key, Field.Value<?> value) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  default Field.Value<?> remove(Object key) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  default void putAll(@NotNull Map<? extends String, ? extends Field.Value<?>> m) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  default void clear() {
    throw new UnsupportedOperationException("Not implemented");
  }

  @NotNull
  @Override
  default Set<String> keySet() {
    return getFields().stream().map(Field::name).collect(Collectors.toSet());
  }

  @NotNull
  @Override
  default Collection<Field.Value<?>> values() {
    return getFields().stream().map(Field::value).collect(Collectors.toList());
  }

  @NotNull
  @Override
  default Set<Map.Entry<String, Field.Value<?>>> entrySet() {
    Map<String, Field.Value<?>> entries =
        getFields().stream().collect(Collectors.toMap(Field::name, Field::value));
    return entries.entrySet();
  }
}
