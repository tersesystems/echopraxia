package com.tersesystems.echopraxia.logstash;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.api.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class AttributesTest extends TestBase {
  public static final AttributeKey<Boolean> IN_BED_ATTR_KEY = AttributeKey.create("inbed");
  public static final Attribute<Boolean> IN_BED_ATTR = IN_BED_ATTR_KEY.bindValue(true);

  public static final AttributeKey<Class<?>> CLASS_TYPE_ATTR = AttributeKey.create("class");

  @Test
  public void testSimpleField() {
    FieldConverter fieldConverter =
        new LogstashFieldConverter() {
          @Override
          public @NotNull Object convertField(@NotNull Field field) {
            Boolean inBed = field.attributes().getOptional(IN_BED_ATTR_KEY).orElse(false);
            boolean isString = field.value().type() == Value.Type.STRING;
            if (inBed && isString) {
              Value<String> inBedValue = Value.string(field.value().asString() + " IN BED");
              Field inBedField = Field.value(field.name(), inBedValue);
              return super.convertField(inBedField);
            }
            return super.convertField(field);
          }
        };
    CoreLogger coreLogger = getCoreLogger().withFieldConverter(fieldConverter);
    Logger<?> logger = LoggerFactory.getLogger(coreLogger, FieldBuilder.instance());

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

  @Test
  public void testInstantToObject() {
    FieldConverter fieldConverter =
        new LogstashFieldConverter() {
          @Override
          public @NotNull Object convertField(@NotNull Field field) {
            Optional<Class<?>> optClass = field.attributes().getOptional(CLASS_TYPE_ATTR);
            boolean isString = field.value().type() == Value.Type.STRING;
            if (optClass.isPresent()
                && isString
                && optClass.get().getName().equals("java.time.Instant")) {
              MyFieldBuilder fb = MyFieldBuilder.instance();
              Field typeField = fb.string("@type", "http://www.w3.org/2001/XMLSchema#dateTime");
              Field valueField = fb.keyValue("@value", field.value());

              Field objectField = fb.object(field.name(), typeField, valueField);
              return new MappedFieldMarker(field, objectField);
            }
            return super.convertField(field);
          }
        };
    CoreLogger coreLogger = getCoreLogger().withFieldConverter(fieldConverter);
    Logger<MyFieldBuilder> logger = LoggerFactory.getLogger(coreLogger, MyFieldBuilder.instance());
    logger.info("date shows {}", fb -> fb.instant(Instant.ofEpochMilli(0)));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isNotEmpty();

    final ILoggingEvent event = listAppender.list.get(0);
    String message = event.getFormattedMessage();
    assertThat(message).isEqualTo("date shows 1970-01-01T00:00:00Z");

    final EncodingListAppender<ILoggingEvent> stringAppender = getStringAppender();
    final String json = stringAppender.list.get(0);
    System.out.println(json);
    assertThat(json).contains("@type");
  }

  @Test
  public void testComplexObjectConversion() {
    FieldConverter fieldConverter =
      new LogstashFieldConverter() {
        @Override
        public @NotNull Object convertField(@NotNull Field field) {
          // https://www.baeldung.com/java-visitor-pattern
          FieldVisitor visitor = new FieldVisitor() {
            void visit() {
              if (field.attributes().containsKey(CHRONOUNIT)) {
                ChronoUnit chronoUnit = field.attributes().get(CHRONOUNIT);
                String name = field.name() + "_" + chronoUnit.toString().toLowerCase();
                MyFieldBuilder fb = MyFieldBuilder.instance();
                Field mappedField = fb.keyValue(name, field.value());
                return new MappedFieldMarker(field, mappedField);
              }
            }
          };
          return super.convertField(field);
        }
      };
    CoreLogger coreLogger = getCoreLogger().withFieldConverter(fieldConverter);
    Logger<MyFieldBuilder> logger = LoggerFactory.getLogger(coreLogger, MyFieldBuilder.instance());

    Person abe = new Person("Abe", 1, "yodelling");
    abe.setFather(new Person("Bert", 35, "keyboards"));
    abe.setMother(new Person("Candace", 30, "iceskating", "hockey", "macrame"));

    logger.info("this is {}", fb -> fb.person("person", abe));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    assertThat(listAppender.list).isNotEmpty();

    final ILoggingEvent event = listAppender.list.get(0);
    String message = event.getFormattedMessage();
    assertThat(message).isEqualTo("this is person=foo");
  }

  static class MyFieldBuilder implements FieldBuilder {
    static MyFieldBuilder instance() {
      return new MyFieldBuilder() {};
    }

    public Field instant(Instant instant) {
      return value("instant", Value.string(instant.toString()))
          .withAttribute(CLASS_TYPE_ATTR.bindValue(instant.getClass()));
    }

    // Renders a `Person` as an object field.
    // Note that properties must be broken down to the basic JSON types,
    // i.e. a primitive string/number/boolean/null or object/array.
    public Field person(String fieldName, Person p) {
      return object(fieldName, personValue(p));
    }

    private Value.ObjectValue personValue(Person p) {
      Field name = keyValue("name", Value.string(p.name()));
      Field age = keyValue("age", Value.number(p.age())).withAttribute(CHRONOUNIT.bindValue(ChronoUnit.YEARS));
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
