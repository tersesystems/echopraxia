package com.tersesystems.echopraxia.logstash;

import ch.qos.logback.classic.LoggerContext;
import com.tersesystems.echopraxia.api.CoreLogger;
import com.tersesystems.echopraxia.api.CoreLoggerProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Logstash implementation of a logger provider.
 *
 * <p>This is the main SPI hook into the ServiceLoader.
 */
public class LogstashLoggerProvider implements CoreLoggerProvider {

  private volatile LoggerContext loggerContext;

  static boolean asyncCallerEnabled;

  private void initialize() {
    if (loggerContext == null) {
      // block until the logger context comes up, we don't want the substitute logger factory
      StaticLoggerBinder singleton = StaticLoggerBinder.getSingleton();
      loggerContext = (LoggerContext) singleton.getLoggerFactory();
    }
  }

  public @NotNull CoreLogger getLogger(@NotNull String fqcn, @NotNull Class<?> clazz) {
    return getLogger(fqcn, clazz.getName());
  }

  public @NotNull CoreLogger getLogger(@NotNull String fqcn, @NotNull String name) {
    initialize();
    return new LogstashCoreLogger(fqcn, loggerContext.getLogger(name));
  }
}
