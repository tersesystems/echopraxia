package echopraxia.logback;

import static org.slf4j.Logger.ROOT_LOGGER_NAME;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
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

  LoggerContext loggerContext() {
    return loggerContext;
  }

  ListAppender<ILoggingEvent> getListAppender() {
    final ch.qos.logback.classic.Logger logger = loggerContext().getLogger(ROOT_LOGGER_NAME);
    return (ListAppender<ILoggingEvent>) logger.iteratorForAppenders().next();
  }
}
