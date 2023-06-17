package com.tersesystems.echopraxia.logstash;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.tersesystems.echopraxia.*;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.FieldBuilder;
import com.tersesystems.echopraxia.api.Value;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;
import java.util.UUID;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.junit.jupiter.api.Test;

class LogstashLoggerTest extends TestBase {

  private static final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

  @Test
  void testDebug() {
    var logger = getLogger();
    logger.debug("hello");

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello");
  }

  @Test
  void testNullMessage() {
    var logger = getLogger();
    logger.debug(null);

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo(null);
  }

  @Test
  public void testLoggerLocation() {
    var logger = getLogger();
    logger.info("Boring Message");

    // We can't go through the list appender here because it doesn't call the encoder,
    // and CallerData expects a stack that has the encoder called from a stack containing
    // the logger.info method (so we can't call the encoder directly from here).
    final EncodingListAppender<ILoggingEvent> stringAppender = getStringAppender();
    final String json = stringAppender.list.get(0);
    assertThat(json).contains("testLoggerLocation"); // caller_method_name
  }

  @Test
  public void testAsyncLoggerLocation() {
    final EncodingListAppender<ILoggingEvent> stringAppender = getStringAppender();
    var asyncLogger = getAsyncLogger();
    asyncLogger.info("Boring Message");

    waitUntilMessages();

    // The async logger's even more work, because we need to set a filter up so we
    // can splice in the correct caller information before the encoder can call
    // event.getCallerData().
    final String json = stringAppender.list.get(0);
    assertThat(json).contains("testAsyncLoggerLocation"); // caller_method_name
  }

  @Test
  void testArguments() {
    var logger = getLogger();
    logger.debug(
        "hello {}, you are {}, {}",
        fb -> fb.list(fb.string("name", "PERSON"), fb.number("age", 13), fb.bool("citizen", true)));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello name=PERSON, you are age=13, citizen=true");
  }

  @Test
  void testArrayOfStringsArgument() {
    var logger = getLogger();
    logger.debug("hello {}", fb -> fb.array("toys", "binkie"));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello toys=[binkie]");
  }

  @Test
  void testNullArgument() {
    var logger = getLogger();
    logger.debug("hello {}", fb -> fb.nullField("nothing"));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello nothing=null");
  }

  @Test
  void testNullStringArgument() {
    var logger = getLogger();
    String value = null;
    logger.debug("hello {}", fb -> (fb.string("name", value)));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello name=null");
  }

  @Test
  void testNullFieldName() {
    var logger = getLogger();
    String value = "value";
    logger.debug("hello {}", fb -> (fb.string(null, value)));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello echopraxia-unknown-1=value");
  }

  @Test
  void testNullNumber() {
    var logger = getLogger();
    Integer value = null;
    logger.debug("hello {}", fb -> (fb.number("name", value)));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("hello name=0");
  }

  @Test
  void testNullBoolean() {
    var logger = getLogger();
    logger.debug("boolean is {}", fb -> (fb.bool("name", (Boolean) null)));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String message = event.getFormattedMessage();
    assertThat(message).isEqualTo("boolean is name=false");
  }

  @Test
  void testNullArrayElement() {
    var logger = getLogger();
    String[] values = {"1", null, "3"};
    logger.debug("array field is {}", fb -> fb.array("arrayName", values));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String message = event.getFormattedMessage();
    assertThat(message).isEqualTo("array field is arrayName=[1, null, 3]");
  }

  @Test
  void testNullObject() {
    var logger = getLogger();
    logger.debug("object is {}", fb -> (fb.object("name", Value.object((Field) null))));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String message = event.getFormattedMessage();
    assertThat(message)
        .isEqualTo("object is name={}"); // {} here is literally an object with no fields
  }

  @Test
  void testObjectArgument() {
    var logger = getLogger();
    logger.debug(
        "hello {}",
        fb -> {
          Field name = fb.string("name", "will");
          Field age = fb.number("age", 13);
          Field toys = fb.array("toys", "binkie", "dotty");
          return fb.object("person", name, age, toys);
        });

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage)
        .isEqualTo("hello person={name=will, age=13, toys=[binkie, dotty]}");
  }

  @Test
  void testException() {
    var logger = getLogger();
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
    var logger = getLogger();
    logger.error(
        "Error {}",
        fb ->
            fb.list(
                fb.string("operation", "MyOperation"),
                fb.exception(new IllegalStateException("oh noes"))));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String formattedMessage = event.getFormattedMessage();
    assertThat(formattedMessage).isEqualTo("Error operation=MyOperation");
    final IThrowableProxy throwableProxy = event.getThrowableProxy();
    assertThat(throwableProxy).isNotNull();
  }

  interface UUIDFieldBuilder extends FieldBuilder {
    default Field uuid(String name, UUID uuid) {
      return string(name, uuid.toString());
    }
  }

  @Test
  void testCustomMapping() {
    Logger<UUIDFieldBuilder> logger = getLogger().withFieldBuilder(new UUIDFieldBuilder() {});

    UUID uuid = UUID.randomUUID();
    logger.error("{}", fb -> fb.uuid("user_id", uuid));

    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String message = event.getMessage();
    assertThat(message).isEqualTo("{}");
    final Object[] args = event.getArgumentArray();
    final ObjectAppendingMarker actual = (ObjectAppendingMarker) args[0];
    assertThat(actual.toStringSelf()).isEqualTo("user_id=" + uuid);
  }

  @Test
  public void testReallyComplexPerson() throws IOException {
    Person abe = new Person("Abe", 1, "yodelling");
    abe.setFather(new Person("Bert", 35, "keyboards"));
    abe.setMother(new Person("Candace", 30, "iceskating"));

    final Logger<MyFieldBuilder> logger = getLogger().withFieldBuilder(new MyFieldBuilder());
    logger.info("hi there {}", fb -> (fb.person("person", abe)));
    final ListAppender<ILoggingEvent> listAppender = getListAppender();
    final ILoggingEvent event = listAppender.list.get(0);
    final String message = event.getFormattedMessage();
    assertThat(message)
        .isEqualTo(
            "hi there person={name=Abe, age=1,"
                + " father={name=Bert, age=35, father=null, mother=null, interests=[keyboards]},"
                + " mother={name=Candace, age=30, father=null, mother=null, interests=[iceskating]}, interests=[yodelling]}");

    String json = toJson(event);
    assertThat(json)
        .isEqualTo(
            "{\"person\":{\"name\":\"Abe\",\"age\":1,"
                + "\"father\":{\"name\":\"Bert\",\"age\":35,\"father\":null,\"mother\":null,\"interests\":[\"keyboards\"]},"
                + "\"mother\":{\"name\":\"Candace\",\"age\":30,\"father\":null,\"mother\":null,\"interests\":[\"iceskating\"]},"
                + "\"interests\":[\"yodelling\"]}}");
  }

  private String toJson(ILoggingEvent event) throws IOException {
    final StringWriter sw = new StringWriter();
    final Object[] argumentArray = event.getArgumentArray();
    final StructuredArgument argument = (StructuredArgument) argumentArray[0];
    try (JsonGenerator generator = mapper.createGenerator(sw)) {
      generator.writeStartObject();
      argument.writeTo(generator);
      generator.writeEndObject();
    }
    return sw.toString();
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

    public Field person(String fieldName, Person p) {
      return keyValue(fieldName, personValue(p));
    }

    private Value<?> personValue(Person p) {
      if (p == null) {
        return Value.nullValue();
      }
      Field name = string("name", p.name());
      Field age = number("age", p.age());
      Field father = keyValue("father", Value.optional(p.getFather().map(this::personValue)));
      Field mother = keyValue("mother", Value.optional(p.getMother().map(this::personValue)));
      Field interests = array("interests", p.interests());
      return Value.object(name, age, father, mother, interests);
    }
  }
}
