package com.tersesystems.echopraxia.fake;

import com.tersesystems.echopraxia.api.CoreLogger;
import com.tersesystems.echopraxia.api.CoreLoggerProvider;
import org.jetbrains.annotations.NotNull;

public class FakeCoreLoggerProvider implements CoreLoggerProvider {
  @Override
  public @NotNull CoreLogger getLogger(@NotNull String fqcn, @NotNull Class<?> clazz) {
    return new FakeCoreLogger(fqcn);
  }

  @Override
  public @NotNull CoreLogger getLogger(@NotNull String fqcn, @NotNull String name) {
    return new FakeCoreLogger(fqcn);
  }
}
