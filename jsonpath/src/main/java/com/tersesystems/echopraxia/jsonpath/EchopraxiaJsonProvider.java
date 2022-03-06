package com.tersesystems.echopraxia.jsonpath;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.json.AbstractJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.LoggingContext;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class EchopraxiaJsonProvider extends AbstractJsonProvider {

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
    return (obj instanceof LoggingContext);
  }

  public Object getMapValue(Object obj, String key) {
    LoggingContext m = (LoggingContext) obj;
    final List<Field> fields = m.getFields();

    if (!m.containsKey(key)) {
      return JsonProvider.UNDEFINED;
    } else {
      return m.get(key);
    }
  }

  @Override
  public List<Object> createArray() {
    return new LinkedList<Object>();
  }

  @Override
  public Object createMap() {
    return new LinkedHashMap<String, Object>();
  }
}
