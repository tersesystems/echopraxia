package com.tersesystems.echopraxia.log4j;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import java.util.List;
import org.junit.jupiter.api.Test;

public class LoggerTest {

  @Test
  public void testLoggerWithStringField() {
    Logger<?> logger = LoggerFactory.getLogger(getClass());
    logger.info("my argument is {}", fb -> fb.onlyString("random_key", "random_value"));
    // XXX this should check that the argument is substituted appropriately
  }

  @Test
  public void testLoggerWithObjectField() {
    Logger<?> logger = LoggerFactory.getLogger(getClass());
    logger.info(
        "my argument is {}",
        fb -> {
          final Field field1 = fb.string("key1", "value1");
          final Field field2 = fb.string("key2", "value2");
          return fb.onlyObject("random_object", field1, field2);
        });
  }

  @Test
  public void testLoggerWithSeveralObjectField() {
    Logger<?> logger = LoggerFactory.getLogger(getClass());
    logger.info(
        "my argument is {}",
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
  }

  @Test
  public void testLoggerWithArrayField() {
    Logger<?> logger = LoggerFactory.getLogger(getClass());
    logger.info(
        "my argument is {}",
        fb -> {
          Number[] intArray = {1, 2, 3};
          final List<Field.Value<?>> values = Field.Value.asList(intArray, Field.Value::number);
          return fb.onlyArray("random_key", values);
        });
    // XXX this should check that the argument is substituted appropriately
  }
}
