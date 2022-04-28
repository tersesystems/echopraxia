package com.tersesystems.echopraxia.log4j;

import static com.tersesystems.echopraxia.log4j.appender.ListAppender.getListAppender;

import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.async.AsyncLogger;
import com.tersesystems.echopraxia.async.AsyncLoggerFactory;
import com.tersesystems.echopraxia.log4j.appender.ListAppender;
import java.io.StringReader;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.junit.jupiter.api.BeforeEach;

public class TestBase {

  @BeforeEach
  void beforeEach() {
    final ListAppender listAppender = getListAppender("ListAppender");
    listAppender.clear();
  }

  Logger<?> getLogger() {
    return LoggerFactory.getLogger();
  }

  AsyncLogger<?> getAsyncLogger() {
    return AsyncLoggerFactory.getLogger();
  }

  void waitUntilMessages() {
    final ListAppender listAppender = getListAppender("ListAppender");
    org.awaitility.Awaitility.await().until(() -> !listAppender.getMessages().isEmpty());
  }

  JsonObject getEntry() {
    final ListAppender listAppender = getListAppender("ListAppender");
    final List<String> messages = listAppender.getMessages();

    final String jsonLine = messages.get(0);
    JsonReader reader = Json.createReader(new StringReader(jsonLine));
    return reader.readObject();
  }
}
