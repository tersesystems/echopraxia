package com.tersesystems.echopraxia.jul;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

public class JSONFormatterTest extends TestBase {

  @Test
  void testDebug() throws JsonProcessingException {
    var logger = getLogger();
    logger.debug("hello");

    List<String> list = EncodedListHandler.ndjson();
    String logRecord = list.get(0);

    final ObjectMapper mapper = new ObjectMapper();
    final JsonNode jsonNode = mapper.readTree(logRecord);

    assertThat(jsonNode.get("level").asText()).isEqualTo("DEBUG");
    assertThat(jsonNode.get("message").asText()).isEqualTo("hello");
  }

  @Test
  void testInfo() throws JsonProcessingException {
    var logger = getLogger();
    logger.info("hello");

    List<String> list = EncodedListHandler.ndjson();
    String logRecord = list.get(0);

    final ObjectMapper mapper = new ObjectMapper();
    final JsonNode jsonNode = mapper.readTree(logRecord);

    assertThat(jsonNode.get("level").asText()).isEqualTo("INFO");
    assertThat(jsonNode.get("message").asText()).isEqualTo("hello");
  }

  @Test
  void testArguments() throws JsonProcessingException {
    var logger = getLogger();
    logger.info(
        "hello {0}, you are {1}, citizen status {2}",
        fb -> fb.list(fb.string("name", "will"), fb.number("age", 13), fb.bool("citizen", true)));

    List<String> list = EncodedListHandler.ndjson();
    String logRecord = list.get(0);

    final ObjectMapper mapper = new ObjectMapper();
    final JsonNode jsonNode = mapper.readTree(logRecord);

    assertThat(jsonNode.get("name").asText()).isEqualTo("will");
    assertThat(jsonNode.get("age").asInt()).isEqualTo(13);
    assertThat(jsonNode.get("citizen").asBoolean()).isEqualTo(true);
  }

  @Test
  void testException() throws JsonProcessingException {
    var logger = getLogger();
    Throwable expected = new IllegalStateException("oh noes");
    logger.error("Error", expected);

    List<String> list = EncodedListHandler.ndjson();
    String logRecord = list.get(0);

    final ObjectMapper mapper = new ObjectMapper();
    final JsonNode jsonNode = mapper.readTree(logRecord);

    assertThat(jsonNode.get("exception").asText())
        .isEqualTo("java.lang.IllegalStateException: oh noes");
  }
}
