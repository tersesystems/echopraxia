package echopraxia.jul;

import static org.assertj.core.api.Assertions.assertThat;

import echopraxia.logging.api.Condition;
import org.junit.jupiter.api.Test;

public class ExceptionHandlerTests extends TestBase {

  @Test
  public void testBadArgument() {
    var logger = getLogger();
    Integer number = null;
    logger.debug("this has a null value", fb -> fb.number("nullNumber", number.intValue()));

    Throwable throwable = StaticExceptionHandler.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testBadWithField() {
    var logger = getLogger();
    Integer number = null;
    var badLogger = logger.withFields(fb -> fb.number("nullNumber", number.intValue()));
    badLogger.debug("this has a null value");

    Throwable throwable = StaticExceptionHandler.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testConditionAndBadWithField() {
    var logger = getLogger();
    Integer number = null;

    Condition condition = Condition.numberMatch("testing", p -> p.raw().intValue() == 5);

    var badLogger = logger.withFields(fb -> fb.number("nullNumber", number.intValue()));
    badLogger.debug(condition, "I have a bad logger field and a good condition");

    Throwable throwable = StaticExceptionHandler.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testBadConditionWithCondition() {
    var logger = getLogger();
    Integer number = null;
    // match on a null condition that will explode
    Condition badCondition =
        Condition.numberMatch("testing", p -> p.raw().intValue() == number.intValue());

    var badLogger = logger.withCondition(badCondition);
    badLogger.debug("I am passing in {}", fb -> fb.number("testing", 5));

    Throwable throwable = StaticExceptionHandler.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testBadCondition() {
    var logger = getLogger();
    Integer number = null;
    Condition badCondition = (level, context) -> number.intValue() == 5;

    logger.debug(badCondition, "I am passing in {}");

    Throwable throwable = StaticExceptionHandler.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testBadConditionAndArgument() {
    var logger = getLogger();
    Integer number = null;
    // match on a null condition that will explode
    Condition badCondition =
        Condition.numberMatch("testing", p -> p.raw().intValue() == number.intValue());

    logger.debug(
        badCondition, "I am passing in {}", fb -> fb.number("nullNumber", number.intValue()));

    Throwable throwable = StaticExceptionHandler.head();
    assertThat(throwable).isInstanceOf(NullPointerException.class);
  }
}
