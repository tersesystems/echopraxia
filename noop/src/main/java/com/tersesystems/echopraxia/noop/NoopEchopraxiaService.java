package com.tersesystems.echopraxia.noop;

import com.tersesystems.echopraxia.spi.AbstractEchopraxiaService;
import com.tersesystems.echopraxia.spi.CoreLogger;
import org.jetbrains.annotations.NotNull;

public class NoopEchopraxiaService extends AbstractEchopraxiaService {

  public NoopEchopraxiaService() {
    super();
  }

  @Override
  public @NotNull CoreLogger getCoreLogger(@NotNull String fqcn, @NotNull Class<?> clazz) {
    return new NoopCoreLogger(fqcn);
  }

  @Override
  public @NotNull CoreLogger getCoreLogger(@NotNull String fqcn, @NotNull String name) {
    return new NoopCoreLogger(fqcn);
  }
}
