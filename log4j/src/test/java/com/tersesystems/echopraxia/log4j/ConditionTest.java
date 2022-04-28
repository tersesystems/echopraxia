package com.tersesystems.echopraxia.log4j;

import static com.tersesystems.echopraxia.log4j.appender.ListAppender.getListAppender;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.api.Condition;
import com.tersesystems.echopraxia.api.Level;
import com.tersesystems.echopraxia.async.AsyncLogger;
import com.tersesystems.echopraxia.log4j.appender.ListAppender;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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

  @Test
  void testAsyncCondition() {
    Condition c =
        (level, ctx) -> {
          try {
            Thread.sleep(900L);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          return true;
        };

    AtomicBoolean logged = new AtomicBoolean(false);
    AsyncLogger<?> loggerWithCondition = getAsyncLogger().withCondition(c);
    loggerWithCondition.info(
        handle -> {
          handle.log("async logging test");
          logged.set(true);
        });

    final ListAppender listAppender = getListAppender("ListAppender");
    final List<String> messages = listAppender.getMessages();
    assertThat(messages).isEmpty();

    await().atMost(1, SECONDS).until(logged::get);

    JsonObject entry = getEntry();
    String message = entry.getString("message");
    assertThat(message).isEqualTo("async logging test");
  }

  @Test
  void testFailedAsyncCondition() {
    AtomicBoolean logged = new AtomicBoolean(false);
    Condition c =
        (level, ctx) -> {
          if (System.currentTimeMillis() > 0) {
            logged.set(true);
            throw new RuntimeException("oh noes!");
          }
          return true;
        };
    AsyncLogger<?> loggerWithCondition = getAsyncLogger().withCondition(c);
    loggerWithCondition.info(
        handle -> {
          handle.log("async logging test");
          logged.set(true);
        });

    await().atLeast(100, MILLISECONDS).until(logged::get);
    JsonObject entry = getEntry();
    String message = entry.getString("message");
    assertThat(message).isEqualTo("Uncaught exception when running asyncLog");
    String exceptionMessage = entry.getJsonObject("thrown").getString("message");
    assertThat(exceptionMessage).isEqualTo("oh noes!");
  }

  @Test
  void testFailedLogging() {
    AtomicBoolean logged = new AtomicBoolean(false);
    AsyncLogger<?> loggerWithCondition = getAsyncLogger();
    loggerWithCondition.info(
        handle -> {
          handle.log(
              "async logging test",
              fb -> {
                if (System.currentTimeMillis() > 0) {
                  logged.set(true);
                  throw new RuntimeException("oh noes!");
                }
                return fb.onlyString("foo", "bar");
              });
        });

    await().atLeast(100, MILLISECONDS).until(logged::get);
    JsonObject entry = getEntry();
    String message = entry.getString("message");
    assertThat(message).isEqualTo("Uncaught exception when running asyncLog");
    String exceptionMessage = entry.getJsonObject("thrown").getString("message");
    assertThat(exceptionMessage).isEqualTo("oh noes!");
  }
}
