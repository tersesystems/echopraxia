package com.tersesystems.echopraxia.spi;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import org.jetbrains.annotations.NotNull;

/**
 * The main SPI interface. Call getInstance() to get the service from service provider.
 *
 * @since 3.0
 */
public interface EchopraxiaService {

  /**
   * @return the exception handler used by the service.
   */
  @NotNull
  ExceptionHandler getExceptionHandler();

  /**
   * @return the exception handler used by the service.
   */
  @NotNull
  Filters getFilters();

  /**
   * @param fqcn the fully qualified class name of the caller.
   * @param clazz the logger class.
   * @return the core logger associated with the service.
   */
  @NotNull
  CoreLogger getCoreLogger(@NotNull String fqcn, @NotNull Class<?> clazz);

  /**
   * @param fqcn the fully qualified class name of the caller.
   * @param name the logger name.
   * @return the core logger associated with the service.
   */
  @NotNull
  CoreLogger getCoreLogger(@NotNull String fqcn, @NotNull String name);

  /**
   * @return an instance of the service.
   */
  @NotNull
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
      throw new ServiceConfigurationError("No EchopraxiaService implementation found!");
    }
  }

  static final EchopraxiaService INSTANCE = init();
}
