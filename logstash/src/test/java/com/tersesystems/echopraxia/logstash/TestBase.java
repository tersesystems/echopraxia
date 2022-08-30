package com.tersesystems.echopraxia.logstash;

import static org.slf4j.Logger.ROOT_LOGGER_NAME;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.echopraxia.*;
import com.tersesystems.echopraxia.api.FieldBuilder;
import com.tersesystems.echopraxia.async.AsyncLogger;
import com.tersesystems.echopraxia.async.AsyncLoggerFactory;
import com.tersesystems.echopraxia.logback.TransformingAppender;
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
    return LoggerFactory.getLogger(getCoreLogger(), FieldBuilder.instance());
  }

  AsyncLogger<?> getAsyncLogger() {
    return AsyncLoggerFactory.getLogger(getCoreLogger(), FieldBuilder.instance());
  }

  ListAppender<ILoggingEvent> getListAppender() {
    final ch.qos.logback.classic.Logger logger = loggerContext().getLogger(ROOT_LOGGER_NAME);
    final TransformingAppender<ILoggingEvent> next =
        (TransformingAppender<ILoggingEvent>) logger.iteratorForAppenders().next();
    return (ListAppender<ILoggingEvent>) next.getAppender("LIST");
  }

  EncodingListAppender<ILoggingEvent> getStringAppender() {
    final ch.qos.logback.classic.Logger logger = loggerContext().getLogger(ROOT_LOGGER_NAME);
    final TransformingAppender<ILoggingEvent> next =
        (TransformingAppender<ILoggingEvent>) logger.iteratorForAppenders().next();
    return (EncodingListAppender<ILoggingEvent>) next.getAppender("STRINGLIST");
  }
}
