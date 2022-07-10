package com.tersesystems.echopraxia.difflogger;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.FieldBuilderResult;
import com.tersesystems.echopraxia.api.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

public class DiffLoggerTests {

  @Test
  public void testLogger() {
    Logger<PersonFieldBuilder> logger = LoggerFactory.getLogger().withFieldBuilder(new PersonFieldBuilder());

    Person before = new Person("Eloise", 1);
    Person after = before.withName("Will");

    logger.info("{}", fb -> fb.diffPerson("personDiff", before, after));

    ListAppender<ILoggingEvent> listAppender = getListAppender();
    List<ILoggingEvent> list = listAppender.list;
    ILoggingEvent event = list.get(0);
    assertThat(event.getFormattedMessage()).isEqualTo("personDiff=[{\"op\":\"replace\",\"path\":\"/name\",\"value\":\"Will\"}]");
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


class PersonFieldBuilder implements DiffFieldBuilder {

  // Renders a `Person` as an object field.
  public Field person(String fieldName, Person p) {
    return keyValue(fieldName, personValue(p));
  }

  public FieldBuilderResult diffPerson(String name, Person before, Person after) {
    return diff(name, personValue(before), personValue(after));
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

