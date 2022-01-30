package com.tersesystems.echopraxia.core;

import org.jetbrains.annotations.NotNull;

/**
 * The CoreLoggerProvider is a service provider interface used by LoggerFactory.
 *
 * <p>You probably won't use this directly, unless you're writing a provider implementation.
 */
public interface CoreLoggerProvider {

  @NotNull
  CoreLogger getLogger(@NotNull Class<?> clazz);

  @NotNull
  CoreLogger getLogger(@NotNull String name);
}
