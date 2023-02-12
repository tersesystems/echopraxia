package com.tersesystems.echopraxia.logstash;

import ch.qos.logback.classic.LoggerContext;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.impl.StaticLoggerBinder;

public class ConverterTest {
  protected LoggerContext loggerContext;

  @BeforeEach
  public void before() {
    try {
      System.setProperty(
          "logback.configurationFile",
          getClass().getResource("/logback-converter.xml").toURI().toURL().toString());
    } catch (MalformedURLException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
    loggerContext = (LoggerContext) StaticLoggerBinder.getSingleton().getLoggerFactory();
  }

  @AfterEach
  public void after() {
    loggerContext.stop();
  }

  @Test
  public void testLog() {
    Logger<?> logger = LoggerFactory.getLogger(getClass());
    logger.info("Only arguments", fb -> fb.string("book", "The Cask"));
    logger.withFields(fb -> fb.string("book", "The Cask")).info("Only logger context");
    logger
        .withFields(fb -> fb.string("book", "From Context"))
        .info("Both context and arguments", fb -> fb.string("book", "From Argument"));
  }
}
