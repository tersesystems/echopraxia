package com.tersesystems.echopraxia;

import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerFilter;

public class TestFilter implements CoreLoggerFilter {
  @Override
  public CoreLogger apply(CoreLogger coreLogger) {
    return coreLogger.withFields(
        fb -> fb.onlyString("example_field", "example_value"), Field.Builder.instance());
  }
}
