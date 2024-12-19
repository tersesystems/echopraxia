package echopraxia.logback;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import echopraxia.api.Condition;
import echopraxia.api.FieldBuilder;
import echopraxia.api.Value;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.Marker;

public class ConditionTurboFilterTest extends TestBase {

  @Test
  void testConditionFail() {
    Logger logger = loggerContext().getLogger(getClass());

    Condition failCondition = (level, context) -> false;
    Marker failMarker = ConditionMarker.apply(failCondition);
    logger.info(failMarker, "This should never log");

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list.size()).isEqualTo(0);
  }

  @Test
  void testConditionPass() {
    Logger logger = loggerContext().getLogger(getClass());

    Condition successCondition = (level, context) -> true;
    Marker successMarker = ConditionMarker.apply(successCondition);
    logger.info(successMarker, "This should always log");

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list.size()).isEqualTo(1);
  }

  @Test
  void testConditionArgumentPass() {
    Logger logger = loggerContext().getLogger(getClass());
    FieldBuilder fb = FieldBuilder.instance();

    Condition successCondition =
        (level, ctx) -> ctx.findString("$.foo").filter(s -> s.equals("bar")).isPresent();
    Marker successMarker = ConditionMarker.apply(successCondition);
    logger.info(successMarker, "Argument {}", fb.string("foo", "bar"));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list.size()).isEqualTo(1);
  }

  @Test
  void testConditionArgumentFail() {
    Logger logger = loggerContext().getLogger(getClass());
    FieldBuilder fb = FieldBuilder.instance();

    Condition fooCondition =
        (level, ctx) -> ctx.findString("$.foo").filter(s -> s.equals("quux")).isPresent();
    Marker marker = ConditionMarker.apply(fooCondition);
    logger.info(marker, "Argument {}", fb.string("foo", "bar"));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list.size()).isEqualTo(0);
  }

  @Test
  void testConditionNumberPass() {
    Logger logger = loggerContext().getLogger(getClass());
    FieldBuilder fb = FieldBuilder.instance();

    Condition countCondition = Condition.numberMatch("count", c -> c.equals(Value.number(5)));
    Marker countMarker = ConditionMarker.apply(countCondition);
    logger.info(countMarker, "Argument {}", (fb.number("count", 5)));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list.size()).isEqualTo(1);
  }

  @Test
  void testTwoConditionsPass() {
    Logger logger = loggerContext().getLogger(getClass());
    FieldBuilder fb = FieldBuilder.instance();

    Condition fooCondition =
        (level, ctx) -> ctx.findString("$.foo").filter(s -> s.equals("bar")).isPresent();
    Condition countCondition = Condition.numberMatch("count", c -> c.equals(Value.number(5)));
    Marker fooMarker = ConditionMarker.apply(fooCondition);
    Marker countMarker = ConditionMarker.apply(countCondition);
    fooMarker.add(countMarker);
    logger.info(fooMarker, "Arguments {} {}", fb.string("foo", "bar"), fb.number("count", 5));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list.size()).isEqualTo(1);
  }

  @Test
  void testTwoConditionsFail() {
    Logger logger = loggerContext().getLogger(getClass());
    FieldBuilder fb = FieldBuilder.instance();

    Condition fooCondition =
        (level, ctx) -> ctx.findString("$.foo").filter(s -> s.equals("bar")).isPresent();
    Condition countCondition = Condition.numberMatch("count", c -> c.equals(Value.number(5)));
    Marker fooMarker = ConditionMarker.apply(fooCondition);
    Marker countMarker = ConditionMarker.apply(countCondition);
    fooMarker.add(countMarker);
    logger.info(fooMarker, "Arguments {} {}", fb.string("foo", "bar"), fb.number("count", 6));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list.size()).isEqualTo(0);
  }
}
