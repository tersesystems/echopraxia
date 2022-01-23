package com.tersesystems.echopraxia.logstash;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.core.CoreLogger;
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

  @Test
  void testMarkerPredicate() {
    // Because MarkerFilter with ACCEPT exists,
    // AND we have a SECURITY marker in context
    // isTraceEnabled should return true even without an explicit marker.
    final Marker securityMarker = MarkerFactory.getMarker("SECURITY");
    final LogstashCoreLogger core = new LogstashCoreLogger(factory.getLogger(getClass().getName()));
    final CoreLogger security = core.withMarkers(securityMarker);
    Logger<?> logger = LoggerFactory.getLogger(security, Field.Builder.instance());

    final org.slf4j.Logger slf4jLogger = core.logger();

    // Calling SLF4J directly should return true...
    assertThat(slf4jLogger.isTraceEnabled(securityMarker)).isTrue();

    // But otherwise TRACE is not enabled...
    assertThat(slf4jLogger.isTraceEnabled()).isFalse();

    // And as the marker is in context, it should be true as well.
    assertThat(logger.isTraceEnabled()).isTrue();
  }

  @Test
  void testComplexFields() {
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

  @Test
  void testThreadContext() {
    fail();
  }

  private Logger<?> getLogger() {
    final LogstashCoreLogger logstashCoreLogger =
        new LogstashCoreLogger(factory.getLogger(getClass().getName()));
    return LoggerFactory.getLogger(logstashCoreLogger, Field.Builder.instance());
  }
}
