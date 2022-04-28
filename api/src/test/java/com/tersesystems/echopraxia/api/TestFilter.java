package com.tersesystems.echopraxia.api;

public class TestFilter implements CoreLoggerFilter {
  @Override
  public CoreLogger apply(CoreLogger coreLogger) {
    return coreLogger.withFields(
        fb -> fb.onlyString("example_field", "example_value"), FieldBuilder.instance());
  }
}
