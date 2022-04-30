package com.tersesystems.echopraxia.scripting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.api.Condition;
import com.tersesystems.echopraxia.api.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
    Logger<?> logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
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
    Logger<?> logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
    logger.info("data of interest found", fb -> fb.number("correlation_id", 123));

    ListAppender<ILoggingEvent> listAppender = getListAppender();
    List<ILoggingEvent> list = listAppender.list;
    ILoggingEvent event = list.get(0);
    assertThat(event.getMessage()).isEqualTo("data of interest found");
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
    Logger<?> logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
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
    Logger<?> logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
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
    Logger<?> logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
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
    Logger<?> logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
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
    Logger<?> logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
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

    Logger<?> logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
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

    Logger<?> logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
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

    Logger<?> logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
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

    Logger<?> logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
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
