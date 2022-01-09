package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerProvider;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;

public class Log4JLoggerProvider implements CoreLoggerProvider {

  private final Log4JLoggingContext empty =
      new Log4JLoggingContext(Collections::emptyList, Collections::emptyList);

  @Override
  public CoreLogger getLogger(Class<?> clazz) {
    return getLogger(clazz.getName());
  }

  @Override
  public CoreLogger getLogger(String name) {
    org.apache.logging.log4j.Logger log4jLogger = LogManager.getLogger(name);
    Condition condition = Condition.always();
    return new Log4JCoreLogger(log4jLogger, empty, condition);
  }
}
