package echopraxia.diff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import echopraxia.api.DefaultField;
import echopraxia.api.Field;
import echopraxia.api.FieldBuilder;
import echopraxia.api.Value;
import echopraxia.logger.Logger;
import echopraxia.logger.LoggerFactory;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DiffFieldBuilderTests {

  @Test
  public void testLogger() {
    Logger<PersonFieldBuilder> logger = LoggerFactory.getLogger(PersonFieldBuilder.instance);

    Person before = new Person("Eloise", 1);
    Person after = before.withName("Will");

    logger.info("{}", fb -> fb.diff("personDiff", before, after));

    ListAppender<ILoggingEvent> listAppender = getListAppender();
    List<ILoggingEvent> list = listAppender.list;
    assertThat(list).isNotEmpty();

    ILoggingEvent event = list.get(0);
    assertThat(event.getFormattedMessage())
        .isEqualTo("personDiff=[{op=replace, path=/name, value=Will}]");
  }

  @BeforeEach
  public void beforeEach() {
    getListAppender().list.clear();
  }

  LoggerContext loggerContext() {
    return (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
  }

  ListAppender<ILoggingEvent> getListAppender() {
    return (ListAppender<ILoggingEvent>)
        loggerContext().getLogger(ROOT_LOGGER_NAME).getAppender("LIST");
  }
}

class Person {
  private final String name;
  private final int age;

  public Person(String name, int age) {
    this.name = name;
    this.age = age;
  }

  public Person withName(String name) {
    return new Person(name, age);
  }

  public Person withAge(int age) {
    return new Person(name, age);
  }

  public Integer getAge() {
    return age;
  }

  public String getName() {
    return name;
  }
}

class PersonFieldBuilder implements DiffFieldBuilder, FieldBuilder {

  private PersonFieldBuilder() {}

  public static final PersonFieldBuilder instance = new PersonFieldBuilder();

  public Field diff(String name, Person before, Person after) {
    return diff(name, personValue(before), personValue(after), DefaultField.class);
  }

  public Value<?> personValue(Person p) {
    if (p == null) {
      return Value.nullValue();
    }
    Field name = string("name", p.getName());
    Field age = number("age", p.getAge());
    return Value.object(name, age);
  }
}
