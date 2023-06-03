package com.tersesystems.echopraxia.logstash;

import static com.tersesystems.echopraxia.api.Value.*;
import static java.util.concurrent.TimeUnit.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.echopraxia.*;
import com.tersesystems.echopraxia.api.Condition;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.Level;
import com.tersesystems.echopraxia.api.Value;
import com.tersesystems.echopraxia.async.AsyncLogger;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

public class ConditionTest extends TestBase {

  @Test
  void testCondition() {
    Logger<?> logger = getLogger();
    Condition condition = (l, c) -> true;
    logger.info(condition, "info");

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    String message = event.getFormattedMessage();
    assertThat(message).isEqualTo("info");
  }

  @Test
  void testConditionWithContext() {
    Condition hasInfoLevel = (level, context) -> level.equals(Level.INFO);

    Logger<?> logger = getLogger();
    Logger<?> loggerWithCondition =
        logger.withCondition(hasInfoLevel).withFields(f -> f.string("herp", "derp"));

    Condition hasFieldNamedHerp =
        (level, c) -> c.getFields().stream().anyMatch(f -> f.name().equals("herp"));
    loggerWithCondition.info(hasFieldNamedHerp, "info");

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isNotEmpty();

    final ILoggingEvent event = listAppender.list.get(0);
    String message = event.getFormattedMessage();
    assertThat(message).isEqualTo("info");
  }

  @Test
  void testNumberMatch() {
    Condition logins = Condition.numberMatch("logins", v -> v.equals(number(1)));
    Logger<?> logger = getLogger();
    logger.info(logins, "{}", fb -> fb.number("logins", 1));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list.size()).isEqualTo(1);

    final ILoggingEvent event = listAppender.list.get(0);
    String message = event.getFormattedMessage();
    assertThat(message).isEqualTo("logins=1");
  }

  @Test
  void testBooleanMatch() {
    Condition logins = Condition.booleanMatch("isAwesome", v -> v.equals(Value.bool(true)));
    Logger<?> logger = getLogger();
    logger.info(logins, "{}", fb -> fb.bool("isAwesome", true));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list.size()).isEqualTo(1);

    final ILoggingEvent event = listAppender.list.get(0);
    String message = event.getFormattedMessage();
    assertThat(message).isEqualTo("isAwesome=true");
  }

  @Test
  void testObjectMatch() {
    Field field = Field.keyValue("foo", Value.string("bar"));
    Condition logins = Condition.objectMatch("myObject", v -> v.equals(Value.object(field)));
    Logger<?> logger = getLogger();
    logger.info(logins, "{}", fb -> fb.object("myObject", List.of(fb.string("foo", "bar"))));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list.size()).isEqualTo(1);

    final ILoggingEvent event = listAppender.list.get(0);
    String message = event.getFormattedMessage();
    assertThat(message).isEqualTo("myObject={foo=bar}");
  }

  @Test
  void testArrayMatch() {
    Condition logins = Condition.arrayMatch("myarray", v -> v.equals(Value.array("foo")));
    Logger<?> logger = getLogger();
    logger.info(logins, "{}", fb -> fb.array("myarray", "foo"));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list.size()).isEqualTo(1);

    final ILoggingEvent event = listAppender.list.get(0);
    String message = event.getFormattedMessage();
    assertThat(message).isEqualTo("myarray=[foo]");
  }

  @Test
  void testFieldConditionIsLive() {
    Condition hasDerp = Condition.stringMatch("herp", v -> Value.equals(v, string("derp")));
    Logger<?> logger = getLogger();
    final AtomicReference<String> changeableValue = new AtomicReference<>("derp");

    // we need to know that withFields is "call by name" and is evaluated fresh on every statement.
    Logger<?> loggerWithContext = logger.withFields(f -> f.string("herp", changeableValue.get()));

    loggerWithContext.info(hasDerp, "has derp");
    changeableValue.set("notderp");
    loggerWithContext.info(hasDerp, "should not log");

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list.size()).isEqualTo(1);

    final ILoggingEvent event = listAppender.list.get(0);
    String message = event.getFormattedMessage();
    assertThat(message).isEqualTo("has derp");
  }

  @Test
  void testMDCConditionIsLive() {
    Condition hasDerp = Condition.stringMatch("herp", v -> Value.equals(v, string("derp")));
    Logger<?> logger = getLogger();

    MDC.put("herp", "derp");
    // we need to know that withFields is "call by name" and is evaluated fresh on every statement.
    Logger<?> loggerWithContext = logger.withThreadContext();

    loggerWithContext.info(hasDerp, "has derp");
    MDC.put("herp", "notderp");
    loggerWithContext.info(hasDerp, "should not log");

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list.size()).isEqualTo(1);

    final ILoggingEvent event = listAppender.list.get(0);
    String message = event.getFormattedMessage();
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

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();

    await().atMost(1, SECONDS).until(logged::get);

    final ILoggingEvent event = listAppender.list.get(0);
    String message = event.getFormattedMessage();
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
    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();

    Throwable actualException = StaticExceptionHandler.head();
    assertThat(actualException.getMessage()).isEqualTo("oh noes!");
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

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list.size()).isEqualTo(0);
    assertThat(StaticExceptionHandler.head().getMessage()).isEqualTo("oh noes!");
  }
}
