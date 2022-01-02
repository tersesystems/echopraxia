package com.tersesystems.echopraxia.logstash;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.junit.jupiter.api.Test;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class ContextTest extends TestBase {

  @Test
  void testMarkers() {
    //
    final LogstashCoreLogger core = new LogstashCoreLogger(factory.getLogger(getClass().getName()));
    Logger<?> logger =
        LoggerFactory.getLogger(
            core.withMarkers(MarkerFactory.getMarker("SECURITY")), Field.Builder.instance());
    logger.withFields(f -> f.onlyString("key", "value")).error("This has a marker");

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("This has a marker");
    final LogstashMarker m = (LogstashMarker) event.getMarker();
    Marker expected =
        (Markers.appendEntries(singletonMap("key", "value")))
            .and(MarkerFactory.getMarker("SECURITY"));
    assertThat(m).isEqualTo(expected);
  }

  private Logger<?> getLogger() {
    final LogstashCoreLogger logstashCoreLogger =
        new LogstashCoreLogger(factory.getLogger(getClass().getName()));
    return LoggerFactory.getLogger(logstashCoreLogger, Field.Builder.instance());
  }

  @Test
  void testComplexMarkers() {
    Logger<?> logger = getLogger();
    logger
        .withFields(
            fb -> {
              Field name = fb.string("name", "will");
              Field age = fb.number("age", 13);
              Field toys = fb.array("toys", Field.Value.string("binkie"));
              Field person = fb.object("person", name, age, toys);
              return singletonList(person);
            })
        .error("This has a marker");

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final LogstashMarker m = (LogstashMarker) event.getMarker();

    final Map<String, Object> props = new HashMap<>();
    props.put("name", "will");
    props.put("age", 13);
    props.put("toys", singletonList("binkie"));
    assertThat(m).isEqualTo(Markers.appendEntries(singletonMap("person", props)));
  }

  @Test
  void testCombinedContext() {
    Logger<?> logger = getLogger();
    Logger<?> loggerWithContext = logger.withFields(f -> f.onlyString("key", "value"));
    loggerWithContext
        .withFields(f -> f.onlyString("key2", "value2"))
        .info("This should have two contexts.");

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("This should have two contexts.");
    final LogstashMarker m = (LogstashMarker) event.getMarker();

    Map<String, String> key = new HashMap<>();
    key.put("key", "value");
    key.put("key2", "value2");
    Marker expected = (Markers.appendEntries(key));
    assertThat(m).isEqualTo(expected);
  }
}
