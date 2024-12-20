package echopraxia.jsonpath;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.mapper.MappingException;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import echopraxia.api.Field;
import echopraxia.api.FieldConstants;
import echopraxia.api.Value;
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

    if (source instanceof Value) {
      Value<?> value = (Value<?>) source;
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
    if (source == Value.NullValue.instance) {
      return null;
    }

    if (source instanceof List) {
      return getList((List<?>) source);
    } else if (source instanceof Map) {
      return getMap((Map<String, ?>) source);
    } else if (source instanceof Value.ArrayValue) {
      return getArrayValue((Value.ArrayValue) source);
    } else if (source instanceof Value.ObjectValue) {
      return getObjectValue((Value.ObjectValue) source);
    } else if (source instanceof Value) {
      return ((Value<?>) source).raw();
    } else if (source instanceof StackTraceElement[]) {
      return getStackTraceElements((StackTraceElement[]) source);
    } else if (source instanceof StackTraceElement) {
      return getStackTraceElement((StackTraceElement) source);
    } else {
      throw new JsonPathException("Could not determine value type for " + source.getClass());
    }
  }

  @NotNull
  private Map<String, Object> getStackTraceElement(StackTraceElement source) {
    Map<String, Object> elementMap = new HashMap<>(8);
    elementMap.put(FieldConstants.METHOD_NAME, source.getMethodName());
    elementMap.put(FieldConstants.CLASS_NAME, source.getClassName());
    elementMap.put(FieldConstants.FILE_NAME, source.getFileName());
    elementMap.put(FieldConstants.LINE_NUMBER, source.getLineNumber());
    return elementMap;
  }

  @NotNull
  private List<Object> getStackTraceElements(StackTraceElement[] source) {
    List<Object> mapped = new ArrayList<>(source.length);
    for (StackTraceElement value : source) {
      mapped.add(mapToObject(value));
    }
    return mapped;
  }

  @NotNull
  private Map<String, Object> getObjectValue(Value.ObjectValue source) {
    Map<String, Object> mapped = new HashMap<>();
    final List<Field> raw = source.raw();
    for (Field f : raw) {
      mapped.put(f.name(), mapToObject(f.value()));
    }
    return mapped;
  }

  @NotNull
  private List<Object> getArrayValue(Value.ArrayValue source) {
    final List<Value<?>> array = source.raw();
    List<Object> mapped = new ArrayList<>(array.size());

    for (Value<?> value : array) {
      mapped.add(mapToObject(value));
    }
    return mapped;
  }

  @NotNull
  private Map<String, Object> getMap(Map<String, ?> source) {
    Map<String, Object> mapped = new HashMap<>();
    for (String key : source.keySet()) {
      final Object mapValue = source.get(key);
      mapped.put(key, mapToObject(mapValue));
    }
    return mapped;
  }

  @NotNull
  private List<Object> getList(List<?> source) {
    List<Object> mapped = new ArrayList<>(source.size());
    for (Object value : source) {
      mapped.add(mapToObject(value));
    }
    return mapped;
  }
}
