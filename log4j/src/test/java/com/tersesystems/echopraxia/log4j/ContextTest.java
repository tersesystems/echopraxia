package com.tersesystems.echopraxia.log4j;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.junit.jupiter.api.Test;

public class ContextTest extends TestBase {

  @Test
  void testMarkers() {
    Marker securityMarker = MarkerManager.getMarker("SECURITY");
    final Log4JCoreLogger core = new Log4JCoreLogger((ExtendedLogger) LogManager.getLogger());
    Logger<?> logger =
        LoggerFactory.getLogger(core.withMarker(securityMarker), Field.Builder.instance());
    logger.error("Message {}", fb -> fb.onlyString("field_name", "field_value"));

    JsonObject entry = getEntry();
    final String message = entry.getString("message");
    assertThat(message).isEqualTo("Message field_value");
    final String actual = entry.getString("marker");
    assertThat(actual).isEqualTo("SECURITY");
  }

  @Test
  void testMarkerPredicate() {
    // Because MarkerFilter with ACCEPT exists,
    // AND we have a SECURITY marker in context
    // isTraceEnabled should return true even without an explicit marker.
    final Marker securityMarker = MarkerManager.getMarker("SECURITY");
    final Log4JCoreLogger core = new Log4JCoreLogger((ExtendedLogger) LogManager.getLogger());
    Logger<?> logger =
        LoggerFactory.getLogger(core.withMarker(securityMarker), Field.Builder.instance());

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
        .error("this is a message with complex fields");

    JsonObject entry = getEntry();
    JsonObject context = entry.getJsonObject("context");
    final JsonObject person = context.getJsonObject("person");
    assertThat(person.getString("name")).isEqualTo("will");
    assertThat(person.getInt("age")).isEqualTo(13);
    JsonArray toys = person.getJsonArray("toys");
    assertThat(toys.getString(0)).isEqualTo("binkie");
  }

  @Test
  void testThreadContext() {
    ThreadContext.put("mdckey", "mdcvalue");
    Logger<?> logger = getLogger();
    logger.withThreadContext().error("message with mdc context");

    JsonObject entry = getEntry();
    JsonObject context = entry.getJsonObject("context");
    final String value = context.getString("mdckey");
    assertThat(value).isEqualTo("mdcvalue");
  }
}
