package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerProvider;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;

public class Log4JCoreLoggerProvider implements CoreLoggerProvider {

  @Override
  public @NotNull CoreLogger getLogger(@NotNull Class<?> clazz) {
    return getLogger(clazz.getName());
  }

  @Override
  public @NotNull CoreLogger getLogger(@NotNull String name) {
    return new Log4JCoreLogger(LogManager.getLogger(name));
  }
}
