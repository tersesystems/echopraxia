package echopraxia.jul;

import echopraxia.api.FieldBuilder;
import echopraxia.logger.Logger;
import echopraxia.logger.LoggerFactory;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import org.junit.jupiter.api.BeforeEach;

public class TestBase {

  @BeforeEach
  public void before() throws IOException {
    StaticExceptionHandler.clear();
    LogManager manager = LogManager.getLogManager();
    manager.reset();

    // Programmatic configuration
    System.setProperty(
        "java.util.logging.SimpleFormatter.format",
        "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$-7s [%3$s] (%2$s) %5$s %6$s%n");

    final Handler consoleHandler = new EncodedListHandler();
    consoleHandler.setLevel(Level.FINEST);

    java.util.logging.Logger logger = java.util.logging.Logger.getLogger("");
    logger.setLevel(Level.FINEST);
    logger.addHandler(consoleHandler);

    EncodedListHandler.clear();
  }

  Logger<FieldBuilder> getLogger() {
    return LoggerFactory.getLogger(getCoreLogger(), FieldBuilder.instance());
  }

  JULCoreLogger getCoreLogger() {
    java.util.logging.Logger logger = java.util.logging.Logger.getLogger(getClass().getName());
    return new JULCoreLogger(Logger.FQCN, logger);
  }
}
