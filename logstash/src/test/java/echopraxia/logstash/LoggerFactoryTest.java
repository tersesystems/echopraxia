package echopraxia.logstash;

import static org.assertj.core.api.Assertions.assertThat;

import echopraxia.api.PresentationFieldBuilder;
import echopraxia.logger.Logger;
import echopraxia.logger.LoggerFactory;
import org.junit.jupiter.api.Test;

public class LoggerFactoryTest {

  @Test
  public void testLoggerFactory() {
    // Check that the SPI works
    final Logger<PresentationFieldBuilder> logger = LoggerFactory.getLogger(getClass());
    assertThat(logger).isNotNull();
  }
}
