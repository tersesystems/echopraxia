package com.tersesystems.echopraxia.jsonpath.fake;

import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerProvider;
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
