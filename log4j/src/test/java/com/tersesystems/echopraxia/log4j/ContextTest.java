package com.tersesystems.echopraxia.log4j;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.api.Condition;
import com.tersesystems.echopraxia.api.FieldBuilder;
import com.tersesystems.echopraxia.model.Field;
import com.tersesystems.echopraxia.spi.CoreLoggerFactory;
import java.util.Map;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ContextTest extends TestBase {

  @BeforeEach
  void clearThreadContext() {
    ThreadContext.clearAll();
  }

  @Test
  void testGetLoggerContext() {
    Log4JCoreLogger core =
        (Log4JCoreLogger) CoreLoggerFactory.getLogger(Logger.class.getName(), ContextTest.class);
    var logger =
        LoggerFactory.getLogger(core, FieldBuilder.instance())
            .withFields(fb -> fb.string("herp", "derp"));
    var coreWithFields = logger.core();
    var fields = coreWithFields.getLoggerContext().getLoggerFields();

    Field field = fields.get(0);
    assertThat(field.name()).isEqualTo("herp");
    assertThat(field.value().asString().raw()).isEqualTo("derp");
  }

  @Test
  void testMarkers() {
    Marker securityMarker = MarkerManager.getMarker("SECURITY");
    Log4JCoreLogger core =
        (Log4JCoreLogger) CoreLoggerFactory.getLogger(Logger.class.getName(), ContextTest.class);
    var logger = LoggerFactory.getLogger(core.withMarker(securityMarker), FieldBuilder.instance());
    logger.error("Message {}", fb -> fb.string("field_name", "field_value"));

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("Message field_name=field_value");
    final String actual = entry.path("marker").asText();
    assertThat(actual).isEqualTo("SECURITY");
  }

  @Test
  void testMarkerPredicate() {
    // Because MarkerFilter with ACCEPT exists,
    // AND we have a SECURITY marker in context
    // isTraceEnabled should return true even without an explicit marker.
    final Marker securityMarker = MarkerManager.getMarker("SECURITY");
    Log4JCoreLogger core =
        (Log4JCoreLogger) CoreLoggerFactory.getLogger(Logger.class.getName(), ContextTest.class);
    var logger = LoggerFactory.getLogger(core.withMarker(securityMarker), FieldBuilder.instance());

    org.apache.logging.log4j.Logger log4jLogger = core.logger();

    // Calling log4j directly should return true...
    assertThat(log4jLogger.isTraceEnabled(securityMarker)).isTrue();

    // But otherwise TRACE is not enabled...
    assertThat(log4jLogger.isTraceEnabled()).isFalse();

    // And as the marker is in context, it should be true as well.
    assertThat(logger.isTraceEnabled()).isTrue();
  }

  @Test
  void testComplexFields() {
    var logger = getLogger();
    logger
        .withFields(
            fb -> {
              Field name = fb.string("name", "will");
              Field age = fb.number("age", 13);
              Field toys = fb.array("toys", "binkie");
              Field person = fb.object("person", name, age, toys);
              return person;
            })
        .error("this is a message with complex fields");

    JsonNode entry = getEntry();
    JsonNode context = entry.path("context");
    final JsonNode person = context.path("person");
    assertThat(person.path("name").asText()).isEqualTo("will");
    assertThat(person.path("age").asInt()).isEqualTo(13);
    JsonNode toys = person.path("toys");
    assertThat(toys.get(0).asText()).isEqualTo("binkie");
  }

  @Test
  void testThreadContext() {
    ThreadContext.put("mdckey", "mdcvalue");
    var logger = getLogger();
    logger.withThreadContext().error("message with mdc context");

    JsonNode entry = getEntry();
    JsonNode context = entry.path("context");
    final String value = context.path("mdckey").asText();
    assertThat(value).isEqualTo("mdcvalue");
  }

  @Test
  void testFindExceptionStackTraceElement() {
    var logger = getLogger();
    Condition c =
        (level, ctx) -> {
          Map<String, ?> element = ctx.findObject("$.exception.stackTrace[0]").get();
          return element.get("fileName").toString().endsWith("ContextTest.java");
        };
    RuntimeException e = new RuntimeException("test message");
    logger.info(c, "Matches on exception", e);

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("Matches on exception");
  }

  @Test
  void testFindDouble() {
    var logger = getLogger();
    Condition c =
        (level, ctx) -> ctx.findNumber("$.arg1").filter(f -> f.doubleValue() == 1.5).isPresent();
    logger.info(c, "Matches on arg1", fb -> fb.number("arg1", 1.5));

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("Matches on arg1");
  }
}
