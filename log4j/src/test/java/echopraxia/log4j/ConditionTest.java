package echopraxia.log4j;

import static echopraxia.log4j.appender.ListAppender.getListAppender;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import echopraxia.log4j.appender.ListAppender;
import echopraxia.logging.api.Condition;
import echopraxia.logging.api.Level;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.Test;

public class ConditionTest extends TestBase {

  @Test
  void testCondition() {
    var logger = getLogger();
    LongAdder adder = new LongAdder();
    Condition condition =
        (l, c) -> {
          adder.increment();
          return true;
        };
    logger.info(condition, "info");

    assertThat(adder.intValue()).isEqualTo(1);

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
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

    var logger = getLogger();
    var loggerWithCondition =
        logger.withCondition(hasInfoLevel).withFields(f -> f.string("herp", "derp"));

    LongAdder fieldConditionAdder = new LongAdder();
    Condition hasFieldNamedHerp =
        (level, c) -> {
          fieldConditionAdder.increment();
          return c.getFields().stream().anyMatch(f -> f.name().equals("herp"));
        };
    loggerWithCondition.info(hasFieldNamedHerp, "info");

    assertThat(fieldConditionAdder.intValue()).isEqualTo(1);
    assertThat(contextConditionAdder.intValue()).isEqualTo(1);
    JsonNode entry = getEntry();
    final JsonNode fields = entry.path("fields");
    assertThat(fields.path("herp").asText()).isEqualTo("derp");
  }

  @Test
  void testFieldConditionIsLive() {
    Condition hasDerp = Condition.valueMatch("herp", f -> f.raw().equals("derp"));
    var logger = getLogger();
    final AtomicReference<String> changeableValue = new AtomicReference<>("derp");

    // we need to know that withFields is "call by name" and is evaluated fresh on every statement.
    var loggerWithContext = logger.withFields(f -> f.string("herp", changeableValue.get()));

    loggerWithContext.info(hasDerp, "has derp");
    changeableValue.set("notderp");
    loggerWithContext.info(hasDerp, "should not log");

    final ListAppender listAppender = getListAppender("ListAppender");
    final List<String> messages = listAppender.getMessages();
    assertThat(messages.size()).isEqualTo(1);

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("has derp");
  }

  @Test
  void testThreadContextConditionIsLive() {
    Condition hasDerp = Condition.valueMatch("herp", f -> f.raw().equals("derp"));
    var logger = getLogger();

    ThreadContext.put("herp", "derp");
    // we need to know that withThreadContext is "call by name" and is evaluated fresh on every
    // statement.
    var loggerWithContext = logger.withThreadContext();
    loggerWithContext.info(hasDerp, "has derp");

    ThreadContext.put("herp", "notderp");
    loggerWithContext.info(hasDerp, "should not log");

    final ListAppender listAppender = getListAppender("ListAppender");
    final List<String> messages = listAppender.getMessages();
    assertThat(messages.size()).isEqualTo(1);

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("has derp");
  }
}
