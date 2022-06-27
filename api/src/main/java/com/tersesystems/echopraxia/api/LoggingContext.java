package com.tersesystems.echopraxia.api;

import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * The logging context interface is exposed to conditions as the way to inspect the available fields
 * for evaluation.
 */
public interface LoggingContext extends FindPathMethods {

  /** @return both context and argument fields, in that order. */
  @NotNull
  List<Field> getFields();

  /** @return the fields passed in as arguments to the logger. */
  List<Field> getArgumentFields();

  /** @return the list of fields that are part of logger's context. */
  List<Field> getLoggerFields();
}
