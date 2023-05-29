package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.api.AbstractEchopraxiaService;
import com.tersesystems.echopraxia.api.CoreLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.jetbrains.annotations.NotNull;

public class Log4JEchopraxiaService extends AbstractEchopraxiaService {

  @Override
  public @NotNull CoreLogger getCoreLogger(@NotNull String fqcn, @NotNull Class<?> clazz) {
    return getCoreLogger(fqcn, clazz.getName());
  }

  @Override
  public @NotNull CoreLogger getCoreLogger(@NotNull String fqcn, @NotNull String name) {
    return new Log4JCoreLogger(fqcn, (ExtendedLogger) LogManager.getLogger(name));
  }
}
