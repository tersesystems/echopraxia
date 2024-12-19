package echopraxia.api;

import echopraxia.spi.CoreLogger;
import echopraxia.spi.CoreLoggerFilter;

public class TestFilter implements CoreLoggerFilter {
  @Override
  public CoreLogger apply(CoreLogger coreLogger) {
    return coreLogger.withFields(
        fb -> fb.string("example_field", "example_value"), FieldBuilder.instance());
  }
}
