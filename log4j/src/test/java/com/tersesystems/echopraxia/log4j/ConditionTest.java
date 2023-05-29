package com.tersesystems.echopraxia.log4j;

import static com.tersesystems.echopraxia.log4j.appender.ListAppender.getListAppender;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.databind.JsonNode;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.api.Condition;
import com.tersesystems.echopraxia.api.Level;
import com.tersesystems.echopraxia.async.AsyncLogger;
import com.tersesystems.echopraxia.log4j.appender.ListAppender;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;
import org.apache.logging.log4j.ThreadContext;
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

    Logger<?> logger = getLogger();
    Logger<?> loggerWithCondition =
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
    Logger<?> logger = getLogger();
    final AtomicReference<String> changeableValue = new AtomicReference<>("derp");

    // we need to know that withFields is "call by name" and is evaluated fresh on every statement.
    Logger<?> loggerWithContext = logger.withFields(f -> f.string("herp", changeableValue.get()));

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
    Logger<?> logger = getLogger();

    ThreadContext.put("herp", "derp");
    // we need to know that withThreadContext is "call by name" and is evaluated fresh on every
    // statement.
    Logger<?> loggerWithContext = logger.withThreadContext();
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

    JsonNode entry = getEntry();
    String message = entry.path("message").asText();
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

    final ListAppender listAppender = getListAppender("ListAppender");
    final List<String> messages = listAppender.getMessages();
    assertThat(messages).isEmpty();

    Throwable throwable = StaticExceptionHandler.head();
    String exceptionMessage = throwable.getMessage();
    assertThat(exceptionMessage).isEqualTo("oh noes!");
  }

  @Test
  void testFailedAsyncLogging() {
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
                return fb.string("foo", "bar");
              });
        });

    await().atLeast(100, MILLISECONDS).until(logged::get);

    final ListAppender listAppender = getListAppender("ListAppender");
    final List<String> messages = listAppender.getMessages();
    assertThat(messages).isEmpty();

    Throwable throwable = StaticExceptionHandler.head();
    String exceptionMessage = throwable.getMessage();
    assertThat(exceptionMessage).isEqualTo("oh noes!");
  }
}
