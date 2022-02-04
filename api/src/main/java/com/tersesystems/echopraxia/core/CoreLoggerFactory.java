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
  public static CoreLogger getLogger(String fqcn, @NotNull Class<?> clazz) {
    return LazyHolder.INSTANCE.getLogger(fqcn, clazz);
  }

  @NotNull
  public static CoreLogger getLogger(String fqcn, @NotNull String name) {
    return LazyHolder.INSTANCE.getLogger(fqcn, name);
  }

  private static class LazyHolder {
    private static CoreLoggerProvider init() {
      ServiceLoader<CoreLoggerProvider> loader = ServiceLoader.load(CoreLoggerProvider.class);
      Iterator<CoreLoggerProvider> iterator = loader.iterator();
      if (iterator.hasNext()) {
        final CoreLoggerProvider provider = iterator.next();
        return provider;
      } else {
        String msg = "No CoreLoggerProvider implementation found in classpath!";
        throw new ServiceConfigurationError(msg);
      }
    }

    static final CoreLoggerProvider INSTANCE = init();
  }
}
