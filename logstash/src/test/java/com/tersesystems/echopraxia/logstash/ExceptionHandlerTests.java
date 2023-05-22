package com.tersesystems.echopraxia.logstash;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.api.Condition;
import com.tersesystems.echopraxia.async.AsyncLogger;
import org.junit.jupiter.api.Test;

public class ExceptionHandlerTests extends TestBase {

  @Test
  public void testBadArgument() {
    Logger<?> logger = getLogger();
    Integer number = null;
    logger.debug("this has a null value", fb -> fb.number("nullNumber", number.intValue()));
    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();

    Throwable throwable = StaticExceptionHandlerProvider.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testBadWithField() {
    Logger<?> logger = getLogger();
    Integer number = null;
    Logger<?> badLogger = logger.withFields(fb -> fb.number("nullNumber", number.intValue()));
    badLogger.debug("this has a null value");
    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();

    Throwable throwable = StaticExceptionHandlerProvider.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testConditionAndBadWithField() {
    Logger<?> logger = getLogger();
    Integer number = null;
    Condition condition =
        (level, context) -> context.findNumber("$.testing").stream().anyMatch(p -> p.equals(5));

    Logger<?> badLogger = logger.withFields(fb -> fb.number("nullNumber", number.intValue()));
    badLogger.debug(condition, "I have a bad logger field and a good condition");
    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();

    Throwable throwable = StaticExceptionHandlerProvider.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testBadConditionWithCondition() {
    Logger<?> logger = getLogger();
    Integer number = null;
    Condition badCondition =
        (level, context) ->
            context.findNumber("$.testing").stream().anyMatch(p -> p.equals(number.intValue()));

    Logger<?> badLogger = logger.withCondition(badCondition);
    badLogger.debug("I am passing in {}", fb -> fb.number("testing", 5));
    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();

    Throwable throwable = StaticExceptionHandlerProvider.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testBadCondition() {
    Logger<?> logger = getLogger();
    Integer number = null;
    Condition badCondition = (level, context) -> number.intValue() == 5;

    logger.debug(badCondition, "I am passing in {}");
    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();

    Throwable throwable = StaticExceptionHandlerProvider.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testBadConditionAndArgument() {
    Logger<?> logger = getLogger();
    Integer number = null;
    Condition badCondition =
        (level, context) ->
            context.findNumber("$.testing").stream().anyMatch(p -> p.equals(number.intValue()));

    logger.debug(
        badCondition, "I am passing in {}", fb -> fb.number("nullNumber", number.intValue()));
    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();

    Throwable throwable = StaticExceptionHandlerProvider.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testAsyncLog() {
    AsyncLogger<?> asyncLogger = getAsyncLogger();
    Integer number = null;
    asyncLogger.info(
        handle -> {
          handle.log("bad argument", fb -> fb.number("nullNumber", number.intValue()));
        });
    org.awaitility.Awaitility.await().until(() -> StaticExceptionHandlerProvider.head() != null);

    Throwable throwable = StaticExceptionHandlerProvider.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testAsyncLogWithField() {
    AsyncLogger<?> asyncLogger = getAsyncLogger();
    Integer number = null;
    asyncLogger
        .withFields(fb -> fb.number("nullNumber", number.intValue()))
        .info(
            handle -> {
              handle.log("no argument");
            });
    org.awaitility.Awaitility.await().until(() -> StaticExceptionHandlerProvider.head() != null);

    Throwable throwable = StaticExceptionHandlerProvider.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }
}
