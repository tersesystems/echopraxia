package com.tersesystems.echopraxia;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.mapper.MappingException;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EchopraxiaMappingProvider implements MappingProvider {

  @SuppressWarnings("unchecked")
  @Override
  public <T> T map(
      @Nullable Object source, @NotNull Class<T> targetType, Configuration configuration) {
    if (source == null) {
      return null;
    }

    if (targetType.equals(Object.class)
        || targetType.equals(java.util.List.class)
        || targetType.equals(Map.class)) {
      return (T) mapToObject(source);
    }

    if (targetType.equals(String.class)
        || Number.class.isAssignableFrom(targetType)
        || targetType.equals(Boolean.class)
        || targetType.equals(Throwable.class)) {
      return mapToValue(source);
    }

    return (T) source;
  }

  @SuppressWarnings("unchecked")
  @Nullable
  private <T> T mapToValue(@Nullable Object source) {
    if (source == null) {
      return null;
    }

    if (source instanceof Field.Value) {
      Field.Value<?> value = (Field.Value<?>) source;
      switch (value.type()) {
        case STRING:
        case EXCEPTION:
        case BOOLEAN:
        case NUMBER:
          return (T) value.raw();
        case NULL:
          return null;
        default:
          throw new JsonPathException("No match for value " + value);
      }
    }
    return (T) source;
  }

  private Map<String, ?> mapThrowable(Throwable t) {
    return null;
  }

  @Override
  public <T> T map(Object source, TypeRef<T> targetType, Configuration configuration) {
    try {
      String msg = "TypeRef are not supported, source = " + source + " targetType = " + targetType;
      throw new UnsupportedOperationException(msg);
    } catch (Exception e) {
      throw new MappingException(e);
    }
  }

  @SuppressWarnings("unchecked")
  @Nullable
  private Object mapToObject(@Nullable Object source) {
    if (source == null) {
      return null;
    }
    if (source == Field.Value.NullValue.instance) {
      return null;
    }

    if (source instanceof List) {
      List<Object> mapped = new ArrayList<>();
      List<?> array = (List<?>) source;
      for (Object value : array) {
        mapped.add(mapToObject(value));
      }
      return mapped;
    } else if (source instanceof Map) {
      Map<String, Object> mapped = new HashMap<>();
      Map<String, ?> sourceMap = (Map<String, ?>) source;
      for (String key : sourceMap.keySet()) {
        final Object mapValue = sourceMap.get(key);
        mapped.put(key, mapToObject(mapValue));
      }
      return mapped;
    } else if (source instanceof Field.Value.ArrayValue) {
      List<Object> mapped = new ArrayList<>();
      Field.Value.ArrayValue arrayValue = (Field.Value.ArrayValue) source;
      final List<Field.Value<?>> array = arrayValue.raw();

      for (Field.Value<?> value : array) {
        mapped.add(mapToObject(value));
      }
      return mapped;
    } else if (source instanceof Field.Value.ObjectValue) {
      Map<String, Object> mapped = new HashMap<>();
      Field.Value.ObjectValue objValue = (Field.Value.ObjectValue) source;
      final List<Field> raw = objValue.raw();

      for (Field f : raw) {
        mapped.put(f.name(), mapToObject(f.value()));
      }
      return mapped;
    } else if (source instanceof Field.Value) {
      return ((Field.Value<?>) source).raw();
    } else {
      throw new JsonPathException("Could not determine value type for " + source.getClass());
    }
  }
}
