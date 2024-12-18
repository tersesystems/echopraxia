package com.tersesystems.echopraxia.spi;

import com.tersesystems.echopraxia.model.DefaultToStringFormatter;
import com.tersesystems.echopraxia.model.ToStringFormatter;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract service with default implementations of exception handler and toString formatter.
 *
 * @since 3.0
 */
public abstract class AbstractEchopraxiaService implements EchopraxiaService {

  private static final ClassLoader[] classLoaders = {ClassLoader.getSystemClassLoader()};

  /** The filters used by the service. */
  protected Filters filters;

  /** The formatter used by the service. */
  protected ToStringFormatter toStringFormatter;

  /** The exception handler used by the service. */
  protected ExceptionHandler exceptionHandler;

  /** Creates a service with defaults. */
  public AbstractEchopraxiaService() {
    this.exceptionHandler = Throwable::printStackTrace;
    this.toStringFormatter = new DefaultToStringFormatter();
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
}
