package com.tersesystems.echopraxia;

import java.util.List;

/**
 * The LoggingContext interface is used in conditions to expose contextual information added to a
 * logger.
 *
 * <p>Specific implementations may have more to expose, but everything should have fields at least.
 */
public interface LoggingContext {

  /**
   * The list of fields that are contextual to the logger.
   *
   * @return list of fields that are part of logger's context.
   */
  List<Field> getFields();
}
