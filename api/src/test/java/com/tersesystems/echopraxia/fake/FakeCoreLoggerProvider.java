package com.tersesystems.echopraxia.fake;

import com.tersesystems.echopraxia.api.CoreLogger;
import com.tersesystems.echopraxia.api.CoreLoggerProvider;

public class FakeCoreLoggerProvider implements CoreLoggerProvider {
  @Override
  public  CoreLogger getLogger( String fqcn,  Class<?> clazz) {
    return new FakeCoreLogger(fqcn);
  }

  @Override
  public  CoreLogger getLogger( String fqcn,  String name) {
    return new FakeCoreLogger(fqcn);
  }
}
