package com.tersesystems.echopraxia.logstash;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.echopraxia.*;
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

  private Logger<?> getLogger() {
    final LogstashCoreLogger logstashCoreLogger =
        new LogstashCoreLogger(factory.getLogger(getClass().getName()));
    return LoggerFactory.getLogger(logstashCoreLogger, Field.Builder.instance());
  }

  @Test
  void testConditionWithContext() {
    Condition hasInfoLevel = (level, context) -> level.equals(Level.INFO);

    Logger<?> logger = getLogger();
    Logger<?> loggerWithCondition =
        logger.withCondition(hasInfoLevel).withFields(f -> f.onlyString("herp", "derp"));

    Condition hasFieldNamedHerp =
        (level, c) -> c.getFields().stream().anyMatch(f -> f.name().equals("herp"));
    loggerWithCondition.info(hasFieldNamedHerp, "info");

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isNotEmpty();

    final ILoggingEvent event = listAppender.list.get(0);
    String message = event.getFormattedMessage();
    assertThat(message).isEqualTo("info");
  }
}
