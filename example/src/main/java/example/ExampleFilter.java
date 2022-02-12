package example;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerFilter;

/** This is an example filter, loaded in through /echopraxia.properties */
public class ExampleFilter implements CoreLoggerFilter {

  @Override
  public CoreLogger apply(CoreLogger coreLogger) {
    return coreLogger.withFields(fb -> fb.onlyBool("uses_filter", true), Field.Builder.instance());
  }
}
