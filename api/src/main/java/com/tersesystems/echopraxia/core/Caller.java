package com.tersesystems.echopraxia.core;

import org.jetbrains.annotations.NotNull;

public class Caller {

  @NotNull
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
    throw new IllegalStateException("No stack trace elements found in thread!");
  }
}
