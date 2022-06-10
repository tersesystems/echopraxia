package com.tersesystems.echopraxia.logstash;

import ch.qos.logback.classic.LoggerContext;
import com.tersesystems.echopraxia.api.CoreLogger;
import com.tersesystems.echopraxia.api.CoreLoggerProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

/**
 * Logstash implementation of a logger provider.
 *
 * <p>This is the main SPI hook into the ServiceLoader.
 */
public class LogstashLoggerProvider implements CoreLoggerProvider {

  private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

  static boolean asyncCallerEnabled = Boolean.parseBoolean(loggerContext.getProperty(LogstashCoreLogger.ECHOPRAXIA_ASYNC_CALLER_PROPERTY));

  public @NotNull CoreLogger getLogger(@NotNull String fqcn, @NotNull Class<?> clazz) {
    return getLogger(fqcn, clazz.getName());
  }

  public @NotNull CoreLogger getLogger(@NotNull String fqcn, @NotNull String name) {
    return new LogstashCoreLogger(fqcn, loggerContext.getLogger(name));
  }
}
