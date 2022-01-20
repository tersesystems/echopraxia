package com.tersesystems.echopraxia.log4j;

import static org.assertj.core.api.Assertions.assertThat;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Level;
import com.tersesystems.echopraxia.Logger;
import java.util.concurrent.atomic.LongAdder;
import javax.json.JsonObject;
import org.junit.jupiter.api.Test;

public class ConditionTest extends TestBase {

  @Test
  void testCondition() {
    Logger<?> logger = getLogger();
    LongAdder adder = new LongAdder();
    Condition condition =
        (l, c) -> {
          adder.increment();
          return true;
        };
    logger.info(condition, "info");

    assertThat(adder.intValue()).isEqualTo(1);

    JsonObject entry = getEntry();
    final String message = entry.getString("message");
    assertThat(message).isEqualTo("info");
  }

  @Test
  void testConditionWithContext() {

    LongAdder contextConditionAdder = new LongAdder();
    Condition hasInfoLevel =
        (level, context) -> {
          contextConditionAdder.increment();
          return level.equals(Level.INFO);
        };

    Logger<?> logger = getLogger();
    Logger<?> loggerWithCondition =
        logger.withCondition(hasInfoLevel).withFields(f -> f.onlyString("herp", "derp"));

    LongAdder fieldConditionAdder = new LongAdder();
    Condition hasFieldNamedHerp =
        (level, c) -> {
          fieldConditionAdder.increment();
          return c.getFields().stream().anyMatch(f -> f.name().equals("herp"));
        };
    loggerWithCondition.info(hasFieldNamedHerp, "info");

    assertThat(fieldConditionAdder.intValue()).isEqualTo(1);
    assertThat(contextConditionAdder.intValue()).isEqualTo(1);
    JsonObject entry = getEntry();
    final JsonObject fields = entry.getJsonObject("fields");
    assertThat(fields.getString("herp")).isEqualTo("derp");
  }
}
