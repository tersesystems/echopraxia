package echopraxia.logging.api;

import echopraxia.api.FieldBuilder;
import echopraxia.logging.spi.CoreLogger;
import echopraxia.logging.spi.CoreLoggerFilter;

public class TestFilter implements CoreLoggerFilter {
  @Override
  public CoreLogger apply(CoreLogger coreLogger) {
    return coreLogger.withFields(
        fb -> fb.string("example_field", "example_value"), FieldBuilder.instance());
  }
}
