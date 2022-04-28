package fluent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.echopraxia.FieldBuilder;
import com.tersesystems.echopraxia.fluent.FluentLogger;
import com.tersesystems.echopraxia.fluent.FluentLoggerFactory;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FluentLoggerTest {

  static class Person {
    final String name;
    final int age;

    public Person(String name, int age) {
      this.name = name;
      this.age = age;
    }
  }

  @Test
  public void testLogger() {
    FluentLogger<FieldBuilder> logger = FluentLoggerFactory.getLogger(getClass());

    Person person = new Person("Eloise", 1);

    logger
        .atInfo()
        .message("name = {}, age = {}")
        .argument(b -> b.string("name", person.name))
        .argument(b -> b.number("age", person.age))
        .log();

    ListAppender<ILoggingEvent> listAppender = getListAppender();
    List<ILoggingEvent> list = listAppender.list;
    ILoggingEvent event = list.get(0);
    assertThat(event.getFormattedMessage()).isEqualTo("name = Eloise, age = 1");
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
