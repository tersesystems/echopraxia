package com.tersesystems.echopraxia.core;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * The core logger factory.
 *
 * <p>This is internal, and is intended for service provider implementations.
 */
public class CoreLoggerFactory {

  public static CoreLogger getLogger(Class<?> clazz) {
    return LazyHolder.INSTANCE.getLogger(clazz);
  }

  public static CoreLogger getLogger(String name) {
    return LazyHolder.INSTANCE.getLogger(name);
  }

  private static class LazyHolder {
    private static CoreLoggerProvider init() {
      ServiceLoader<CoreLoggerProvider> loader = ServiceLoader.load(CoreLoggerProvider.class);
      Iterator<CoreLoggerProvider> iterator = loader.iterator();
      if (iterator.hasNext()) {
        return iterator.next();
      } else {
        String msg = "No CoreLoggerProvider implementation found in classpath!";
        throw new ServiceConfigurationError(msg);
      }
    }

    static final CoreLoggerProvider INSTANCE = init();
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
