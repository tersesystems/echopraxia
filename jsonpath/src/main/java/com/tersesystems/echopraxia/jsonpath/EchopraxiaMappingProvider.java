package com.tersesystems.echopraxia.jsonpath;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.mapper.MappingException;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

public class EchopraxiaMappingProvider implements MappingProvider {

  @Override
  public <T> T map(Object source, Class<T> targetType, Configuration configuration) {
    try {
      String msg = "source = " + source + " targetType = " + targetType;
      throw new UnsupportedOperationException(msg);
    } catch (Exception e) {
      throw new MappingException(e);
    }
  }

  @Override
  public <T> T map(Object source, TypeRef<T> targetType, Configuration configuration) {
    try {
      String msg = "source = " + source + " targetType = " + targetType;
      throw new UnsupportedOperationException(msg);
    } catch (Exception e) {
      throw new MappingException(e);
    }
  }
}
