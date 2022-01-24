package com.tersesystems.echopraxia.logstash;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.core.CoreLogger;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.logstash.logback.marker.EmptyLogstashMarker;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class ContextTest extends TestBase {

  private static final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

  @Test
  void testMarkers() {

    final Marker securityMarker = MarkerFactory.getMarker("SECURITY");
    final LogstashCoreLogger core = new LogstashCoreLogger(factory.getLogger(getClass().getName()));
    Logger<?> logger =
        LoggerFactory.getLogger(core.withMarkers(securityMarker), Field.Builder.instance());
    logger.withFields(f -> f.onlyString("key", "value")).error("This has a marker");

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("This has a marker");
    final List<Marker> markers = getMarkers(event);

    testMarker(markers.get(0), "key", "value");
    final Marker shouldBeSecurityMarker = markers.get(1);
    assertThat(shouldBeSecurityMarker).isSameAs(securityMarker);
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
  void testComplexFields() throws IOException {
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
    final List<Marker> markers = getMarkers(event);

    final ObjectAppendingMarker marker = (ObjectAppendingMarker) markers.get(0);

    // getFieldValue calls the "logfmt" version that is fed into formatted message
    final String actual = (String) marker.getFieldValue();
    assertThat(actual).isEqualTo("[will, 13, toys=[binkie]]");

    final StringWriter sw = new StringWriter();
    final JsonGenerator generator = mapper.createGenerator(sw);
    generator.writeStartObject();
    marker.writeTo(generator);
    generator.writeEndObject();
    generator.close();
    assertThat(sw.toString())
        .isEqualTo("{\"person\":{\"name\":\"will\",\"age\":13,\"toys\":[\"binkie\"]}}");
  }

  @Test
  void testArrays() throws IOException {
    Logger<?> logger = getLogger();
    logger
        .withFields(
            fb -> {
              Field.Value a4 = Field.Value.array(true, false, true);
              Field.Value a3 = Field.Value.array("1", "2", "3");
              Field.Value a2 = Field.Value.array(1, 2, 3);
              Field field = fb.array("a1", a2, a3, a4);
              return singletonList(field);
            })
        .error("This has a marker");

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final List<Marker> markers = getMarkers(event);

    final ObjectAppendingMarker marker = (ObjectAppendingMarker) markers.get(0);

    // getFieldValue calls the "logfmt" version that is fed into formatted message
    final String actual = (String) marker.getFieldValue();
    assertThat(actual).isEqualTo("[[1, 2, 3], [1, 2, 3], [true, false, true]]");

    final StringWriter sw = new StringWriter();
    final JsonGenerator generator = mapper.createGenerator(sw);
    generator.writeStartObject();
    marker.writeTo(generator);
    generator.writeEndObject();
    generator.close();
    assertThat(sw.toString()).isEqualTo("{\"a1\":[[1,2,3],[\"1\",\"2\",\"3\"],[true,false,true]]}");
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

    final List<Marker> markers = getMarkers(event);
    final ObjectAppendingMarker m1 = (ObjectAppendingMarker) markers.get(0);
    final ObjectAppendingMarker m2 = (ObjectAppendingMarker) markers.get(1);

    testMarker(m1, "key", "value");
    testMarker(m2, "key2", "value2");
  }

  @Test
  void testThreadContext() {
    MDC.put("mdckey", "mdcvalue");
    Logger<?> logger = getLogger().withThreadContext();
    logger.info("some message");

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("some message");

    final List<Marker> markers = getMarkers(event);
    testMarker(markers.get(0), "mdckey", "mdcvalue");
  }

  private void testMarker(Marker marker, String key, Object value) {
    final ObjectAppendingMarker m = (ObjectAppendingMarker) marker;

    // You can't use ObjectAppendingMarker.equals because it wants instance equality.
    assertThat(m.getFieldName()).isEqualTo(key);
    assertThat(m.getFieldValue()).isEqualTo(value);
  }

  // we can't use the result of Markers.aggregate, as an EmptyLogstashMarker ONLY CHECKS THE NAME.
  private List<Marker> getMarkers(ILoggingEvent event) {
    final EmptyLogstashMarker m = (EmptyLogstashMarker) event.getMarker();
    Stream<Marker> stream = StreamSupport.stream(m.spliterator(), false);
    return stream.collect(Collectors.toList());
  }

  private Logger<?> getLogger() {
    final LogstashCoreLogger logstashCoreLogger =
        new LogstashCoreLogger(factory.getLogger(getClass().getName()));
    return LoggerFactory.getLogger(logstashCoreLogger, Field.Builder.instance());
  }
}
