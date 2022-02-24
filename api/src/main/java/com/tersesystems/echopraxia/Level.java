package com.tersesystems.echopraxia;

/**
 * An enumeration of the logging levels.
 *
 * <p>Note that ordinal values may not match with Logback/Log4J/SLF4J levels, and you should
 * exercise caution when looking at cardinal/numeric values because they're all different.
 */
public enum Level {

  // Order is significant, and compareTo uses ordinal values internally.
  TRACE, // 0
  DEBUG, // 1
  INFO,  // 2
  WARN,  // 3
  ERROR; // 4

  public boolean isGreater(Level r) {
    return compareTo(r) > 0;
  }

  public boolean isGreaterOrEqual(Level r) {
    return compareTo(r) >= 0;
  }

  public boolean isLess(Level r) {
    return compareTo(r) < 0;
  }

  public boolean isLessOrEqual(Level r) {
    return compareTo(r) <= 0;
  }

  public boolean isEqual(Level r) {
    return equals(r);
  }
}
