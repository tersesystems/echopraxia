package com.tersesystems.echopraxia.logstash;

import static org.slf4j.Logger.ROOT_LOGGER_NAME;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.echopraxia.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;

public class TestBase {
  protected LoggerContext loggerContext;

  @BeforeEach
  public void before() {
    try {
      LoggerContext factory = new LoggerContext();
      JoranConfigurator joran = new JoranConfigurator();
      joran.setContext(factory);
      factory.reset();
      joran.doConfigure(getClass().getResource("/logback-test.xml").toURI().toURL());
      this.loggerContext = factory;
    } catch (JoranException | URISyntaxException | MalformedURLException je) {
      je.printStackTrace();
    }
  }

  void waitUntilMessages() {
    final EncodingListAppender<ILoggingEvent> appender = getStringAppender();
    org.awaitility.Awaitility.await().until(() -> !appender.list.isEmpty());
  }

  LoggerContext loggerContext() {
    return loggerContext;
  }

  LogstashCoreLogger getCoreLogger() {
    return new LogstashCoreLogger(Logger.FQCN, loggerContext().getLogger(getClass().getName()));
  }

  Logger<?> getLogger() {
    return LoggerFactory.getLogger(getCoreLogger(), Field.Builder.instance());
  }

  AsyncLogger<?> getAsyncLogger() {
    return AsyncLoggerFactory.getLogger(getCoreLogger(), Field.Builder.instance());
  }

  ListAppender<ILoggingEvent> getListAppender() {
    return (ListAppender<ILoggingEvent>)
        loggerContext().getLogger(ROOT_LOGGER_NAME).getAppender("LIST");
  }

  EncodingListAppender<ILoggingEvent> getStringAppender() {
    return (EncodingListAppender<ILoggingEvent>)
        loggerContext().getLogger(ROOT_LOGGER_NAME).getAppender("STRINGLIST");
  }
}
