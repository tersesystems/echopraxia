package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerProvider;
import org.slf4j.ILoggerFactory;

/**
 * Logstash implementation of a logger provider.
 *
 * <p>This is the main SPI hook into the ServiceLoader.
 */
public class LogstashLoggerProvider implements CoreLoggerProvider {

  public CoreLogger getLogger(Class<?> clazz) {
    return getLogger(clazz.getName());
  }

  public CoreLogger getLogger(String name) {
    ILoggerFactory factory = org.slf4j.LoggerFactory.getILoggerFactory();
    org.slf4j.Logger logger = factory.getLogger(name);
    return new LogstashCoreLogger(logger);
  }
}
