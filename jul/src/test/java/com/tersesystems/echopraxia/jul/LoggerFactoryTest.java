package com.tersesystems.echopraxia.jul;

import static org.assertj.core.api.Assertions.assertThat;

import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.api.PresentationFieldBuilder;
import org.junit.jupiter.api.Test;

public class LoggerFactoryTest {

  @Test
  public void testLoggerFactory() {
    // Check that the SPI works
    final Logger<PresentationFieldBuilder> logger = LoggerFactory.getLogger(getClass());
    assertThat(logger).isNotNull();
  }
}
