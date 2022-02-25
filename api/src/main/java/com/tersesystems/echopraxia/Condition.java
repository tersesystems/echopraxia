package com.tersesystems.echopraxia;

import java.util.function.Predicate;

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
   * @return a condition that renders result of this condition AND given condition.
   */
  default Condition and(Condition c) {
    return (level, context) -> Condition.this.test(level, context) && c.test(level, context);
  }

  /**
   * Returns a condition which does a logical AND on this condition with the given condition.
   *
   * @param c the given condition.
   * @return a condition that renders result of this condition OR given condition.
   */
  default Condition or(Condition c) {
    return (level, context) -> Condition.this.test(level, context) || c.test(level, context);
  }

  /**
   * Returns a condition which does a logical XOR on this condition with the given condition.
   *
   * @param c the given condition.
   * @return a condition that renders result of this condition XOR given condition.
   */
  default Condition xor(Condition c) {
    return (level, context) -> Condition.this.test(level, context) ^ c.test(level, context);
  }

  /**
   * A condition that will always be met.
   *
   * @return a condition returning true.
   */
  static Condition always() {
    return Conditions.ALWAYS;
  }

  /**
   * A condition that will never be met.
   *
   * @return a condition returning false.
   */
  static Condition never() {
    return Conditions.NEVER;
  }

  /**
   * A condition that returns true if level.isGreaterOrEqual(threshold).
   *
   * @param threshold the minimum threshold to meet.
   * @return a condition that tests for the level to at least meet the threshold.
   */
  static Condition threshold(Level threshold) {
    return (level, context) -> level.isGreaterOrEqual(threshold);
  }

  /**
   * A condition that returns true if the level is equal to exactLevel.
   *
   * @param exactLevel the exact level to match.
   * @return a condition returning if level.isEqual(exactLevel):
   */
  static Condition exactly(Level exactLevel) {
    return (level, context) -> level.isEqual(exactLevel);
  }

  /** @return A condition that matches if the level is diagnostic: DEBUG or TRACE. */
  static Condition diagnostic() {
    return (level, context) -> level.isLessOrEqual(Level.DEBUG);
  }

  /** @return A condition that matches if the level is operational: INFO, WARN, or ERROR. */
  static Condition operational() {
    return (level, context) -> level.isGreaterOrEqual(Level.INFO);
  }

  /**
   * Searches through the fields for any match of the predicate.
   *
   * @param predicate a predicate that the field must satisfy.
   * @return true if the predicate is satisfied, false otherwise.
   */
  static Condition anyMatch(Predicate<Field> predicate) {
    return (level, ctx) -> ctx.getFields().stream().anyMatch(predicate);
  }

  /**
   * Searches through the fields for none match of the predicate.
   *
   * @param predicate a predicate
   * @return true if no elements match the predicate, false otherwise.
   */
  static Condition noneMatch(Predicate<Field> predicate) {
    return (level, ctx) -> ctx.getFields().stream().noneMatch(predicate);
  }

  /**
   * Searches through the fields for the given field name and value.
   *
   * @param fieldName The name of the field.
   * @param predicate a predicate
   * @return true if no elements match the predicate, false otherwise.
   */
  static Condition valueMatch(String fieldName, Predicate<Field.Value<?>> predicate) {
    return (level, ctx) ->
        ctx.getFields().stream()
            .filter(f -> f.name().equals(fieldName))
            .map(Field::value)
            .anyMatch(predicate);
  }
}

class Conditions {
  static final Condition NEVER = (level, context) -> false;

  static final Condition ALWAYS = (level, context) -> true;
}
