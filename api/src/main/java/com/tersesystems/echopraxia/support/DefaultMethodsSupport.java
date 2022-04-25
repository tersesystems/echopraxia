package com.tersesystems.echopraxia.support;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.core.CoreLogger;
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
