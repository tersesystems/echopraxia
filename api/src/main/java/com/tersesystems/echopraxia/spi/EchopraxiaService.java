package com.tersesystems.echopraxia.spi;

import com.tersesystems.echopraxia.api.*;
import java.util.*;
import org.jetbrains.annotations.NotNull;

/**
 * The main SPI interface. Call getInstance() to get the service from service provider.
 *
 * @since 3.0
 */
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

  /**
   * Gets the field creator.
   *
   * @param <F> the field type
   * @param fieldClass the field class.
   * @return the field creator matching the field class.
   */
  @NotNull
  <F extends Field> FieldCreator<F> getFieldCreator(@NotNull Class<F> fieldClass);

  static FieldCreator<Field> getFieldCreator() {
    return getInstance().getFieldCreator(Field.class);
  }

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
