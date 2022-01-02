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
   * @param clazz the logger class to use
   * @return the logger.
   */
  public static Logger<Field.Builder> getLogger(Class<?> clazz) {
    return LazyHolder.INSTANCE.getLogger(clazz);
  }

  /**
   * Creates a logger using the given name.
   *
   * @param name the logger name to use
   * @return the logger.
   */
  public static Logger<Field.Builder> getLogger(String name) {
    return LazyHolder.INSTANCE.getLogger(name);
  }

  /**
   * Creates a logger using the caller's class name.
   *
   * @return the logger.
   */
  public static Logger<Field.Builder> getLogger() {
    return LazyHolder.INSTANCE.getLogger(Caller.resolveClassName());
  }

  public static class Caller {

    public static String resolveClassName() {
      // If we're on JDK 9, we can use
      // StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
      // Class<?> callerClass = walker.getCallerClass();
      // However, this works fine: https://stackoverflow.com/a/11306854
      StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
      String callerClassName = null;
      for (int i = 1; i < stElements.length; i++) {
        StackTraceElement ste = stElements[i];
        if (!ste.getClassName().equals(Caller.class.getName())
            && ste.getClassName().indexOf("java.lang.Thread") != 0) {
          if (callerClassName == null) {
            callerClassName = ste.getClassName();
          } else if (!callerClassName.equals(ste.getClassName())) {
            return ste.getClassName();
          }
        }
      }
      return null;
    }
  }
}
