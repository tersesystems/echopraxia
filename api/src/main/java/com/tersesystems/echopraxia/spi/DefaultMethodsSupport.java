package com.tersesystems.echopraxia.spi;

import org.jetbrains.annotations.NotNull;

/**
 * Methods that are used by the defaults to do delegation to the core logger.
 *
 * @param <FB> the field builder type.
 */
public interface DefaultMethodsSupport<FB> {
  /**
   * @return the name associated with the logger
   */
  @NotNull
  String getName();

  /**
   * @return The core logger underlying this logger
   */
  @NotNull
  CoreLogger core();

  /**
   * @return the field builder being used by this logger.
   */
  @NotNull
  FB fieldBuilder();
}
