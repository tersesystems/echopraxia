package com.tersesystems.echopraxia.spi;

import org.jetbrains.annotations.NotNull;

/**
 * Methods that are used by the defaults to do delegation to the core logger.
 *
 * @param <FB> the field builder type.
 */
public interface DefaultMethodsSupport<FB> {
  @NotNull
  String getName();

  @NotNull
  CoreLogger core();

  @NotNull
  FB fieldBuilder();
}
