package com.tersesystems.echopraxia.log4j;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.api.*;
import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class AttributesTest extends TestBase {
  public static final AttributeKey<Boolean> IN_BED_ATTR_KEY = AttributeKey.create("inbed");
  public static final Attribute<Boolean> IN_BED_ATTR = IN_BED_ATTR_KEY.bindValue(true);

  public static final AttributeKey<Class<?>> CLASS_TYPE_ATTR = AttributeKey.create("class");

  Log4JCoreLogger getCoreLogger() {
    return new Log4JCoreLogger(
        Logger.FQCN, (ExtendedLogger) LogManager.getLogger(getClass().getName()));
  }

  @Test
  public void testSimpleField() {
    FieldTransformer fieldTransformer =
        new FieldTransformer() {
          @Override
          public @NotNull Field tranformArgumentField(@NotNull Field field) {
            Boolean inBed = field.attributes().getOptional(IN_BED_ATTR_KEY).orElse(false);
            boolean isString = field.value().type() == Value.Type.STRING;
            if (inBed && isString) {
              Value<String> inBedValue = Value.string(field.value().asString() + " IN BED");
              return Field.value(field.name(), inBedValue);
            }
            return field;
          }
        };
    CoreLogger coreLogger = getCoreLogger().withFieldTransformer(fieldTransformer);
    Logger<MyFieldBuilder> logger = LoggerFactory.getLogger(coreLogger, MyFieldBuilder.instance());

    logger.info("message {}", fb -> fb.string("foo", "bar").withAttribute(IN_BED_ATTR));

    JsonNode entry = getEntry();
    final String message = entry.path("message").asText();
    assertThat(message).isEqualTo("message bar IN BED");
  }

  static class MyFieldBuilder implements DefaultFieldBuilder {
    static MyFieldBuilder instance() {
      return new MyFieldBuilder() {};
    }

    public Field instant(Instant instant) {
      return value("instant", Value.string(instant.toString()))
          .withAttribute(CLASS_TYPE_ATTR.bindValue(instant.getClass()));
    }
  }

  public static final class UserID {
    private final String id;

    public UserID(String id) {
      this.id = id;
    }

    @Override
    public String toString() {
      return this.id;
    }
  }
}
