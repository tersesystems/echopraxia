package example;

import static com.tersesystems.echopraxia.Field.Value;
import static java.util.Collections.singletonList;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerFactory;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    Person abe = new Person("Abe", 1, "yodelling");
    abe.setFather(new Person("Bert", 35, "keyboards"));
    abe.setMother(new Person("Candace", 30, "iceskating"));

    // Show off a condition that only returns true if the date field is present in logger
    Condition dateCondition =
        (level, context) ->
            context.getFields().stream()
                .anyMatch(field -> field.name().equals("last_accessed_date"));

    // Render the person using the custom field builder as a StructuredArgument.
    if (logger.isInfoEnabled(dateCondition)) {
      logger.info("hi there {}", fb -> fb.only(fb.person("person", abe)));
    }

    // If you have conditions that are CPU expensive or may block on I/O network etc,
    // then you can delegate logging to another thread (MDC / ThreadContext is copied).
    final ExecutorService executor =
        Executors.newSingleThreadExecutor(
            r -> {
              Thread t = new Thread(r);
              t.setDaemon(true);
              t.setName("condition-executor");
              return t;
            });

    final Logger<MyFieldBuilder> asyncLogger = logger.withExecutor(executor);
    asyncLogger.asyncError(
        error ->
            error.log("This is a test {}", fb -> fb.onlyNumber("nanotime", System.nanoTime())));

    // You can also use a custom logger
    MyLogger myLogger = MyLoggerFactory.getLogger();
    myLogger.debug("Using my logger {}", fb -> fb.onlyDate("my date", new Date()));

    // Render some statements from context, always uses fb.string
    org.slf4j.MDC.put("mdckey", "mdcvalue");
    myLogger
        .withThreadContext()
        .withCondition(
            (l, ctx) -> ctx.getFields().stream().anyMatch(field -> field.name().equals("mdckey")))
        .info("This statement has MDC values in context");
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
    public Field person(String fieldName, Person p) {
      return keyValue(fieldName, personValue(p));
    }

    public Value.ObjectValue personValue(Person p) {
      // Note that properties must be broken down to the basic JSON types,
      // i.e. a primitive string/number/boolean/null or object/array.
      Field name = string("name", p.name());
      Field age = number("age", p.age());
      Field father = keyValue("father", Value.optional(p.getFather().map(this::personValue)));
      Field mother = keyValue("mother", Value.optional(p.getMother().map(this::personValue)));
      Field interests = array("interests", p.interests());
      return Value.object(name, age, father, mother, interests);
    }
  }

  static class MyLogger extends Logger<MyFieldBuilder> {
    protected MyLogger(CoreLogger core, MyFieldBuilder fieldBuilder) {
      super(core, fieldBuilder);
    }
  }

  static class MyLoggerFactory {
    public static MyLogger getLogger() {
      return new MyLogger(CoreLoggerFactory.getLogger(), myFieldBuilder);
    }
  }
}
