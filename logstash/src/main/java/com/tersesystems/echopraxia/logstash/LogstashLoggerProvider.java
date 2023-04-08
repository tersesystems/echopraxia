package com.tersesystems.echopraxia.logstash;

import ch.qos.logback.classic.LoggerContext;
import com.tersesystems.echopraxia.api.CoreLogger;
import com.tersesystems.echopraxia.api.CoreLoggerProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * Logstash implementation of a logger provider.
 *
 * <p>This is the main SPI hook into the ServiceLoader.
 */
public class LogstashLoggerProvider implements CoreLoggerProvider {

  // Customize the number of retries, using 10 as the default
  private static final int retryCount =
          Integer.parseInt(System.getProperty("echopraxia.logback.retries", "10"));

  private volatile LoggerContext loggerContext;

  private void initialize() {
    if (loggerContext == null) {
      // Delay initialization as long as possible, and attempt to account for
      // a logging component that may invoke code which logs (and therefore uses
      // a substitute logger.  Until Logback has finished initializing, we'll get
      // back org.slf4j.helpers.SubstituteLoggerFactory and that's no good.
      //
      // Sleeping here isn't ideal, but:
      //
      // a) it should only happen once
      // b) it's happening at start up, in application code
      // c) I don't know what else to do here.
      //
      // https://www.slf4j.org/codes.html#substituteLogger
      // https://www.slf4j.org/codes.html#replay
      if (loggerContext == null) {
        int retries = retryCount;
        ILoggerFactory factory = null;
        while (retries > 0) {
          try {
            factory = LoggerFactory.getILoggerFactory();
            if (factory instanceof LoggerContext) {
              loggerContext = (LoggerContext) factory;
              break;
            } else {
              System.err.println(
                      "LogstashLoggerProvider: Logback still initializing, sleeping for 100 ms...");
              retries -= 1;
              // https://logback.qos.ch/manual/configuration.html#auto_configuration
              // It takes about 100 milliseconds for Joran to parse a given logback configuration
              // file.
              Thread.sleep(100L);
            }
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }
        if (loggerContext == null) {
          System.err.println(
                  "LogstashLoggerProvider: No Logback implementation can be found after 10 retries.  Giving up.");
          throw new ServiceConfigurationError("Invalid ILoggerFactory implementation " + factory);
        }
      }
    }
  }

  public @NotNull CoreLogger getLogger(@NotNull String fqcn, @NotNull Class<?> clazz) {
    return getLogger(fqcn, clazz.getName());
  }

  public @NotNull CoreLogger getLogger(@NotNull String fqcn, @NotNull String name) {
    initialize();
    return new LogstashCoreLogger(fqcn, loggerContext.getLogger(name));
  }
}
