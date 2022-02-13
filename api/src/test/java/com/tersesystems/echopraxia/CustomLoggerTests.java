package com.tersesystems.echopraxia;

import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerFactory;
import org.junit.jupiter.api.Test;

public class CustomLoggerTests {

  @Test
  public void testInfo() {
    final CoreLogger core =
        CoreLoggerFactory.getLogger(CustomLogger.class.getName(), "example.Logger");
    final CustomLogger<Field.Builder> logger = new CustomLogger<>(core, Field.Builder.instance());
    logger.info("Testing");
  }
}
