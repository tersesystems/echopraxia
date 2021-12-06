package com.tersesystems.echopraxia;

/**
 * An enumeration of the logging levels.
 *
 * <p>Note that ordinal values may not match with Logback/Log4J/SLF4J levels, and you should
 * exercise caution when looking at cardinal/numeric values because they're all different.
 */
public enum Level {
  ERROR,
  WARN,
  INFO,
  DEBUG,
  TRACE
}
