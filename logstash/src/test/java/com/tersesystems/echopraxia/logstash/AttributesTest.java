package com.tersesystems.echopraxia.logstash;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.api.*;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class AttributesTest extends TestBase {
  public static final AttributeKey<Boolean> IN_BED_ATTR_KEY = AttributeKey.create("inbed");
  public static final Attribute<Boolean> IN_BED_ATTR = IN_BED_ATTR_KEY.bindValue(true);

  @Test
  public void testSimpleField() {
    FieldTransformer fieldTransformer =
        new FieldTransformer() {
          public @NotNull Field convertField(@NotNull Field field) {
            Boolean inBed = field.attributes().getOptional(IN_BED_ATTR_KEY).orElse(false);
            boolean isString = field.value().type() == Value.Type.STRING;
            if (inBed && isString) {
              Value<String> inBedValue = Value.string(field.value().asString() + " IN BED");
              return Field.value(field.name(), inBedValue);
            }
            return field;
          }

          @Override
          public @NotNull Field tranformArgumentField(@NotNull Field field) {
            return convertField(field);
          }

          @Override
          public @NotNull Field transformLoggerField(@NotNull Field field) {
            return convertField(field);
          }
        };
    CoreLogger coreLogger = getCoreLogger().withFieldTransformer(fieldTransformer);
    Logger<DefaultFieldBuilder> logger = LoggerFactory.getLogger(coreLogger);

    String cookieSaying = "you will have a long and illustrious career";
    logger.info(
        "fortune cookie says {}",
        fb -> fb.string("cookieSaying", cookieSaying).withAttribute(IN_BED_ATTR));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isNotEmpty();

    final ILoggingEvent event = listAppender.list.get(0);
    String message = event.getFormattedMessage();
    assertThat(message)
        .isEqualTo("fortune cookie says you will have a long and illustrious career IN BED");
  }

  static class MyFieldBuilder implements DefaultFieldBuilder {
    static MyFieldBuilder instance() {
      return new MyFieldBuilder() {};
    }

    // Renders a `Person` as an object field.
    // Note that properties must be broken down to the basic JSON types,
    // i.e. a primitive string/number/boolean/null or object/array.
    public Field person(String fieldName, Person p) {
      return object(fieldName, personValue(p));
    }

    private Value.ObjectValue personValue(Person p) {
      Field name = keyValue("name", Value.string(p.name()));
      Field age =
          keyValue("age", Value.number(p.age()))
              .withAttribute(CHRONOUNIT.bindValue(ChronoUnit.YEARS));
      Field father = keyValue("father", Value.optional(p.getFather().map(this::personValue)));
      Field mother = keyValue("mother", Value.optional(p.getMother().map(this::personValue)));
      Field interests = array("interests", p.interests());
      return Value.object(name, age, father, mother, interests);
    }
  }

  private static final AttributeKey<ChronoUnit> CHRONOUNIT = AttributeKey.create("chronoUnit");

  static class Person {

    private final String name;
    private final int age;
    private final String[] interests;

    private Person father;
    private Person mother;

    Person(String name, int age, String... interests) {
      this.name = name;
      this.age = age;
      this.interests = interests;
    }

    public String name() {
      return name;
    }

    public int age() {
      return age;
    }

    public String[] interests() {
      return interests;
    }

    public void setFather(Person father) {
      this.father = father;
    }

    public Optional<Person> getFather() {
      return Optional.ofNullable(father);
    }

    public void setMother(Person mother) {
      this.mother = mother;
    }

    public Optional<Person> getMother() {
      return Optional.ofNullable(mother);
    }
  }
}
