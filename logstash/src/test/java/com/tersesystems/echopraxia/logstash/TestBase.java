package com.tersesystems.echopraxia.logstash;

import static org.slf4j.Logger.ROOT_LOGGER_NAME;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.ILoggerFactory;

public class TestBase {
  protected ILoggerFactory factory;

  @BeforeEach
  public void before() {
    try {
      LoggerContext factory = new LoggerContext();
      JoranConfigurator joran = new JoranConfigurator();
      joran.setContext(factory);
      factory.reset();
      joran.doConfigure(getClass().getResource("/logback-test.xml").toURI().toURL());
      this.factory = factory;
    } catch (JoranException | URISyntaxException | MalformedURLException je) {
      je.printStackTrace();
    }
  }

  LoggerContext loggerContext() {
    return (LoggerContext) factory;
  }

  ListAppender<ILoggingEvent> getListAppender() {
    return (ListAppender<ILoggingEvent>)
        loggerContext().getLogger(ROOT_LOGGER_NAME).getAppender("LIST");
  }
}
