package com.tersesystems.echopraxia;

/**
 * A condition is used to conditionally log statements based on the level and context of the logger.
 */
public interface Condition {

  /**
   * Tests the condition.
   *
   * @param level the logging level
   * @param context the logging context
   * @return true if the conditions are met, false otherwise.
   */
  boolean test(Level level, LoggingContext context);

  /**
   * Returns a condition which does a logical AND on this condition with the given condition.
   *
   * @param c the given condition.
   * @return this condition AND given position.
   */
  default Condition and(Condition c) {
    return (level, context) -> Condition.this.test(level, context) && c.test(level, context);
  }

  /**
   * A condition that will always be met.
   *
   * @return a condition returning true.
   */
  static Condition always() {
    return (level, context) -> true;
  }

  /**
   * A condition that will never be met.
   *
   * @return a condition returning false.
   */
  static Condition never() {
    return (level, context) -> false;
  }
}
