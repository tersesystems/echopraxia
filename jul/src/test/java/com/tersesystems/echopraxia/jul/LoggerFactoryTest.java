package com.tersesystems.echopraxia.jul;

import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.api.FieldBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoggerFactoryTest {

  @Test
  public void testLoggerFactory() {
    // Check that the SPI works
    final Logger<FieldBuilder> logger = LoggerFactory.getLogger(getClass());
    assertThat(logger).isNotNull();
  }
}
