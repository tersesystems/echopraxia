package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerProvider;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;

public class Log4JLoggerProvider implements LoggerProvider {

  @Override
  public Logger<Field.Builder> getLogger(Class<?> clazz) {
    return getLogger(clazz.getName());
  }

  @Override
  public Logger<Field.Builder> getLogger(String name) {
    org.apache.logging.log4j.Logger log4jLogger = LogManager.getLogger(name);
    Log4JLoggingContext context =
        new Log4JLoggingContext(Collections::emptyList, Collections::emptyList);
    Condition condition = Condition.always();
    Log4JCoreLogger coreLogger = new Log4JCoreLogger(log4jLogger, context, condition);
    return new Logger<>(coreLogger, Logger.defaultFieldBuilder());
  }
}
