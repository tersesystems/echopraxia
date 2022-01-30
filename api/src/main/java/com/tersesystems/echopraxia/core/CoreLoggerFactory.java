package com.tersesystems.echopraxia.core;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import org.jetbrains.annotations.NotNull;

/**
 * The core logger factory.
 *
 * <p>This is internal, and is intended for service provider implementations.
 */
public class CoreLoggerFactory {

  @NotNull
  public static CoreLogger getLogger(@NotNull Class<?> clazz) {
    return LazyHolder.INSTANCE.getLogger(clazz);
  }

  @NotNull
  public static CoreLogger getLogger(@NotNull String name) {
    return LazyHolder.INSTANCE.getLogger(name);
  }

  @NotNull
  public static CoreLogger getLogger() {
    return getLogger(Caller.resolveClassName());
  }

  private static class LazyHolder {
    private static CoreLoggerProvider init() {
      ServiceLoader<CoreLoggerProvider> loader = ServiceLoader.load(CoreLoggerProvider.class);
      Iterator<CoreLoggerProvider> iterator = loader.iterator();
      if (iterator.hasNext()) {
        return iterator.next();
      } else {
        String msg = "No CoreLoggerProvider implementation found in classpath!";
        throw new ServiceConfigurationError(msg);
      }
    }

    static final CoreLoggerProvider INSTANCE = init();
  }
}
