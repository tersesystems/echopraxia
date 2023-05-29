package com.tersesystems.echopraxia.jul;

import static org.assertj.core.api.Assertions.assertThat;

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

    Throwable throwable = StaticExceptionHandler.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testBadWithField() {
    Logger<?> logger = getLogger();
    Integer number = null;
    Logger<?> badLogger = logger.withFields(fb -> fb.number("nullNumber", number.intValue()));
    badLogger.debug("this has a null value");

    Throwable throwable = StaticExceptionHandler.head();
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

    Throwable throwable = StaticExceptionHandler.head();
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

    Throwable throwable = StaticExceptionHandler.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testBadCondition() {
    Logger<?> logger = getLogger();
    Integer number = null;
    Condition badCondition = (level, context) -> number.intValue() == 5;

    logger.debug(badCondition, "I am passing in {}");

    Throwable throwable = StaticExceptionHandler.head();
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

    Throwable throwable = StaticExceptionHandler.head();
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
    org.awaitility.Awaitility.await().until(() -> StaticExceptionHandler.head() != null);

    Throwable throwable = StaticExceptionHandler.head();
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
    org.awaitility.Awaitility.await().until(() -> StaticExceptionHandler.head() != null);

    Throwable throwable = StaticExceptionHandler.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }
}
