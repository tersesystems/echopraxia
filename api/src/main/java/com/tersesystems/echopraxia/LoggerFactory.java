package com.tersesystems.echopraxia;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * The LoggerFactory class returns a logger, using a LoggerProvider returned from service loader.
 *
 * <p>{@code Logger logger = LoggerFactory.getLogger(getClass()); }
 */
public class LoggerFactory {

  private static class LazyHolder {
    private static LoggerProvider init() {
      ServiceLoader<LoggerProvider> loader = ServiceLoader.load(LoggerProvider.class);
      Iterator<LoggerProvider> iterator = loader.iterator();
      if (iterator.hasNext()) {
        return iterator.next();
      } else {
        String msg = "No LoggerProvider implementation found in classpath!";
        throw new ServiceConfigurationError(msg);
      }
    }

    static final LoggerProvider INSTANCE = init();
  }

  /**
   * Creates a logger using the given class name.
   *
   * @param clazz the class to use
   * @return the logger.
   */
  public static Logger<Field.Builder> getLogger(Class<?> clazz) {
    return LazyHolder.INSTANCE.getLogger(clazz);
  }

  /**
   * Creates a logger using the given name.
   *
   * @param name the name to use
   * @return the logger.
   */
  public static Logger<Field.Builder> getLogger(String name) {
    return LazyHolder.INSTANCE.getLogger(name);
  }
}
