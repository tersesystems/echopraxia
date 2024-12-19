package echopraxia.jul;

import static org.assertj.core.api.Assertions.assertThat;

import echopraxia.Logger;
import echopraxia.LoggerFactory;
import echopraxia.api.PresentationFieldBuilder;
import org.junit.jupiter.api.Test;

public class LoggerFactoryTest {

  @Test
  public void testLoggerFactory() {
    // Check that the SPI works
    final Logger<PresentationFieldBuilder> logger = LoggerFactory.getLogger(getClass());
    assertThat(logger).isNotNull();
  }
}
