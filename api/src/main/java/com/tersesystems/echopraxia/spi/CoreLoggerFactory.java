package com.tersesystems.echopraxia.spi;

import org.jetbrains.annotations.NotNull;

/**
 * The core logger factory.
 *
 * <p>This is internal, and is intended for service provider implementations.
 */
public class CoreLoggerFactory {

  @NotNull
  public static CoreLogger getLogger(@NotNull String fqcn, @NotNull Class<?> clazz) {
    CoreLogger core = EchopraxiaService.getInstance().getCoreLogger(fqcn, clazz);
    return processFilters(core);
  }

  @NotNull
  public static CoreLogger getLogger(@NotNull String fqcn, @NotNull String name) {
    CoreLogger core = EchopraxiaService.getInstance().getCoreLogger(fqcn, name);
    return processFilters(core);
  }

  @NotNull
  private static CoreLogger processFilters(@NotNull CoreLogger core) {
    return EchopraxiaService.getInstance().getFilters().apply(core);
  }
}
