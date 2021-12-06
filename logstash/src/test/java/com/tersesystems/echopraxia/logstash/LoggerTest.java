package com.tersesystems.echopraxia.logstash;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Logger;
import java.util.Arrays;
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
        fb ->
            Arrays.asList(
                fb.string("name", "will"), fb.number("age", 13), fb.bool("citizen", true)));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello will, you are 13, citizen status true");
  }

  private Logger<Field.Builder> getLogger() {
    return new Logger<>(
        new LogstashCoreLogger(factory.getLogger(getClass().getName())),
        Logger.defaultFieldBuilder());
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
    logger.debug("hello {}", fb -> singletonList(fb.nullValue("nothing")));

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
          Field toys = fb.array("toys", Field.Value.string("binkie"));
          Field person = fb.object("person", name, age, toys);
          return singletonList(person);
        });

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello person=[name=will, age=13, toys=[binkie]]");
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

  //  @Test
  //  void testValueMapping() {
  //      Logger<?> logger = getLogger();
  //    ValueMapper<UUID, String> uuidMapper = uuid -> Value.string(uuid.toString());
  //    UUID uuid = UUID.randomUUID();
  //    logger.error("user id {}", uuidMapper.field("user_id", uuid));
  //
  //    final ListAppender<ILoggingEvent> listAppender = getListAppender();
  //    final ILoggingEvent event = listAppender.list.get(0);
  //    final String message = event.getMessage();
  //    assertThat(message).isEqualTo("user id {}");
  //    final Object[] args = event.getArgumentArray();
  //    assertThat(args[0]).isEqualTo(StructuredArguments.v("user_id", uuid));
  //  }

}
