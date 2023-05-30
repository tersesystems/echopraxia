package com.tersesystems.echopraxia.api;

import java.util.Collections;
import org.jetbrains.annotations.NotNull;

/** Abstract service with default implementations of exception handler and toString formatter. */
public abstract class AbstractEchopraxiaService implements EchopraxiaService {

  private static final ClassLoader[] classLoaders = {ClassLoader.getSystemClassLoader()};
  protected FieldCreator fieldCreator;

  protected Filters filters;
  protected ToStringFormatter toStringFormatter;
  protected ExceptionHandler exceptionHandler;

  public AbstractEchopraxiaService() {
    this.exceptionHandler = Throwable::printStackTrace;
    this.toStringFormatter = new DefaultToStringFormatter();
    this.fieldCreator = new DefaultFieldCreator();
    this.filters = initFilters();
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

  public @NotNull FieldCreator getFieldCreator() {
    return fieldCreator;
  }
}
