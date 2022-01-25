package com.tersesystems.echopraxia.logstash;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.junit.jupiter.api.Test;

class LoggerTest extends TestBase {

  private static final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

  @Test
  void testDebug() {
    Logger<?> logger = getLogger();
    logger.debug("hello");

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello");
  }

  @Test
  void testArguments() {
    Logger<?> logger = getLogger();
    logger.debug(
        "hello {}, you are {}, citizen status {}",
        fb -> fb.list(fb.string("name", "will"), fb.number("age", 13), fb.bool("citizen", true)));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello will, you are 13, citizen status true");
  }

  private Logger<?> getLogger() {
    LogstashCoreLogger logstashCoreLogger =
        new LogstashCoreLogger(factory.getLogger(getClass().getName()));
    return LoggerFactory.getLogger(logstashCoreLogger, Field.Builder.instance());
  }

  @Test
  void testArrayOfStringsArgument() {
    Logger<?> logger = getLogger();
    logger.debug("hello {}", fb -> fb.onlyArray("toys", "binkie"));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello toys=[binkie]");
  }

  @Test
  void testNullArgument() {
    Logger<?> logger = getLogger();
    logger.debug("hello {}", fb -> fb.only(fb.nullField("nothing")));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello null");
  }

  @Test
  void testObjectArgument() {
    Logger<?> logger = getLogger();
    logger.debug(
        "hello {}",
        fb -> {
          Field name = fb.string("name", "will");
          Field age = fb.number("age", 13);
          Field toys = fb.array("toys", "binkie", "dotty");
          Field person = fb.object("person", name, age, toys);
          return fb.list(person);
        });

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello person={will, 13, toys=[binkie, dotty]}");
  }

  @Test
  void testException() {
    Logger<?> logger = getLogger();
    logger.error("Error", new IllegalStateException("oh noes"));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("Error");
    final IThrowableProxy throwableProxy = event.getThrowableProxy();
    assertThat(throwableProxy).isNotNull();
  }

  @Test
  void testArgumentsException() {
    Logger<?> logger = getLogger();
    logger.error(
        "Error {}",
        fb ->
            Arrays.asList(
                fb.string("operation", "MyOperation"),
                fb.exception(new IllegalStateException("oh noes"))));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("Error MyOperation");
    final IThrowableProxy throwableProxy = event.getThrowableProxy();
    assertThat(throwableProxy).isNotNull();
  }

  interface UUIDFieldBuilder extends Field.Builder {
    default Field uuid(String name, UUID uuid) {
      return string(name, uuid.toString());
    }
  }

  @Test
  void testCustomMapping() {
    Logger<UUIDFieldBuilder> logger = getLogger().withFieldBuilder(new UUIDFieldBuilder() {});

    UUID uuid = UUID.randomUUID();
    logger.error("user id {}", fb -> fb.only(fb.uuid("user_id", uuid)));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String message = event.getMessage();
    assertThat(message).isEqualTo("user id {}");
    final Object[] args = event.getArgumentArray();
    final ObjectAppendingMarker actual = (ObjectAppendingMarker) args[0];
    assertThat(actual.getFieldValue()).isEqualTo(uuid.toString());
  }

  @Test
  public void testReallyComplexPerson() throws IOException {
    Person abe = new Person("Abe", 1, "yodelling");
    abe.setFather(new Person("Bert", 35, "keyboards"));
    abe.setMother(new Person("Candace", 30, "iceskating"));

    final Logger<MyFieldBuilder> logger = getLogger().withFieldBuilder(MyFieldBuilder.class);
    logger.info("hi there {}", fb -> fb.only(fb.person("person", abe)));
    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String message = event.getFormattedMessage();
    assertThat(message)
        .isEqualTo(
            "hi there person={Abe, 1,"
                + " father={Bert, 35, null, null, interests=[keyboards]},"
                + " mother={Candace, 30, null, null, interests=[iceskating]}, interests=[yodelling]}");

    final StringWriter sw = new StringWriter();
    final Object[] argumentArray = event.getArgumentArray();
    final StructuredArgument argument = (StructuredArgument) argumentArray[0];
    try (JsonGenerator generator = mapper.createGenerator(sw)) {
      generator.writeStartObject();
      argument.writeTo(generator);
      generator.writeEndObject();
    }
    assertThat(sw.toString())
        .isEqualTo(
            "{\"person\":{\"name\":\"Abe\",\"age\":1,"
                + "\"father\":{\"name\":\"Bert\",\"age\":35,\"father\":null,\"mother\":null,\"interests\":[\"keyboards\"]},"
                + "\"mother\":{\"name\":\"Candace\",\"age\":30,\"father\":null,\"mother\":null,\"interests\":[\"iceskating\"]},"
                + "\"interests\":[\"yodelling\"]}}");
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

  public static class MyFieldBuilder implements Field.Builder {

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
