package com.tersesystems.echopraxia.logstash;

import static java.util.concurrent.TimeUnit.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.echopraxia.*;
import com.tersesystems.echopraxia.api.Condition;
import com.tersesystems.echopraxia.api.Level;
import com.tersesystems.echopraxia.async.AsyncLogger;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

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
    assertThat(listAppender.list).isNotEmpty();

    final ILoggingEvent event = listAppender.list.get(0);
    String message = event.getFormattedMessage();
    assertThat(message).isEqualTo("Uncaught exception when running asyncLog");
    Throwable actualException = ((ThrowableProxy) event.getThrowableProxy()).getThrowable();
    assertThat(actualException.getMessage()).isEqualTo("oh noes!");
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
                return fb.string("foo", "bar");
              });
        });

    await().atLeast(100, MILLISECONDS).until(logged::get);

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isNotEmpty();
    final ILoggingEvent event = listAppender.list.get(0);
    String message = event.getFormattedMessage();
    assertThat(message).isEqualTo("Uncaught exception when running asyncLog");
    Throwable actualException = ((ThrowableProxy) event.getThrowableProxy()).getThrowable();
    assertThat(actualException.getMessage()).isEqualTo("oh noes!");
  }
}
