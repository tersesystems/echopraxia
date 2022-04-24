package com.tersesystems.echopraxia.logstash;

import static com.jayway.jsonpath.Filter.*;
import static com.tersesystems.echopraxia.Field.Value;
import static com.tersesystems.echopraxia.Field.Value.*;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.tersesystems.echopraxia.*;
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
    final LogstashCoreLogger core =
        new LogstashCoreLogger(Logger.FQCN, loggerContext.getLogger(getClass().getName()));
    Logger<?> logger =
        LoggerFactory.getLogger(core.withMarkers(securityMarker), FieldBuilder.instance());
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
    final LogstashCoreLogger core =
        new LogstashCoreLogger(Logger.FQCN, loggerContext.getLogger(getClass().getName()));
    final CoreLogger security = core.withMarkers(securityMarker);
    Logger<?> logger = LoggerFactory.getLogger(security, FieldBuilder.instance());

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
              Field toys = fb.array("toys", "binkie");
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
    assertThat(actual).isEqualTo("{will, 13, toys=[binkie]}");

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
              Value.ArrayValue a4 = array(true, false, true);
              Value.ArrayValue a3 = array("1", "2", "3");
              Value.ArrayValue a2 = array(1, 2, 3);
              Field field = fb.array("a1", array(a2, a3, a4));
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

  @Test
  void testFindString() {
    Logger<?> logger = getLogger();
    Condition c =
        (level, ctx) -> ctx.findString("$.arg1").filter(v -> v.equals("value1")).isPresent();
    logger.info(c, "Matches on arg1", fb -> fb.onlyString("arg1", "value1"));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("Matches on arg1");
  }

  @Test
  void testFindInteger() {
    Logger<?> logger = getLogger();
    Condition c =
        (level, ctx) -> ctx.findNumber("$.arg1").filter(v -> v.intValue() == 1).isPresent();
    logger.info(c, "Matches on arg1", fb -> fb.onlyNumber("arg1", 1));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("Matches on arg1");
  }

  @Test
  void testFindBoolean() {
    Logger<?> logger = getLogger();
    Condition c = (level, ctx) -> ctx.findBoolean("$.arg1").orElse(false);
    logger.info(c, "Matches on arg1", fb -> fb.onlyBool("arg1", true));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("Matches on arg1");
  }

  @Test
  void testFindDouble() {
    Logger<?> logger = getLogger();
    Condition c =
        (level, ctx) -> ctx.findNumber("$.arg1").filter(f -> f.doubleValue() == 1.5).isPresent();
    logger.info(c, "Matches on arg1", fb -> fb.onlyNumber("arg1", 1.5));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("Matches on arg1");
  }

  @Test
  void testInlinePredicate() {
    Logger<?> logger = getLogger();
    final Condition cheapBookCondition =
        (level, context) -> !context.findList("$.store.book[?(@.price < 10)]").isEmpty();

    logger.info(
        cheapBookCondition,
        "found cheap books",
        fb -> {
          Field category = fb.string("category", "fiction");
          Field price = fb.number("price", 5);
          Field book = fb.object("book", category, price);
          return fb.onlyObject("store", book);
        });

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("found cheap books");
  }

  @Test
  void testFindNull() {
    Logger<?> logger = getLogger();
    final Condition nullCondition = (level, context) -> context.findNull("$.myNullField");

    logger.info(nullCondition, "found null", fb -> fb.onlyNullField("myNullField"));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("found null");
  }

  @Test
  void testFindNullButString() {
    Logger<?> logger = getLogger();
    final Condition nullCondition = (level, context) -> context.findNull("$.myNullField");

    logger.info(nullCondition, "found null", fb -> fb.onlyString("myNullField", "notnull"));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();
  }

  @Test
  void testJsonPathMissingProperty() {
    Logger<?> logger = getLogger();
    final Condition noFindException =
        (level, ctx) -> ctx.findString("$.exception.message").isPresent();

    logger.info(noFindException, "no exception in this message");

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();
  }

  @Test
  void testMismatchedString() {
    Logger<?> logger = getLogger();
    final Condition noFindException = (level, ctx) -> ctx.findString("$.notastring").isPresent();

    // property is present but is boolean, not a string
    logger.info(noFindException, "this should not log", fb -> fb.onlyBool("notastring", true));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();
  }

  @Test
  void testMismatchedObject() {
    Logger<?> logger = getLogger();
    final Condition noFindException = (level, ctx) -> ctx.findObject("$.notanobject").isPresent();

    // property is present but is boolean, not a string
    logger.info(noFindException, "this should not log", fb -> fb.onlyBool("notanobject", true));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();
  }

  @Test
  void testMismatchedList() {
    Logger<?> logger = getLogger();
    final Condition noFindException = (level, ctx) -> ctx.findList("$.notalist").size() > 0;

    // property is present but is boolean, not a string
    logger.info(noFindException, "this should not log", fb -> fb.onlyBool("notalist", true));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isEmpty();
  }

  @Test
  void testFindException() {
    Logger<?> logger = getLogger();
    Condition c =
        (level, ctx) ->
            ctx.findThrowable("$.exception")
                .filter(e -> "test message".equals(e.getMessage()))
                .isPresent();
    RuntimeException e = new RuntimeException("test message");
    logger.info(c, "Matches on exception", e);

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("Matches on exception");
  }

  @Test
  void testNullMessageInException() {
    Logger<?> logger = getLogger();
    Condition c = (level, ctx) -> ctx.findNull("$.exception.message");
    RuntimeException e = new IllegalArgumentException((String) null);
    logger.info(c, "Matches on null message in exception", e);

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("Matches on null message in exception");
  }

  @Test
  void testNullInNestedArray() {
    Logger<?> logger = getLogger();
    Condition c = (level, ctx) -> ctx.findList("$..interests").size() > 0;
    logger.info(
        c,
        "Can manage null in array",
        fb -> fb.onlyObject("foo", fb.onlyArray("interests", "foo", null, null)));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("Can manage null in array");
  }

  @Test
  void testObjectArrayObject() {
    Logger<?> logger = getLogger();
    Condition c = (level, ctx) -> ctx.findBoolean("$.foo.array[2].one").get();
    logger.info(
        c,
        "complex objects",
        fb -> {
          Value.ObjectValue objectValue =
              object(fb.bool("one", true), fb.string("two", "t"), fb.number("three", 3));
          final Value.ArrayValue arrayValue =
              array(string("interests"), string("foo"), objectValue);
          return fb.onlyObject("foo", fb.onlyArray("array", arrayValue));
        });

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("complex objects");
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
}
