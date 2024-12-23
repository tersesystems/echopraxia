package echopraxia.logstash;

import static echopraxia.api.Value.*;
import static java.util.concurrent.TimeUnit.*;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import echopraxia.api.Field;
import echopraxia.api.Value;
import echopraxia.logging.api.Condition;
import echopraxia.logging.api.Level;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

public class ConditionTest extends TestBase {

  @Test
  void testCondition() {
    var logger = getLogger();
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

    var logger = getLogger();
    var loggerWithCondition =
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
    var logger = getLogger();
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
    var logger = getLogger();
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
    var logger = getLogger();
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
    var logger = getLogger();
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
    var logger = getLogger();
    final AtomicReference<String> changeableValue = new AtomicReference<>("derp");

    // we need to know that withFields is "call by name" and is evaluated fresh on every statement.
    var loggerWithContext = logger.withFields(f -> f.string("herp", changeableValue.get()));

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
    var logger = getLogger();

    MDC.put("herp", "derp");
    // we need to know that withFields is "call by name" and is evaluated fresh on every statement.
    var loggerWithContext = logger.withThreadContext();

    loggerWithContext.info(hasDerp, "has derp");
    MDC.put("herp", "notderp");
    loggerWithContext.info(hasDerp, "should not log");

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list.size()).isEqualTo(1);

    final ILoggingEvent event = listAppender.list.get(0);
    String message = event.getFormattedMessage();
    assertThat(message).isEqualTo("has derp");
  }
}
