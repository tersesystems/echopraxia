package echopraxia.logstash;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import echopraxia.api.Condition;
import org.junit.jupiter.api.Test;

public class ExceptionHandlerTests extends TestBase {

  @Test
  public void testBadArgument() {
    var logger = getLogger();
    Integer number = null;
    logger.debug("this has a null value", fb -> fb.number("nullNumber", number.intValue()));
    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();

    Throwable throwable = StaticExceptionHandler.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testBadWithField() {
    var logger = getLogger();
    Integer number = null;
    var badLogger = logger.withFields(fb -> fb.number("nullNumber", number.intValue()));
    badLogger.debug("this has a null value");
    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();

    Throwable throwable = StaticExceptionHandler.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testConditionAndBadWithField() {
    var logger = getLogger();
    Integer number = null;
    Condition condition =
        (level, context) -> context.findNumber("$.testing").stream().anyMatch(p -> p.equals(5));

    var badLogger = logger.withFields(fb -> fb.number("nullNumber", number.intValue()));
    badLogger.debug(condition, "I have a bad logger field and a good condition");
    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();

    Throwable throwable = StaticExceptionHandler.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testBadConditionWithCondition() {
    var logger = getLogger();
    Integer number = null;
    Condition badCondition =
        (level, context) ->
            context.findNumber("$.testing").stream().anyMatch(p -> p.equals(number.intValue()));

    var badLogger = logger.withCondition(badCondition);
    badLogger.debug("I am passing in {}", fb -> fb.number("testing", 5));
    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();

    Throwable throwable = StaticExceptionHandler.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testBadCondition() {
    var logger = getLogger();
    Integer number = null;
    Condition badCondition = (level, context) -> number.intValue() == 5;

    logger.debug(badCondition, "I am passing in {}");
    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();

    Throwable throwable = StaticExceptionHandler.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testBadConditionAndArgument() {
    var logger = getLogger();
    Integer number = null;
    Condition badCondition =
        (level, context) ->
            context.findNumber("$.testing").stream().anyMatch(p -> p.equals(number.intValue()));

    logger.debug(
        badCondition, "I am passing in {}", fb -> fb.number("nullNumber", number.intValue()));
    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();

    Throwable throwable = StaticExceptionHandler.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testAsyncLog() {
    var asyncLogger = getAsyncLogger();
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
    var asyncLogger = getAsyncLogger();
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
