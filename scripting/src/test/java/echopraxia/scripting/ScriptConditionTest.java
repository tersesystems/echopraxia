package echopraxia.scripting;

import static echopraxia.scripting.ScriptFunction.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.FunctionParameter;
import com.twineworks.tweakflow.lang.values.Values;
import echopraxia.api.Field;
import echopraxia.logger.LoggerFactory;
import echopraxia.logging.api.Condition;
import echopraxia.logging.api.LoggingContext;
import echopraxia.logstash.LogstashCoreLogger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ScriptConditionTest {

  @Test
  public void testFindStringCondition() {
    Condition condition =
        ScriptCondition.create(
            false,
            "library echopraxia {"
                + "  function evaluate: (string level, dict ctx) ->"
                + "    ctx[\"find_string\"](\"correlation_id\") == \"match\";"
                + "}",
            Throwable::printStackTrace);
    var logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
    logger.info("data of interest found", fb -> (fb.string("correlation_id", "match")));

    ListAppender<ILoggingEvent> listAppender = getListAppender();
    List<ILoggingEvent> list = listAppender.list;
    ILoggingEvent event = list.get(0);
    assertThat(event.getMessage()).isEqualTo("data of interest found");
  }

  @Test
  public void testFindNumberCondition() {
    Condition condition =
        ScriptCondition.create(
            false,
            "library echopraxia {"
                + "  function evaluate: (string level, dict ctx) ->"
                + "    ctx[\"find_number\"](\"correlation_id\") == 123;"
                + "}",
            Throwable::printStackTrace);
    var logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
    logger.info("data of interest found", fb -> fb.number("correlation_id", 123));

    ListAppender<ILoggingEvent> listAppender = getListAppender();
    List<ILoggingEvent> list = listAppender.list;
    ILoggingEvent event = list.get(0);
    assertThat(event.getMessage()).isEqualTo("data of interest found");
  }

  @Test
  public void testUserDefinedCondition() {
    List<ValueMapEntry> userFunctions =
        Collections.singletonList(
            ValueMapEntry.make(
                "now",
                builder()
                    .supplier(() -> Values.make(Instant.now()))
                    .result(Types.DATETIME)
                    .build()));
    Condition condition =
        ScriptCondition.create(
            ctx -> userFunctions,
            false,
            "import * as std from \"std\";\n"
                + "alias std.time as time;\n"
                + "library echopraxia {"
                + "  function evaluate: (string level, dict ctx) ->"
                + "    let { now: ctx[\"now\"]; }"
                + "    time.unix_timestamp(now()) > 0;"
                + "}",
            Throwable::printStackTrace);
    var logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
    logger.info("time is good");

    ListAppender<ILoggingEvent> listAppender = getListAppender();
    List<ILoggingEvent> list = listAppender.list;
    ILoggingEvent event = list.get(0);
    assertThat(event.getMessage()).isEqualTo("time is good");
  }

  @Test
  public void testLoggerPropertyCondition() {
    Function<LoggingContext, List<ValueMapEntry>> userFunctions =
        ctx ->
            Collections.singletonList(
                ValueMapEntry.make(
                    "logger_property",
                    builder()
                        .parameter(
                            new FunctionParameter(
                                0, "property_name", Types.STRING, Values.make("")))
                        .function(
                            propertyName -> {
                              LogstashCoreLogger core = (LogstashCoreLogger) ctx.getCore();
                              LoggerContext loggerContext = core.logger().getLoggerContext();
                              String propertyValue =
                                  loggerContext.getProperty(propertyName.string());
                              return Values.make(propertyValue);
                            })
                        .result(Types.STRING)
                        .build()));
    Condition condition =
        ScriptCondition.create(
            userFunctions,
            false,
            "library echopraxia {"
                + "  function evaluate: (string level, dict ctx) ->"
                + "    let { logger_property: ctx[\"logger_property\"]; }"
                + "    logger_property(\"herp\") == \"derp\";"
                + "}",
            Throwable::printStackTrace);
    var logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
    logger.info("the logger property is derp!");

    ListAppender<ILoggingEvent> listAppender = getListAppender();
    List<ILoggingEvent> list = listAppender.list;
    ILoggingEvent event = list.get(0);
    assertThat(event.getMessage()).isEqualTo("the logger property is derp!");
  }

  @Test
  public void testFindBooleanCondition() {
    Condition condition =
        ScriptCondition.create(
            false,
            "library echopraxia {"
                + "  function evaluate: (string level, dict ctx) ->"
                + "    ctx[\"find_boolean\"](\"bool_value\") == true;"
                + "}",
            Throwable::printStackTrace);
    var logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
    logger.info("data of interest found", fb -> fb.bool("bool_value", true));

    ListAppender<ILoggingEvent> listAppender = getListAppender();
    List<ILoggingEvent> list = listAppender.list;
    ILoggingEvent event = list.get(0);
    assertThat(event.getMessage()).isEqualTo("data of interest found");
  }

  @Test
  public void testFindNullCondition() {
    Condition condition =
        ScriptCondition.create(
            false,
            "library echopraxia {"
                + "  function evaluate: (string level, dict ctx) ->"
                + "    ctx[:find_null](\"null_value\") == true;"
                + "}",
            Throwable::printStackTrace);
    var logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
    logger.info("data of interest found", fb -> fb.nullField("null_value"));

    ListAppender<ILoggingEvent> listAppender = getListAppender();
    List<ILoggingEvent> list = listAppender.list;
    ILoggingEvent event = list.get(0);
    assertThat(event.getMessage()).isEqualTo("data of interest found");
  }

  @Test
  public void testFindObjectCondition() {
    Condition condition =
        ScriptCondition.create(
            false,
            "library echopraxia {"
                + "  function evaluate: (string level, dict ctx) ->"
                + "    let {"
                + "      find_object: ctx[:find_object];"
                + "      obj: find_object(\"$.obj\");"
                + "    }"
                + "    obj[:foo] == 1337;"
                + "}",
            Throwable::printStackTrace);
    var logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
    logger.info(
        "data of interest found",
        fb -> fb.object("obj", fb.number("foo", 1337), fb.number("bar", 0xDEADBEEF)));

    ListAppender<ILoggingEvent> listAppender = getListAppender();
    List<ILoggingEvent> list = listAppender.list;
    ILoggingEvent event = list.get(0);
    assertThat(event.getMessage()).isEqualTo("data of interest found");
  }

  @Test
  public void testFindListCondition() {
    Condition condition =
        ScriptCondition.create(
            false,
            "library echopraxia {"
                + "  function evaluate: (string level, dict ctx) ->"
                + "    let {"
                + "      find_list: ctx[\"find_list\"];"
                + "      interests: find_list(\"$.obj.interests\");"
                + "    }"
                + "    interests[1] == \"drink\";"
                + "}",
            Throwable::printStackTrace);
    var logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
    logger.info(
        "data of interest found",
        fb -> fb.object("obj", (fb.array("interests", "food", "drink", "sleep"))));

    ListAppender<ILoggingEvent> listAppender = getListAppender();
    List<ILoggingEvent> list = listAppender.list;
    ILoggingEvent event = list.get(0);
    assertThat(event.getMessage()).isEqualTo("data of interest found");
  }

  @Test
  public void testGetFieldsCondition() {
    Condition condition =
        ScriptCondition.create(
            false,
            "library echopraxia {"
                + "  function evaluate: (string level, dict ctx) ->"
                + "    let {"
                + "      fields: ctx[:fields]();"
                + "    }"
                + "    fields[:obj][:interests][2] == \"sleep\";"
                + "}",
            Throwable::printStackTrace);
    var logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
    logger.info(
        "data of interest found",
        fb -> fb.object("obj", (fb.array("interests", "food", "drink", "sleep"))));

    ListAppender<ILoggingEvent> listAppender = getListAppender();
    List<ILoggingEvent> list = listAppender.list;
    ILoggingEvent event = list.get(0);
    assertThat(event.getMessage()).isEqualTo("data of interest found");
  }

  @Test
  public void testExceptionFromFile() {
    Path path = Paths.get("src/test/tweakflow/exception.tf");
    Condition condition = ScriptCondition.create(false, path, Throwable::printStackTrace);

    var logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
    logger
        .withFields(fb -> (fb.exception(new RuntimeException("testing"))))
        .info("data of interest found");

    ListAppender<ILoggingEvent> listAppender = getListAppender();
    List<ILoggingEvent> list = listAppender.list;
    ILoggingEvent event = list.get(0);
    assertThat(event.getMessage()).isEqualTo("data of interest found");
  }

  @Test
  public void testConditionFromFile() {
    Path path = Paths.get("src/test/tweakflow/condition.tf");
    Condition condition = ScriptCondition.create(false, path, Throwable::printStackTrace);

    var logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
    logger.withFields(fb -> (fb.string("correlation_id", "match"))).info("data of interest found");

    ListAppender<ILoggingEvent> listAppender = getListAppender();
    List<ILoggingEvent> list = listAppender.list;
    ILoggingEvent event = list.get(0);
    assertThat(event.getMessage()).isEqualTo("data of interest found");
  }

  @Test
  public void testNoCondition() {
    Path path = Paths.get("src/test/tweakflow/condition.tf");
    Condition condition = ScriptCondition.create(false, path, Throwable::printStackTrace);

    var logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
    logger.info("this should not log");

    ListAppender<ILoggingEvent> listAppender = getListAppender();
    List<ILoggingEvent> list = listAppender.list;
    assertThat(list).isEmpty();
  }

  @Test
  public void testComplexCondition() {
    String path =
        "import * as std from \"std\";"
            + "alias std.strings as str;"
            + "library echopraxia {"
            + "  function evaluate: (string level, dict ctx) ->"
            + "    let {"
            + "      find_string: ctx[\"find_string\"];"
            + "    }"
            + "    str.lower_case(find_string(\"$.person.name\")) == \"will\";"
            + "}";

    Condition condition = ScriptCondition.create(false, path, Throwable::printStackTrace);

    var logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
    logger
        .withFields(
            fb -> {
              Field name = fb.string("name", "Will");
              Field age = fb.number("age", 13);
              Field toys = fb.array("toys", "binkie");
              Field person = fb.object("person", name, age, toys);
              return (person);
            })
        .info("data of interest found");

    ListAppender<ILoggingEvent> listAppender = getListAppender();
    List<ILoggingEvent> list = listAppender.list;
    ILoggingEvent event = list.get(0);
    assertThat(event.getMessage()).isEqualTo("data of interest found");
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
