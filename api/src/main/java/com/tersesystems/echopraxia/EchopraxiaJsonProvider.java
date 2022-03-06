package com.tersesystems.echopraxia;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.spi.json.JsonProvider;
import java.io.InputStream;
import java.util.*;
import org.jetbrains.annotations.NotNull;

public class EchopraxiaJsonProvider implements JsonProvider {

  @Override
  public Object parse(String json) throws InvalidJsonException {
    throw new InvalidJsonException("Not implemented");
  }

  @Override
  public Object parse(InputStream jsonStream, String charset) throws InvalidJsonException {
    throw new InvalidJsonException("Not implemented");
  }

  @Override
  public String toJson(Object obj) {
    throw new InvalidJsonException("Not implemented");
  }

  @Override
  public boolean isMap(Object obj) {
    // System.out.println("isMap: obj " + obj);
    return (obj instanceof LoggingContext) || (obj instanceof Field.Value.ObjectValue);
  }

  @Override
  public Object unwrap(Object obj) {
    // System.out.println("unwrap: obj " + obj);
    if (obj instanceof Field.Value) {
      return ((Field.Value<?>) obj).raw();
    }
    return obj;
  }

  public boolean isArray(Object obj) {
    // System.out.println("isArray: obj " + obj);

    return (obj instanceof Field.Value.ArrayValue || obj instanceof java.util.List);
  }

  public int length(Object obj) {
    // System.out.println("length: obj " + obj);
    if (isArray(obj)) {
      return arraySize(obj);
    } else if (isMap(obj)) {
      return getPropertyKeys(obj).size();
    } else if (obj instanceof String) {
      return ((String) obj).length();
    }
    throw new JsonPathException(
        "length operation cannot be applied to "
            + (obj != null ? obj.getClass().getName() : "null"));
  }

  @Override
  public Iterable<?> toIterable(Object obj) {
    // System.out.println("toIterable: obj " + obj);
    if (isArray(obj)) return ((Iterable) obj);
    else
      throw new JsonPathException(
          "Cannot iterate over " + obj != null ? obj.getClass().getName() : "null");
  }

  @Override
  public Collection<String> getPropertyKeys(Object obj) {
    // System.out.println("getPropertyKeys: obj " + obj);
    if (isArray(obj)) {
      throw new UnsupportedOperationException();
    } else {
      // XXX Needs to be fixed
      return ((Map) obj).keySet();
    }
  }

  private int arraySize(Object obj) {
    // System.out.println("setProperty: obj " + obj);
    if (obj instanceof Field.Value.ArrayValue) {
      return ((Field.Value.ArrayValue) obj).raw().size();
    }
    if (obj instanceof List) {
      return ((List<?>) obj).size();
    }
    throw new JsonPathException(
        "length operation cannot be applied to "
            + (obj != null ? obj.getClass().getName() : "null"));
  }

  public void setProperty(Object obj, Object key, Object value) {
    // XXX Figure out if this is used internally
    if (isMap(obj)) {
      ((Map) obj).put(key.toString(), value);
    } else {
      throw new JsonPathException(
          "setProperty operation cannot be used with " + obj != null
              ? obj.getClass().getName()
              : "null");
    }
  }

  @Override
  public void removeProperty(Object obj, Object key) {
    // System.out.println("removeProperty: obj " + obj);
    if (isMap(obj)) ((Map) obj).remove(key.toString());
    else {
      List list = (List) obj;
      int index = key instanceof Integer ? (Integer) key : Integer.parseInt(key.toString());
      list.remove(index);
    }
  }

  public Object getArrayIndex(Object obj, int idx) {
    // System.out.println("getArrayIndex: obj " + obj);
    if (obj instanceof Field.Value.ArrayValue) {
      final List<Field.Value<?>> raw = ((Field.Value.ArrayValue) obj).raw();
      return raw.get(idx);
    }
    if (obj instanceof List) {
      return ((List<?>) obj).get(idx);
    }

    throw new IllegalArgumentException("Object is not valid: " + obj.toString());
  }

  @Override
  public Object getArrayIndex(Object obj, int idx, boolean unwrap) {
    return ((List) obj).get(idx);
  }

  @Override
  public void setArrayIndex(Object array, int idx, Object newValue) {
    // this is used to answer queries from created lists
    if (!isArray(array)) {
      throw new UnsupportedOperationException();
    } else {
      List l = (List) array;
      if (idx == l.size()) {
        l.add(newValue);
      } else {
        l.set(idx, newValue);
      }
    }
  }

  @Override
  public Object getMapValue(Object obj, String key) {
    // System.out.println("getMapValue: obj " + obj);
    if (obj instanceof LoggingContext) {
      return findValue(key, ((LoggingContext) obj).getFields());
    }
    if (obj instanceof Field.Value.ObjectValue) {
      return findValue(key, ((Field.Value.ObjectValue) obj).raw());
    }
    return JsonProvider.UNDEFINED;
  }

  @NotNull
  private Object findValue(String key, List<Field> fields) {
    // This is O(N), so it will be slower when there are large lists.
    final Optional<? extends Field.Value<?>> first =
        fields.stream().filter(f -> f.name().equals(key)).map(Field::value).findFirst();
    return first.isPresent() ? first.get() : JsonProvider.UNDEFINED;
  }

  @Override
  public List<Object> createArray() {
    // System.out.println("createArray:");
    return new LinkedList<Object>();
  }

  @Override
  public Object createMap() {
    // System.out.println("createMap:");
    return new LinkedHashMap<String, Object>();
  }
}
