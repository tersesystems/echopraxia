package com.tersesystems.echopraxia.logstash;

import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.ILoggerFactory;

/**
 * Logstash implementation of a logger provider.
 *
 * <p>This is the main SPI hook into the ServiceLoader.
 */
public class LogstashLoggerProvider implements CoreLoggerProvider {

  public @NotNull CoreLogger getLogger(@NotNull Class<?> clazz) {
    return getLogger(clazz.getName());
  }

  public @NotNull CoreLogger getLogger(@NotNull String name) {
    ILoggerFactory factory = org.slf4j.LoggerFactory.getILoggerFactory();
    org.slf4j.Logger logger = factory.getLogger(name);
    return new LogstashCoreLogger(logger);
  }
}
