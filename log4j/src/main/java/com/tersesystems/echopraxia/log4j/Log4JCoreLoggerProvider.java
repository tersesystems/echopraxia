package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerProvider;
import org.apache.logging.log4j.LogManager;
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
    // From https://logging.apache.org/log4j/2.x/faq.html#logger-wrapper
    //
    // Log4j remembers the fully qualified class name (FQCN) of the logger and uses this to walk the
    // stack trace for every log event when configured to print location. (Be aware that logging
    // with location is slow and may impact the performance of your application.)
    //
    // The problem with custom logger wrappers is that they have a different FQCN than the actual
    // logger, so Log4j can’t find the place where your custom logger was called.
    //
    // The solution is to provide the correct FQCN. The easiest way to do this is to let Log4j
    // generate the logger wrapper for you. Log4j comes with a Logger wrapper generator tool. This
    // tool was originally meant to support custom log levels and is documented here.
    //
    // The generated logger code will take care of the FQCN.
    //
    // https://logging.apache.org/log4j/2.x/manual/customloglevels.html#CustomLoggers
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
