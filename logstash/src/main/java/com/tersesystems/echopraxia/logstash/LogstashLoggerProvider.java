package com.tersesystems.echopraxia.logstash;

import ch.qos.logback.classic.LoggerContext;
import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerProvider;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Logstash implementation of a logger provider.
 *
 * <p>This is the main SPI hook into the ServiceLoader.
 */
public class LogstashLoggerProvider implements CoreLoggerProvider {

  private LoggerContext loggerContext;

  @Override
  public void initialize() {
    this.loggerContext = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
    addEchopraxiaPackages(loggerContext.getFrameworkPackages());
  }

  public @NotNull CoreLogger getLogger(@NotNull Class<?> clazz) {
    return getLogger(clazz.getName());
  }

  public @NotNull CoreLogger getLogger(@NotNull String name) {
    org.slf4j.Logger logger = loggerContext.getLogger(name);
    return new LogstashCoreLogger(logger);
  }

  public void addEchopraxiaPackages(List<String> frameworkPackages) {
    // CallerData uses substring match: if (currentClass.startsWith(s))
    // https://github.com/qos-ch/logback/blob/master/logback-classic/src/main/java/ch/qos/logback/classic/spi/CallerData.java#L113
    addFrameworkPackage(frameworkPackages, "com.tersesystems.echopraxia");
  }

  public void addFrameworkPackage(List<String> frameworkPackages, String packageName) {
    if (!frameworkPackages.contains(packageName)) {
      frameworkPackages.add(packageName);
    }
  }
}
