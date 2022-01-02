package com.tersesystems.echopraxia;

import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerFactory;

/**
 * The LoggerFactory class.
 *
 * <p>{@code Logger logger = LoggerFactory.getLogger(getClass()); }
 */
public class LoggerFactory {

  /**
   * Creates a logger using the given class name.
   *
   * @param clazz the logger class to use
   * @return the logger.
   */
  public static Logger<Field.Builder> getLogger(Class<?> clazz) {
    final CoreLogger core = CoreLoggerFactory.getLogger(clazz);
    return getLogger(core, Logger.defaultFieldBuilder());
  }

  /**
   * Creates a logger using the given name.
   *
   * @param name the logger name to use
   * @return the logger.
   */
  public static Logger<Field.Builder> getLogger(String name) {
    final CoreLogger core = CoreLoggerFactory.getLogger(name);
    return getLogger(core, Logger.defaultFieldBuilder());
  }

  /**
   * Creates a logger using the caller's class name.
   *
   * @return the logger.
   */
  public static Logger<Field.Builder> getLogger() {
    CoreLogger core = CoreLoggerFactory.getLogger(CoreLoggerFactory.Caller.resolveClassName());
    return getLogger(core, Logger.defaultFieldBuilder());
  }

  /**
   * Creates a logger from a core logger and a field builder.
   *
   * @param core logger
   * @return the logger.
   */
  public static <FB extends Field.Builder> Logger<FB> getLogger(CoreLogger core, FB fieldBuilder) {
    return new Logger<>(core, fieldBuilder);
  }
}
