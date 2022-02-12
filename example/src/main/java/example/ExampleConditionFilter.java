package example;

import ch.qos.logback.classic.Logger;
import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerFilter;
import com.tersesystems.echopraxia.logstash.LogstashCoreLogger;
import java.util.function.Supplier;

public class ExampleConditionFilter implements CoreLoggerFilter {

  private static final Condition globalCondition =
      (level, context) -> {
        return true;
      };

  @Override
  public Supplier<CoreLogger> apply(Supplier<CoreLogger> supplier) {
    return () -> {
      CoreLogger coreLogger = supplier.get();
      LogstashCoreLogger logstashCoreLogger = (LogstashCoreLogger) coreLogger;
      Logger logger = logstashCoreLogger.logger();
      // XXX should have a coreLogger.getName()
      return coreLogger.withCondition(globalCondition);
    };
  }
}
