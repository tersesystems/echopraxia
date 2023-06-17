package com.tersesystems.echopraxia.jul;

import static org.assertj.core.api.Assertions.assertThat;

import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.FieldBuilder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class JULLoggerTest extends TestBase {

  @Test
  void testDebug() {
    var logger = getLogger();
    logger.debug("hello");

    List<LogRecord> list = EncodedListHandler.records();
    LogRecord logRecord = list.get(0);
    assertThat(logRecord.getLevel()).isEqualTo(Level.FINE);
    assertThat(logRecord.getMessage()).isEqualTo("hello");
  }

  @Test
  void testInfo() {
    var logger = getLogger();
    logger.info("hello");

    List<LogRecord> list = EncodedListHandler.records();
    LogRecord logRecord = list.get(0);
    assertThat(logRecord.getLevel()).isEqualTo(Level.INFO);
    assertThat(logRecord.getMessage()).isEqualTo("hello");
  }

  @Test
  void testArguments() {
    var logger = getLogger();
    logger.info(
        "hello {0}, you are {1}, citizen status {2}",
        fb -> fb.list(fb.string("name", "will"), fb.number("age", 13), fb.bool("citizen", true)));

    List<LogRecord> list = EncodedListHandler.records();
    LogRecord logRecord = list.get(0);
    assertThat(logRecord.getLevel()).isEqualTo(Level.INFO);
    assertThat(logRecord.getMessage()).isEqualTo("hello {0}, you are {1}, citizen status {2}");
    Object[] parameters = logRecord.getParameters();
    Field nameField = (Field) parameters[0];
    assertThat(nameField.name()).isEqualTo("name");
    assertThat(nameField.value().raw()).isEqualTo("will");
  }

  @Test
  void testWithThreadContext() {
    var logger = getLogger();
    MDC.put("mdckey", "mdcvalue");
    logger.withThreadContext().info("hello");

    List<LogRecord> list = EncodedListHandler.records();
    EchopraxiaLogRecord logRecord = (EchopraxiaLogRecord) list.get(0);
    Field[] fields = logRecord.getLoggerFields();
    Field nameField = fields[0];
    assertThat(nameField.name()).isEqualTo("mdckey");
    assertThat(nameField.value().raw()).isEqualTo("mdcvalue");
  }

  @Test
  void testException() {
    var logger = getLogger();
    Throwable expected = new IllegalStateException("oh noes");
    logger.error("Error", expected);

    List<LogRecord> list = EncodedListHandler.records();
    LogRecord logRecord = list.get(0);
    Throwable actual = logRecord.getThrown();
    assertThat(actual).hasSameClassAs(expected);
  }

  interface UUIDFieldBuilder extends FieldBuilder {
    default Field uuid(String name, UUID uuid) {
      return string(name, uuid.toString());
    }
  }

  // Example class with several fields on it.
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

  public static class MyFieldBuilder implements FieldBuilder {

    public MyFieldBuilder() {}

    // Renders a `Person` as an object field.
    // Note that properties must be broken down to the basic JSON types,
    // i.e. a primitive string/number/boolean/null or object/array.
    public Field person(String fieldName, Person p) {
      Field name = string("name", p.name());
      Field age = number("age", p.age());
      Field father = p.getFather().map(f -> person("father", f)).orElse(nullField("father"));
      Field mother = p.getMother().map(m -> person("mother", m)).orElse(nullField("mother"));
      Field interests = array("interests", p.interests());
      Field[] fields = {name, age, father, mother, interests};
      return object(fieldName, fields);
    }
  }
}
