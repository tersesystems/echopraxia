package com.tersesystems.echopraxia.fluent;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.core.Caller;
import com.tersesystems.echopraxia.core.CoreLogger;

/** The factory for FluentLogger. */
public class FluentLoggerFactory {

  /**
   * Creates a logger using the given class name.
   *
   * @param clazz the logger class to use
   * @return the logger.
   */
  public static FluentLogger<Field.Builder> getLogger(Class<?> clazz) {
    return getLogger(clazz, Field.Builder.instance());
  }

  /**
   * Creates a logger using the given class name and explicit field builder.
   *
   * @param clazz the logger class to use
   * @param builder the field builder.
   * @return the logger.
   * @param <FB> the type of field builder.
   */
  public static <FB extends Field.Builder> FluentLogger<FB> getLogger(Class<?> clazz, FB builder) {
    CoreLogger coreLogger = LoggerFactory.getLogger(clazz).core();
    return getLogger(coreLogger, builder);
  }

  /**
   * Creates a logger using the given name.
   *
   * @param name the logger name to use
   * @return the logger.
   */
  public static FluentLogger<Field.Builder> getLogger(String name) {
    return getLogger(name, Field.Builder.instance());
  }

  /**
   * Creates a logger using the given name and an explicit field builder.
   *
   * @param name the logger name to use
   * @param builder the field builder.
   * @param <FB> the type of field builder.
   * @return the logger.
   */
  public static <FB extends Field.Builder> FluentLogger<FB> getLogger(String name, FB builder) {
    CoreLogger coreLogger = LoggerFactory.getLogger(name).core();
    return getLogger(coreLogger, builder);
  }

  /**
   * Creates a logger using the caller's class name.
   *
   * @return the logger.
   */
  public static FluentLogger<Field.Builder> getLogger() {
    return getLogger(Caller.resolveClassName());
  }

  /**
   * Creates a logger using the caller's class name and an explicit field builder.
   *
   * @param builder the field builder.
   * @return the logger.
   * @param <FB> the type of field builder.
   */
  public static <FB extends Field.Builder> FluentLogger<Field.Builder> getLogger(FB builder) {
    return getLogger(Caller.resolveClassName(), builder);
  }

  /**
   * Creates a logger using a core logger and an explicit field builder.
   *
   * @param coreLogger the core logger.
   * @param builder the field builder.
   * @param <FB> the type of field builder.
   * @return the logger.
   */
  public static <FB extends Field.Builder> FluentLogger<FB> getLogger(
      CoreLogger coreLogger, FB builder) {
    return new FluentLogger<>(coreLogger, builder);
  }
}
