package com.tersesystems.echopraxia.logstash;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import java.util.Arrays;
import java.util.UUID;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.junit.jupiter.api.Test;

class LoggerTest extends TestBase {

  @Test
  void testDebug() {
    Logger<?> logger = getLogger();
    logger.debug("hello");

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello");
  }

  @Test
  void testArguments() {
    Logger<?> logger = getLogger();
    logger.debug(
        "hello {}, you are {}, citizen status {}",
        fb -> fb.list(fb.string("name", "will"), fb.number("age", 13), fb.bool("citizen", true)));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello will, you are 13, citizen status true");
  }

  private Logger<?> getLogger() {
    LogstashCoreLogger logstashCoreLogger =
        new LogstashCoreLogger(factory.getLogger(getClass().getName()));
    return LoggerFactory.getLogger(logstashCoreLogger, Field.Builder.instance());
  }

  @Test
  void testArrayOfStringsArgument() {
    Logger<?> logger = getLogger();
    logger.debug("hello {}", fb -> fb.onlyArray("toys", Field.Value.string("binkie")));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello toys=[binkie]");
  }

  // XXX test array of objects

  @Test
  void testNullArgument() {
    Logger<?> logger = getLogger();
    logger.debug("hello {}", fb -> fb.list(fb.nullValue("nothing")));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello null");
  }

  @Test
  void testObjectArgument() {
    Logger<?> logger = getLogger();
    logger.debug(
        "hello {}",
        fb -> {
          Field name = fb.string("name", "will");
          Field age = fb.number("age", 13);
          Field toys = fb.array("toys", "binkie", "dotty");
          Field person = fb.object("person", name, age, toys);
          return fb.list(person);
        });

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello person=[will, 13, toys=[binkie, dotty]]");
  }

  @Test
  void testException() {
    Logger<?> logger = getLogger();
    logger.error("Error", new IllegalStateException("oh noes"));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("Error");
    final IThrowableProxy throwableProxy = event.getThrowableProxy();
    assertThat(throwableProxy).isNotNull();
  }

  @Test
  void testArgumentsException() {
    Logger<?> logger = getLogger();
    logger.error(
        "Error {}",
        fb ->
            Arrays.asList(
                fb.string("operation", "MyOperation"),
                fb.exception(new IllegalStateException("oh noes"))));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("Error MyOperation");
    final IThrowableProxy throwableProxy = event.getThrowableProxy();
    assertThat(throwableProxy).isNotNull();
  }

  interface UUIDFieldBuilder extends Field.Builder {
    default Field uuid(String name, UUID uuid) {
      return string(name, uuid.toString());
    }
  }

  @Test
  void testCustomMapping() {
    Logger<UUIDFieldBuilder> logger = getLogger().withFieldBuilder(new UUIDFieldBuilder() {});

    UUID uuid = UUID.randomUUID();
    logger.error("user id {}", fb -> fb.only(fb.uuid("user_id", uuid)));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String message = event.getMessage();
    assertThat(message).isEqualTo("user id {}");
    final Object[] args = event.getArgumentArray();
    final ObjectAppendingMarker actual = (ObjectAppendingMarker) args[0];
    assertThat(actual.getFieldValue()).isEqualTo(uuid.toString());
  }
}
