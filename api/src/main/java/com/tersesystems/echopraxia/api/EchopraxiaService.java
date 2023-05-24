package com.tersesystems.echopraxia.api;

import java.util.*;
import org.jetbrains.annotations.NotNull;

public interface EchopraxiaService {
  @NotNull
  ExceptionHandler getExceptionHandler();

  @NotNull
  default Filters getFilters() {
    return FiltersLazyHolder.INSTANCE;
  }

  @NotNull
  default CoreLogger getCoreLogger(@NotNull String fqcn, @NotNull Class<?> clazz) {
    return CoreLoggerLazyHolder.INSTANCE.getLogger(fqcn, clazz);
  }

  @NotNull
  default CoreLogger getCoreLogger(@NotNull String fqcn, @NotNull String name) {
    return CoreLoggerLazyHolder.INSTANCE.getLogger(fqcn, name);
  }

  ToStringFormatter getToStringFormatter();

  static EchopraxiaService getInstance() {
    return EchopraxiaServiceLazyHolder.INSTANCE;
  }
}

class EchopraxiaServiceLazyHolder {
  private static EchopraxiaService init() {
    ServiceLoader<EchopraxiaServiceProvider> loader =
        ServiceLoader.load(EchopraxiaServiceProvider.class);
    Iterator<EchopraxiaServiceProvider> iterator = loader.iterator();
    if (iterator.hasNext()) {
      return iterator.next().getEchopraxiaService();
    } else {
      return new DefaultEchopraxiaService();
    }
  }

  static final EchopraxiaService INSTANCE = init();
}

class CoreLoggerLazyHolder {
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

class FiltersLazyHolder {

  private static final ClassLoader[] classLoaders = {ClassLoader.getSystemClassLoader()};

  static final Filters INSTANCE = init();

  private static Filters init() {
    try {
      return new Filters(classLoaders);
    } catch (Exception e) {
      // If we get to this point, something has gone horribly wrong.
      EchopraxiaService.getInstance().getExceptionHandler().handleException(e);
      // Keep going with no filters.
      return new Filters(Collections.emptyList());
    }
  }
}
