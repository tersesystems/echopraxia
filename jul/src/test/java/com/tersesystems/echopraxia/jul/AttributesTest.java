package com.tersesystems.echopraxia.jul;

import static org.assertj.core.api.Assertions.assertThat;

import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.api.*;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AttributesTest extends TestBase {

  @BeforeEach
  public void before() throws IOException {
    super.before();
  }

  @Test
  void testValue() {
    Logger<?> logger = getLogger();
    logger.debug("hello {0}", fb -> fb.string("name", "world"));

    List<String> list = EncodedListHandler.lines();
    String line = list.get(0);
    assertThat(line).isEqualTo("hello name=world");
  }

  @Test
  void testMappedInstant() {
    Logger<MyFieldBuilder> logger = getLogger().withFieldBuilder(MyFieldBuilder.INSTANCE);
    logger.debug("hello {0}", fb -> fb.instant("name", Instant.ofEpochMilli(0)));

    List<String> list = EncodedListHandler.lines();
    String line = list.get(0);
    assertThat(line).isEqualTo("hello name=1970-01-01T00:00:00Z");
  }

  @Test
  void testInstant() {
    var converter = new InstantFieldTransformer();
    java.util.logging.Logger julLogger = java.util.logging.Logger.getLogger(getClass().getName());
    JULCoreLogger coreLogger =
        new JULCoreLogger(Logger.FQCN, julLogger).withFieldConverter(converter);
    Logger<MyFieldBuilder> logger = LoggerFactory.getLogger(coreLogger, MyFieldBuilder.INSTANCE);
    logger.debug("hello {0}", fb -> fb.instant("name", Instant.ofEpochMilli(0)));

    List<String> list = EncodedListHandler.lines();
    String line = list.get(0);
    assertThat(line).isEqualTo("hello name=1970-01-01T00:00:00Z");
  }

  static class MyFieldBuilder implements FieldBuilder {

    public Field instant(String name, Instant value) {
      return Field.keyValue(name, Value.string(value.toString()), DefaultField.class)
          .withAttribute(INSTANT_ATTR_KEY.bindValue(true));
    }

    public static MyFieldBuilder INSTANCE = new MyFieldBuilder();
  }

  static class InstantFieldTransformer implements FieldTransformer {
    @Override
    public @NotNull Field convertArgumentField(@NotNull Field field) {
      return field;
    }

    @Override
    public @NotNull Field convertLoggerField(@NotNull Field field) {
      Boolean isInstant = field.attributes().getOptional(INSTANT_ATTR_KEY).orElse(false);
      if (isInstant) {
        MyFieldBuilder fb = MyFieldBuilder.INSTANCE;
        Field typeField = fb.string("@type", "http://www.w3.org/2001/XMLSchema#dateTime");
        Field valueField = fb.keyValue("@value", field.value());

        Field mappedField = fb.object(field.name(), typeField, valueField);
        return new MappedField(field, mappedField);
      } else {
        return field;
      }
    }
  }

  private static final AttributeKey<Boolean> INSTANT_ATTR_KEY = AttributeKey.create("instant");
}
