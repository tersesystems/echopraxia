package com.tersesystems.echopraxia.log4j;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.tersesystems.echopraxia.*;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.FieldBuilder;
import com.tersesystems.echopraxia.api.Value;
import com.tersesystems.echopraxia.async.AsyncLogger;
import com.tersesystems.echopraxia.async.AsyncLoggerFactory;
import org.junit.jupiter.api.Test;

public class Log4JLoggerTest extends TestBase {

  @Test
  void testNullMessage() {
    Logger<?> logger = getLogger();
    logger.debug(null);

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("null");
  }

  @Test
  void testNullStringArgument() {
    Logger<?> logger = getLogger();
    String value = null;
    logger.info("hello {}", fb -> (fb.string("name", value)));

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("hello name=null");
  }

  @Test
  void testNullFieldName() {
    Logger<?> logger = getLogger();
    String value = "value";
    logger.debug("array field is {}", fb -> (fb.array(null, value)));

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("array field is echopraxia-unknown-1=[value]");
  }

  @Test
  void testNullNumber() {
    Logger<?> logger = getLogger();
    Integer value = null;
    logger.debug("number is {}", fb -> (fb.number("name", value)));

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("number is name=0");
  }

  @Test
  void testNullBoolean() {
    Logger<?> logger = getLogger();
    logger.debug("boolean is {}", fb -> (fb.bool("name", (Boolean) null)));

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("boolean is name=false");
  }

  @Test
  void testNullArrayElement() {
    Logger<?> logger = getLogger();
    String[] values = {"1", null, "3"};
    logger.debug("array field is {}", fb -> (fb.array("arrayName", values)));

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("array field is arrayName=[1, null, 3]");
  }

  @Test
  void testNullObject() {
    Logger<?> logger = getLogger();
    logger.debug("object is {}", fb -> (fb.object("name", Value.object((Field) null))));

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message)
        .isEqualTo("object is name={}"); // {} here is literally an object with no fields
  }

  @Test
  public void testLoggerWithStringField() {
    Logger<?> logger = LoggerFactory.getLogger(getClass());
    logger.info("my argument is {}", fb -> fb.string("random_key", "random_value"));

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("my argument is random_key=random_value");

    final JsonNode fields = entry.path("fields");
    assertThat(fields.path("random_key").asText()).isEqualTo("random_value");
  }

  @Test
  public void testLoggerLocation() {
    Logger<?> logger = LoggerFactory.getLogger(getClass());
    logger.info("Boring Message"); // this is line 109

    JsonNode entry = getEntry();
    final JsonNode fields = entry.path("source");
    assertThat(fields.path("class").asText())
        .isEqualTo("com.tersesystems.echopraxia.log4j.Log4JLoggerTest");
    assertThat(fields.path("method").asText()).isEqualTo("testLoggerLocation");
    assertThat(fields.path("file").asText()).isEqualTo("Log4JLoggerTest.java");
    // disable the line check as it keeps changing...
    // assertThat(fields.getJsonNumber("line").intValue()).isEqualTo(109); // this is very
    // sensitive!
  }

  @Test
  public void testLoggerLocationWithAsyncLogger() {
    // note you must have includeLocation="true" in log4j2.xml to trigger the
    // throwable / stacktrace in the core logger...
    AsyncLogger<?> asyncLogger = AsyncLoggerFactory.getLogger(getClass(), FieldBuilder.instance());
    asyncLogger.info("Boring Message");

    waitUntilMessages();

    JsonNode entry = getEntry();

    final JsonNode fields = entry.path("source");
    assertThat(fields.path("class").asText())
        .isEqualTo("com.tersesystems.echopraxia.log4j.Log4JLoggerTest");
    assertThat(fields.path("method").asText()).isEqualTo("testLoggerLocationWithAsyncLogger");
    assertThat(fields.path("file").asText()).isEqualTo("Log4JLoggerTest.java");
  }

  @Test
  public void testLoggerWithObjectField() {
    Logger<?> logger = LoggerFactory.getLogger(getClass());
    logger.info(
        "my argument is {}",
        fb -> {
          final Field field1 = fb.string("key1", "value1");
          final Field field2 = fb.string("key2", "value2");
          return fb.object("random_object", field1, field2);
        });

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("my argument is random_object={key1=value1, key2=value2}");

    final JsonNode fields = entry.path("fields");
    final JsonNode randomObject = fields.path("random_object");
    assertThat(randomObject.path("key1").asText()).isEqualTo("value1");
    assertThat(randomObject.path("key2").asText()).isEqualTo("value2");
  }

  @Test
  public void testLoggerWithSeveralObjectField() {
    Logger<?> logger = LoggerFactory.getLogger(getClass());
    logger.info(
        "my arguments are {} {}",
        fb ->
            fb.list(
                fb.object(
                    "object1", //
                    fb.string("key1", "value1"),
                    fb.string("key2", "value2")),
                fb.object(
                    "object2", //
                    fb.string("key3", "value3"),
                    fb.string("key4", "value4"))));
    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message)
        .isEqualTo(
            "my arguments are object1={key1=value1, key2=value2} object2={key3=value3, key4=value4}");

    final JsonNode fields = entry.path("fields");

    final JsonNode object1 = fields.path("object1");
    assertThat(object1.path("key1").asText()).isEqualTo("value1");
    assertThat(object1.path("key2").asText()).isEqualTo("value2");

    final JsonNode object2 = fields.path("object2");
    assertThat(object2.path("key3").asText()).isEqualTo("value3");
    assertThat(object2.path("key4").asText()).isEqualTo("value4");
  }

  @Test
  public void testLoggerWithArrayField() {
    Logger<?> logger = LoggerFactory.getLogger(getClass());
    logger.info(
        "my argument is {}",
        fb -> {
          Integer[] intArray = {1, 2, 3};
          return fb.array("random_key", intArray);
        });

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("my argument is random_key=[1, 2, 3]");

    final JsonNode fields = entry.path("fields");
    final JsonNode jsonArray = fields.path("random_key");
    assertThat(jsonArray.get(0).asInt()).isEqualTo(1);
    assertThat(jsonArray.get(1).asInt()).isEqualTo(2);
    assertThat(jsonArray.get(2).asInt()).isEqualTo(3);
  }

  @Test
  public void testLoggerWithThrowable() {
    Logger<?> logger = LoggerFactory.getLogger(getClass());
    Exception exception = new RuntimeException("Some exception");
    logger.error("Message", exception);

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("Message");

    final JsonNode ex = entry.get("thrown");
    assertThat(ex).isNotNull();
  }

  @Test
  public void testLoggerWithThrowableField() {
    Logger<?> logger = LoggerFactory.getLogger(getClass());
    Exception exception = new RuntimeException("Some exception");
    logger.error("Message {}", fb -> fb.exception(exception));

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("Message exception=java.lang.RuntimeException: Some exception");

    final JsonNode ex = entry.get("thrown");
    assertThat(ex).isNotNull();
  }

  @Test
  public void testLoggerWithContextField() {
    Logger<?> logger =
        LoggerFactory.getLogger(getClass())
            .withFields(fb -> fb.string("context_name", "context_field"));
    logger.error("Message");

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("Message");

    final JsonNode fields = entry.path("fields");
    assertThat(fields.path("context_name").asText()).isEqualTo("context_field");
  }

  @Test
  public void testLoggerWithContextAndArgumentField() {
    Logger<?> logger =
        LoggerFactory.getLogger(getClass())
            .withFields(fb -> fb.string("context_name", "context_field"));
    logger.error("Message {}", fb -> fb.string("arg_name", "arg_field"));

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("Message arg_name=arg_field");

    final JsonNode fields = entry.path("fields");
    assertThat(fields.path("arg_name").asText()).isEqualTo("arg_field");
    assertThat(fields.path("context_name").asText()).isEqualTo("context_field");
  }
}
