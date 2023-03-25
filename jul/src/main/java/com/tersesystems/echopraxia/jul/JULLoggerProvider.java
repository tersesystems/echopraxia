package com.tersesystems.echopraxia.jul;

import com.tersesystems.echopraxia.api.CoreLogger;
import com.tersesystems.echopraxia.api.CoreLoggerProvider;
import org.jetbrains.annotations.NotNull;

public class JULLoggerProvider implements CoreLoggerProvider {

  @Override
  public @NotNull CoreLogger getLogger(@NotNull String fqcn, @NotNull Class<?> clazz) {
    return getLogger(fqcn, clazz.getName());
  }

  @Override
  public @NotNull CoreLogger getLogger(@NotNull String fqcn, @NotNull String name) {
    // See LogRecord.inferCaller for details
    System.setProperty("jdk.logger.packages", "com.tersesystems.echopraxia");

    // Logger logger = logManager.getLogger(name);
    java.util.logging.Logger logger = java.util.logging.Logger.getLogger(getClass().getName());
    return new JULCoreLogger(fqcn, logger);
  }
}
