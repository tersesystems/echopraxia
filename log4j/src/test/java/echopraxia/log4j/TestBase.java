package echopraxia.log4j;

import static echopraxia.log4j.appender.ListAppender.getListAppender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import echopraxia.api.FieldBuilder;
import echopraxia.log4j.appender.ListAppender;
import echopraxia.logger.Logger;
import echopraxia.logger.LoggerFactory;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;

public class TestBase {

  @BeforeEach
  void beforeEach() {
    StaticExceptionHandler.clear();
    final ListAppender listAppender = getListAppender("ListAppender");
    listAppender.clear();
  }

  @NotNull
  Logger<FieldBuilder> getLogger() {
    return LoggerFactory.getLogger();
  }

  void waitUntilMessages() {
    final ListAppender listAppender = getListAppender("ListAppender");
    org.awaitility.Awaitility.await().until(() -> !listAppender.getMessages().isEmpty());
  }

  JsonNode getEntry() {
    final ListAppender listAppender = getListAppender("ListAppender");
    final List<String> messages = listAppender.getMessages();

    final String jsonLine = messages.get(0);
    try {
      return mapper.readTree(jsonLine);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
}
