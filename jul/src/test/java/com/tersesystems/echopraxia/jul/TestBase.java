package com.tersesystems.echopraxia.jul;

import com.tersesystems.echopraxia.*;
import com.tersesystems.echopraxia.api.FieldBuilder;
import java.io.IOException;
import java.util.logging.LogManager;
import org.junit.jupiter.api.BeforeEach;

public class TestBase {

  @BeforeEach
  public void before() throws IOException {
    LogManager manager = LogManager.getLogManager();
    manager.reset();
    manager.readConfiguration(
        getClass().getClassLoader().getResourceAsStream("logging.properties"));
  }

  Logger<?> getLogger() {
    return LoggerFactory.getLogger(getCoreLogger(), FieldBuilder.instance());
  }

  JULCoreLogger getCoreLogger() {
    java.util.logging.Logger logger = java.util.logging.Logger.getLogger(getClass().getName());
    return new JULCoreLogger(Logger.FQCN, logger);
  }
}
