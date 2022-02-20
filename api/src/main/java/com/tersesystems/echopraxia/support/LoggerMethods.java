package com.tersesystems.echopraxia.support;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import org.jetbrains.annotations.NotNull;

/**
 * Logging methods specific to the synchronous Logger (isEnabled checks + base).
 *
 * @param <FB>
 */
public interface LoggerMethods<FB extends Field.Builder> extends BaseLoggerMethods<FB> {

  /** @return true if the logger level is TRACE or higher. */
  boolean isTraceEnabled();

  /**
   * @param condition the given condition.
   * @return true if the logger level is TRACE or higher and the condition is met.
   */
  boolean isTraceEnabled(@NotNull Condition condition);

  /** @return true if the logger level is DEBUG or higher. */
  boolean isDebugEnabled();

  /**
   * @param condition the given condition.
   * @return true if the logger level is DEBUG or higher and the condition is met.
   */
  boolean isDebugEnabled(@NotNull Condition condition);

  /** @return true if the logger level is INFO or higher. */
  boolean isInfoEnabled();

  /**
   * @param condition the given condition.
   * @return true if the logger level is INFO or higher and the condition is met.
   */
  boolean isInfoEnabled(@NotNull Condition condition);

  /** @return true if the logger level is WARN or higher. */
  boolean isWarnEnabled();

  /**
   * @param condition the given condition.
   * @return true if the logger level is WARN or higher and the condition is met.
   */
  boolean isWarnEnabled(@NotNull Condition condition);

  /** @return true if the logger level is ERROR or higher. */
  boolean isErrorEnabled();

  /**
   * @param condition the given condition.
   * @return true if the logger level is ERROR or higher and the condition is met.
   */
  boolean isErrorEnabled(@NotNull Condition condition);
}
