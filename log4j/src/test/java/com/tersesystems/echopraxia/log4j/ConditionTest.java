package com.tersesystems.echopraxia.log4j;

import static org.assertj.core.api.Assertions.assertThat;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Level;
import com.tersesystems.echopraxia.Logger;
import javax.json.JsonObject;
import org.junit.jupiter.api.Test;

public class ConditionTest extends TestBase {

  @Test
  void testCondition() {
    Logger<?> logger = getLogger();
    // XXX change this with something that checks the condition was evaluated
    Condition condition = (l, c) -> true;
    logger.info(condition, "info");

    JsonObject entry = getEntry();
    final String message = entry.getString("message");
    assertThat(message).isEqualTo("info");
  }

  @Test
  void testConditionWithContext() {
    Condition hasInfoLevel = (level, context) -> level.equals(Level.INFO);

    Logger<?> logger = getLogger();
    Logger<?> loggerWithCondition =
        logger.withCondition(hasInfoLevel).withFields(f -> f.onlyString("herp", "derp"));

    Condition hasFieldNamedHerp =
        (level, c) -> c.getFields().stream().anyMatch(f -> f.name().equals("herp"));
    loggerWithCondition.info(hasFieldNamedHerp, "info");

    JsonObject entry = getEntry();
    final JsonObject fields = entry.getJsonObject("fields");
    assertThat(fields.getString("herp")).isEqualTo("derp");
  }
}
