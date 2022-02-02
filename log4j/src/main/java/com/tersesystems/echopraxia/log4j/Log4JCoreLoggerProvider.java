package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.jetbrains.annotations.NotNull;

public class Log4JCoreLoggerProvider implements CoreLoggerProvider {

  private LoggerContext context;

  @Override
  public void initialize() {
    context = LogManager.getContext();

    // https://issues.apache.org/jira/browse/LOG4J2-2792
    // https://logging.apache.org/log4j/2.x/manual/async.html#Location
    // https://logging.apache.org/log4j/2.x/manual/layouts.html#LocationInformation
    //
    // Goes through StackLocator and StackLocatorUtil to calculate the location.
    // LocationAwareLogEventFactory seems to be the entry point to an event.
    // after that it comes from getSource() which has a StackTraceElement.
    //
    // Does not seem to be anything like getFrameworkPackages to filter the source,
    // I think you would have to do this through a custom JSON exception resolver.
    // and again for any other layouts.
  }

  @Override
  public @NotNull CoreLogger getLogger(@NotNull Class<?> clazz) {
    return getLogger(clazz.getName());
  }

  @Override
  public @NotNull CoreLogger getLogger(@NotNull String name) {
    return new Log4JCoreLogger(context.getLogger(name));
  }
}
