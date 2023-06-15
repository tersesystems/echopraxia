package com.tersesystems.echopraxia.spi;

import com.tersesystems.echopraxia.api.*;
import java.util.Collections;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract service with default implementations of exception handler and toString formatter.
 *
 * @since 3.0
 */
public abstract class AbstractEchopraxiaService implements EchopraxiaService {

  private static final ClassLoader[] classLoaders = {ClassLoader.getSystemClassLoader()};

  protected Filters filters;
  protected ToStringFormatter toStringFormatter;
  protected ExceptionHandler exceptionHandler;

  private final ConcurrentHashMap<Class<?>, FieldCreator<?>> fieldCreatorMap;

  public AbstractEchopraxiaService() {
    this.exceptionHandler = Throwable::printStackTrace;
    this.toStringFormatter = new DefaultToStringFormatter();
    this.filters = initFilters();
    this.fieldCreatorMap = new ConcurrentHashMap<>();
  }

  private Filters initFilters() {
    try {
      return new Filters(classLoaders);
    } catch (Exception e) {
      // If we get to this point, something has gone horribly wrong.
      exceptionHandler.handleException(e);
      // Keep going with no filters.
      return new Filters(Collections.emptyList());
    }
  }

  @NotNull
  @Override
  public Filters getFilters() {
    return filters;
  }

  @Override
  public @NotNull ExceptionHandler getExceptionHandler() {
    return exceptionHandler;
  }

  @Override
  public @NotNull ToStringFormatter getToStringFormatter() {
    return toStringFormatter;
  }

  public <F extends Field> @NotNull FieldCreator<F> getFieldCreator(Class<F> fieldClass) {
    //noinspection unchecked
    return (FieldCreator<F>)
        fieldCreatorMap.computeIfAbsent(fieldClass, AbstractEchopraxiaService::loadFieldCreator);
  }

  private static <T extends Field> FieldCreator<T> loadFieldCreator(Class<?> t) {
    for (FieldCreator<?> s : ServiceLoader.load(FieldCreator.class))
      if (s.canServe(t))
        //noinspection unchecked
        return (FieldCreator<T>) s;
    throw new UnsupportedOperationException("No field creator found for class " + t);
  }
}
