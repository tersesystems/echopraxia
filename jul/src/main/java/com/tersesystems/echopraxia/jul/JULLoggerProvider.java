package com.tersesystems.echopraxia.jul;

import com.tersesystems.echopraxia.api.CoreLogger;
import com.tersesystems.echopraxia.api.CoreLoggerProvider;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

public class JULLoggerProvider implements CoreLoggerProvider {

  @Override
  public @NotNull CoreLogger getLogger(@NotNull String fqcn, @NotNull Class<?> clazz) {
    return getLogger(fqcn, clazz.getName());
  }

  @Override
  public @NotNull CoreLogger getLogger(@NotNull String fqcn, @NotNull String name) {
    Logger logger = Logger.getLogger(name);
    return new JULCoreLogger(fqcn, logger);
  }
}
