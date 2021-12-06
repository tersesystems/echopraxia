package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerProvider;
import org.slf4j.ILoggerFactory;

public class LogstashLoggerProvider implements LoggerProvider {

  public Logger<Field.Builder> getLogger(Class<?> clazz) {
    return getLogger(clazz.getName());
  }

  public Logger<Field.Builder> getLogger(String name) {
    ILoggerFactory factory = org.slf4j.LoggerFactory.getILoggerFactory();
    org.slf4j.Logger logger = factory.getLogger(name);
    return new Logger<>(new LogstashCoreLogger(logger), Logger.defaultFieldBuilder());
  }
}
