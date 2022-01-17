package example;

import static java.util.Collections.singletonList;

import com.tersesystems.echopraxia.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class Main {

  // For basic logging, you can always use the `Logger<?>` syntax
  // which gives you a basic field builder.
  private static final Logger<?> basicLogger = LoggerFactory.getLogger();

  private Date lastAccessedDate = new Date();

  private static final MyFieldBuilder myFieldBuilder = new MyFieldBuilder();

  // More often you'll want to create a logger with your own domain objects and render those.
  // So we start with the basic logger...
  //   ...and add a custom field builder...
  //   ...then add a date to the logger's context.
  //
  // Here, `last_accessed_date` will be rendered with every log entry, but will
  // not show in the log message.
  //
  // Note that here we have a final logger because we have to access a field that
  // belongs to an object instance.
  private final Logger<MyFieldBuilder> logger =
      basicLogger
          .withFieldBuilder(myFieldBuilder)
          .withFields(fb -> fb.onlyDate("last_accessed_date", lastAccessedDate));

  public static void main(String[] args) {
    Main m = new Main();
    m.doStuff();
  }

  // Example method that will do logging.
  private void doStuff() {
    // Show the "before" date
    logger.info("This renders with the last access date");

    // touch the last accessed date to show context fields
    lastAccessedDate = new Date();

    // Show the "after" date
    logger.info("This renders the updated date");

    // Disable statements through condition
    logger.error(Condition.never(), "This will never render");

    // Create a complex business object
    Person eloise = new Person("Eloise", 1, "binkie");

    // Show off a condition that only returns true if the date field is present in logger
    Condition dateCondition =
        (level, context) ->
            context.getFields().stream()
                .anyMatch(field -> field.name().equals("last_accessed_date"));

    // Render the person using the custom field builder as a StructuredArgument.
    if (logger.isInfoEnabled(dateCondition)) {
      logger.info("hi there {}", fb -> singletonList(fb.person("small_mammal", eloise)));
    }
  }

  // Example class with several fields on it.
  static class Person {

    private final String name;
    private final int age;
    private final String[] toys;

    Person(String name, int age, String... toys) {
      this.name = name;
      this.age = age;
      this.toys = toys;
    }

    public String name() {
      return name;
    }

    public int age() {
      return age;
    }

    public String[] toys() {
      return toys;
    }
  }

  /**
   * A custom field builder. This is useful for structured logging because typically you want to
   * serialize custom objects to JSON in one place.
   */
  public static class MyFieldBuilder implements Field.Builder {

    public MyFieldBuilder() {}

    // Renders a date using the `only` idiom returning a list of `Field`.
    // This is a useful shortcut when you only have one field you want to add.
    public List<Field> onlyDate(String name, Date date) {
      return singletonList(date(name, date));
    }

    // Renders a date as an ISO 8601 string.
    public Field date(String name, Date date) {
      return string(
          name, DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(date.getTime())));
    }

    // Renders a `Person` as an object field.
    // Note that properties must be broken down to the basic JSON types,
    // i.e. a primitive string/number/boolean/null or object/array.
    public Field person(String name, Person person) {
      return object(
          name,
          string("name", person.name()),
          number("age", person.age()),
          array("toys", Field.Value.asList(person.toys(), Field.Value::string)));
    }
  }
}
