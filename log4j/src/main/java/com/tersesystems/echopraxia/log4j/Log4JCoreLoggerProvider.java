package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerProvider;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;

public class Log4JCoreLoggerProvider implements CoreLoggerProvider {

  @Override
  public CoreLogger getLogger(Class<?> clazz) {
    return getLogger(clazz.getName());
  }

  @Override
  public CoreLogger getLogger(String name) {
    org.apache.logging.log4j.Logger log4jLogger = LogManager.getLogger(name);
    Log4JLoggingContext context = new Log4JLoggingContext(Collections::emptyList, null);
    Condition condition = Condition.always();
    return new Log4JCoreLogger(log4jLogger, context, condition);
  }
}
