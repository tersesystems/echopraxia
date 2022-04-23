package com.tersesystems.echopraxia;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;
import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.tersesystems.echopraxia.fake.FakeLoggingContext;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class JsonPathTests {

  final JsonProvider jsonProvider = new EchopraxiaJsonProvider();
  final MappingProvider mappingProvider = new EchopraxiaMappingProvider();

  Configuration configuration() {
    return Configuration.builder()
        .jsonProvider(jsonProvider)
        .mappingProvider(mappingProvider)
        .options(Option.DEFAULT_PATH_LEAF_TO_NULL)
        .options(Option.SUPPRESS_EXCEPTIONS)
        .build();
  }

  @Test
  public void testRead() {
    PersonBuilder builder = new PersonBuilder();

    Person abe = new Person("Abe", 1, "yodelling");
    abe.setFather(new Person("Bert", 35, "keyboards"));
    abe.setMother(new Person("Candace", 30, "iceskating"));

    LoggingContext context = FakeLoggingContext.single(builder.person("person", abe));
    final DocumentContext documentContext = JsonPath.parse(context, configuration());
    final List interests = documentContext.read("$.person.father.interests", List.class);
    final String s = (String) interests.get(0);
    assertThat(s).isEqualTo("keyboards");
  }

  @Test
  public void testIndexSelect() {
    PersonBuilder builder = new PersonBuilder();

    Person abe = new Person("Abe", 1, "yodelling");
    abe.setFather(new Person("Bert", 35, "keyboards"));
    abe.setMother(new Person("Candace", 30, "iceskating", "hockey", "macrame"));

    LoggingContext context = FakeLoggingContext.single(builder.person("person", abe));
    final DocumentContext documentContext = JsonPath.parse(context, configuration());
    String interest = documentContext.read("$.person.mother.interests[1]", String.class);
    assertThat(interest).isEqualTo("hockey");
  }

  @Test
  public void testIndexRange() {
    PersonBuilder builder = new PersonBuilder();

    Person abe = new Person("Abe", 1, "yodelling");
    abe.setFather(new Person("Bert", 35, "keyboards"));
    abe.setMother(new Person("Candace", 30, "iceskating", "hockey", "macrame"));

    LoggingContext context = FakeLoggingContext.single(builder.person("person", abe));
    final DocumentContext documentContext = JsonPath.parse(context, configuration());
    List interests = documentContext.read("$.person.mother.interests[1,2]", List.class);
    assertThat(interests.get(0)).isEqualTo("hockey");
    assertThat(interests.get(1)).isEqualTo("macrame");
  }

  @Test
  public void testDeepScan() {
    PersonBuilder builder = new PersonBuilder();

    Person abe = new Person("Abe", 1, "yodelling");
    abe.setFather(new Person("Bert", 35, "keyboards"));
    abe.setMother(new Person("Candace", 30, "iceskating", "hockey", "macrame"));

    LoggingContext context = FakeLoggingContext.single(builder.person("person", abe));
    final DocumentContext documentContext = JsonPath.parse(context, configuration());
    List mother = documentContext.read("$..mother", List.class);
    assertThat(mother).size().isEqualTo(4);
  }

  @Test
  public void testInlinePredicate() {
    PersonBuilder builder = new PersonBuilder();

    Person abe = new Person("Abe", 1, "yodelling");
    abe.setFather(new Person("Bert", 35, "keyboards"));
    abe.setMother(new Person("Candace", 30, "iceskating", "hockey", "macrame"));
    LoggingContext context = FakeLoggingContext.single(builder.person("person", abe));
    final DocumentContext documentContext = JsonPath.parse(context, configuration());
    List<Field.Value<?>> results = documentContext.read("$.person.mother[?(@.age <= 30)]");
    assertThat(results.get(0)).isExactlyInstanceOf(Field.Value.ObjectValue.class);
  }

  @Test
  public void testInlinePredicateNoMatch() {
    // https://github.com/json-path/JsonPath#inline-predicates
    PersonBuilder builder = new PersonBuilder();

    Person abe = new Person("Abe", 1, "yodelling");
    abe.setFather(new Person("Bert", 35, "keyboards"));
    abe.setMother(new Person("Candace", 30, "iceskating", "hockey", "macrame"));
    LoggingContext context = FakeLoggingContext.single(builder.person("person", abe));
    final DocumentContext documentContext = JsonPath.parse(context, configuration());
    List<Field.Value<?>> results = documentContext.read("$.person.father[?(@.age <= 30)]");
    assertThat(results).isEmpty();
  }

  @Test
  public void testFilterPredicate() {
    // https://github.com/json-path/JsonPath#filter-predicates

    PersonBuilder builder = new PersonBuilder();
    Person abe = new Person("Abe", 1, "yodelling");
    abe.setFather(new Person("Bert", 35, "keyboards"));
    abe.setMother(new Person("Candace", 30, "iceskating", "hockey", "macrame"));
    LoggingContext context = FakeLoggingContext.single(builder.person("person", abe));

    Filter iceskatingFilter = filter(where("interests").contains("iceskating"));
    final DocumentContext documentContext = JsonPath.parse(context, configuration());
    List<Field.Value<?>> results = documentContext.read("$.person.mother[?]", iceskatingFilter);
    assertThat(results).isNotEmpty();
  }

  @Test
  public void testExceptionMessage() {
    final FieldBuilder fb = FieldBuilder.instance();
    LoggingContext context =
        FakeLoggingContext.single(fb.exception(new RuntimeException("some message")));

    final DocumentContext documentContext = JsonPath.parse(context, configuration());
    String message = documentContext.read("$.exception.message", String.class);
    assertThat(message).isEqualTo("some message");
  }

  @Test
  public void testExceptionCauseMessage() {
    final FieldBuilder fb = FieldBuilder.instance();
    final RuntimeException cause = new RuntimeException("some other message");
    LoggingContext context =
        FakeLoggingContext.single(fb.exception(new RuntimeException("some message", cause)));

    final DocumentContext documentContext = JsonPath.parse(context, configuration());
    String message = documentContext.read("$.exception.cause.message", String.class);
    assertThat(message).isEqualTo("some other message");
  }

  @Test
  public void testExceptionStackTrace() {
    final FieldBuilder fb = FieldBuilder.instance();
    LoggingContext context =
        FakeLoggingContext.single(fb.exception(new RuntimeException("some message")));

    final DocumentContext documentContext = JsonPath.parse(context, configuration());
    String methodName = documentContext.read("$.exception.stackTrace[0].methodName", String.class);
    assertThat(methodName).isEqualTo("testExceptionStackTrace");
  }

  @Test
  public void testExceptionStackTraceMissing() {
    LoggingContext context = FakeLoggingContext.empty();

    final DocumentContext documentContext = JsonPath.parse(context, configuration());
    String methodName = documentContext.read("$.exception.stackTrace[0].methodName", String.class);
    assertThat(methodName).isNull();
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

  public static class PersonBuilder implements FieldBuilder {

    public PersonBuilder() {}

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
