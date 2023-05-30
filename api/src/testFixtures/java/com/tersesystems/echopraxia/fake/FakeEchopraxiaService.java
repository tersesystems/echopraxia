package com.tersesystems.echopraxia.fake;

import com.tersesystems.echopraxia.api.AbstractEchopraxiaService;
import com.tersesystems.echopraxia.api.CoreLogger;
import org.jetbrains.annotations.NotNull;

public class FakeEchopraxiaService extends AbstractEchopraxiaService {
  @Override
  public @NotNull CoreLogger getCoreLogger(@NotNull String fqcn, @NotNull Class<?> clazz) {
    return new FakeCoreLogger(fqcn);
  }

  @Override
  public @NotNull CoreLogger getCoreLogger(@NotNull String fqcn, @NotNull String name) {
    return new FakeCoreLogger(fqcn);
  }
}
