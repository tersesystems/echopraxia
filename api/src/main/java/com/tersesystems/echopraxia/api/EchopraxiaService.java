package com.tersesystems.echopraxia.api;

import java.util.*;
import org.jetbrains.annotations.NotNull;

/** The main SPI interface. Call getInstance() to get the service from service provider. */
public interface EchopraxiaService {
  @NotNull
  ExceptionHandler getExceptionHandler();

  @NotNull
  Filters getFilters();

  @NotNull
  CoreLogger getCoreLogger(@NotNull String fqcn, @NotNull Class<?> clazz);

  @NotNull
  CoreLogger getCoreLogger(@NotNull String fqcn, @NotNull String name);

  @NotNull
  ToStringFormatter getToStringFormatter();

  @NotNull
  FieldCreator getFieldCreator();

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
