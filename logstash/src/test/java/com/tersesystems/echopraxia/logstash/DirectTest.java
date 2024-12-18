package com.tersesystems.echopraxia.logstash;

import static net.logstash.logback.marker.Markers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.echopraxia.api.Condition;
import com.tersesystems.echopraxia.api.FieldBuilder;
import com.tersesystems.echopraxia.logback.ConditionMarker;
import com.tersesystems.echopraxia.logback.DirectFieldMarker;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.BasicMDCAdapter;

public class DirectTest {

  protected LoggerContext loggerContext;

  @Test
  void testMarker() {
    FieldBuilder fb = FieldBuilder.instance();
    org.slf4j.Logger logger = loggerContext().getLogger("com.example.Foo");
    logger.info(DirectFieldMarker.apply(fb.string("foo", "bar")), "message with marker");

    final List<ILoggingEvent> eventList = getListAppender().list;
    final List<String> jsonList = getStringAppender().list;

    final ILoggingEvent event = eventList.get(0);
    assertThat(event.getFormattedMessage()).isEqualTo("message with marker");
    final String json = jsonList.get(0);
    assertThat(json).contains("\"foo\" : \"bar\"");
  }

  @Test
  void testTwoMarkers() {
    FieldBuilder fb = FieldBuilder.instance();
    org.slf4j.Logger logger = loggerContext().getLogger("com.example.Foo");
    final DirectFieldMarker fields =
        DirectFieldMarker.apply(fb.list(fb.string("foo", "bar"), fb.number("someNumber", 1)));
    logger.info(fields, "message with marker");

    final List<ILoggingEvent> eventList = getListAppender().list;
    final List<String> jsonList = getStringAppender().list;

    final ILoggingEvent event = eventList.get(0);
    assertThat(event.getFormattedMessage()).isEqualTo("message with marker");
    final String json = jsonList.get(0);
    assertThat(json).contains("\"foo\" : \"bar\"");
    assertThat(json).contains("\"someNumber\" : 1");
  }

  @Test
  void testArgument() {
    FieldBuilder fb = FieldBuilder.instance();
    org.slf4j.Logger logger = loggerContext().getLogger("com.example.Foo");
    logger.info("message with argument {}", fb.string("foo", "bar"));

    final List<ILoggingEvent> eventList = getListAppender().list;
    final List<String> jsonList = getStringAppender().list;

    final ILoggingEvent event = eventList.get(0);
    assertThat(event.getFormattedMessage()).isEqualTo("message with argument foo=bar");
    final String json = jsonList.get(0);
    assertThat(json).contains("\"foo\" : \"bar\"");
  }

  @Test
  void testConditionDependingOnMarkerPass() {
    FieldBuilder fb = FieldBuilder.instance();
    final DirectFieldMarker fields =
        DirectFieldMarker.apply(fb.list(fb.string("extra", "value"), fb.number("someNumber", 1)));
    Condition condition = Condition.stringMatch("extra", s -> s.raw().equals("value"));

    org.slf4j.Logger logger = loggerContext().getLogger("com.example.Foo");
    logger.info(
        aggregate(fields, ConditionMarker.apply(condition)), "testConditionDependingOnMarkerPass");

    final List<ILoggingEvent> eventList = getListAppender().list;
    final List<String> jsonList = getStringAppender().list;

    final ILoggingEvent event = eventList.get(0);
    assertThat(event.getFormattedMessage()).isEqualTo("testConditionDependingOnMarkerPass");
    final String json = jsonList.get(0);
    assertThat(json).contains("\"someNumber\" : 1");
  }

  @Test
  void testConditionDependingOnMarkerFail() {
    FieldBuilder fb = FieldBuilder.instance();
    final DirectFieldMarker fields =
        DirectFieldMarker.apply(fb.list(fb.string("extra", "value"), fb.number("someNumber", 1)));
    Condition condition = Condition.stringMatch("extra", s -> !s.raw().equals("value"));

    org.slf4j.Logger logger = loggerContext().getLogger("com.example.Foo");
    logger.info(
        aggregate(fields, ConditionMarker.apply(condition)), "testConditionDependingOnMarkerFail");

    final List<ILoggingEvent> eventList = getListAppender().list;
    assertThat(eventList).isEmpty();
  }

  @Test
  void testConditionDependingOnArgumentPass() {
    FieldBuilder fb = FieldBuilder.instance();
    Condition condition = Condition.stringMatch("extra", s -> s.raw().equals("value"));

    org.slf4j.Logger logger = loggerContext().getLogger("com.example.Foo");
    logger.info(
        aggregate(ConditionMarker.apply(condition)),
        "testConditionDependingOnArgumentPass",
        fb.string("extra", "value"));

    final List<ILoggingEvent> eventList = getListAppender().list;
    final List<String> jsonList = getStringAppender().list;

    final ILoggingEvent event = eventList.get(0);
    assertThat(event.getFormattedMessage()).isEqualTo("testConditionDependingOnArgumentPass");
    final String json = jsonList.get(0);
    assertThat(json).contains("\"extra\" : \"value\"");
  }

  @Test
  void testConditionDependingOnArgumentFail() {
    FieldBuilder fb = FieldBuilder.instance();
    Condition condition = Condition.stringMatch("extra", s -> !s.raw().equals("value"));

    org.slf4j.Logger logger = loggerContext().getLogger("com.example.Foo");
    logger.info(
        aggregate(ConditionMarker.apply(condition)),
        "testConditionDependingOnArgumentFail",
        fb.number("someNumber", 1));

    final List<ILoggingEvent> eventList = getListAppender().list;
    assertThat(eventList).isEmpty();
  }

  @Test
  void testConditionDependingOnAndMarkerAndArgumentPass() {
    FieldBuilder fb = FieldBuilder.instance();
    DirectFieldMarker fields =
        DirectFieldMarker.apply(fb.list(fb.string("extra", "value"), fb.number("someNumber", 1)));

    Condition extraEqualsValue = Condition.stringMatch("extra", s -> s.raw().equals("value"));
    Condition fooEqualsBar = Condition.stringMatch("foo", s -> s.raw().equals("bar"));
    ConditionMarker condition = ConditionMarker.apply(extraEqualsValue.and(fooEqualsBar));

    org.slf4j.Logger logger = loggerContext().getLogger("com.example.Foo");
    logger.info(aggregate(fields, condition), "message with argument {}", fb.string("foo", "bar"));

    final List<ILoggingEvent> eventList = getListAppender().list;
    final List<String> jsonList = getStringAppender().list;

    final ILoggingEvent event = eventList.get(0);
    assertThat(event.getFormattedMessage()).isEqualTo("message with argument foo=bar");
    final String json = jsonList.get(0);
    assertThat(json).contains("\"foo\" : \"bar\"");
    assertThat(json).contains("\"someNumber\" : 1");
  }

  @Test
  void testConditionDependingOnAndMarkerAndArgumentFail() {
    FieldBuilder fb = FieldBuilder.instance();
    DirectFieldMarker fields =
        DirectFieldMarker.apply(fb.list(fb.string("extra", "value"), fb.number("someNumber", 1)));

    Condition extraEqualsValue = Condition.stringMatch("extra", s -> s.raw().equals("value"));
    Condition fooEqualsBar = Condition.stringMatch("foo", s -> s.raw().equals("bar"));
    ConditionMarker condition = ConditionMarker.apply(extraEqualsValue.xor(fooEqualsBar));

    org.slf4j.Logger logger = loggerContext().getLogger("com.example.Foo");
    logger.info(aggregate(fields, condition), "message with argument {}", fb.string("foo", "bar"));

    final List<ILoggingEvent> eventList = getListAppender().list;
    assertThat(eventList).isEmpty();
  }

  @BeforeEach
  public void before() {
    try {
      LoggerContext factory = new LoggerContext();
      factory.setMDCAdapter(new BasicMDCAdapter());

      // If we don't set MDC explicitly here then we get...
      //at java.lang.NullPointerException: Cannot invoke "org.slf4j.spi.MDCAdapter.getCopyOfContextMap()" because "mdcAdapter" is null
      //at 	at ch.qos.logback.classic.spi.LoggingEvent.getMDCPropertyMap(LoggingEvent.java:460)
      JoranConfigurator joran = new JoranConfigurator();
      joran.setContext(factory);
      factory.reset();
      joran.doConfigure(getClass().getResource("/logback-direct-test.xml").toURI().toURL());
      this.loggerContext = factory;
    } catch (JoranException | URISyntaxException | MalformedURLException je) {
      je.printStackTrace();
      fail(je);
    }
  }

  LoggerContext loggerContext() {
    return loggerContext;
  }

  ListAppender<ILoggingEvent> getListAppender() {
    final ch.qos.logback.classic.Logger logger = loggerContext().getLogger(ROOT_LOGGER_NAME);
    final Iterator<Appender<ILoggingEvent>> iterator = logger.iteratorForAppenders();
    for (Appender<ILoggingEvent> a; iterator.hasNext(); ) {
      a = iterator.next();
      if (a instanceof ListAppender) {
        return (ListAppender<ILoggingEvent>) a;
      }
    }
    throw new IllegalStateException("oh noes");
  }

  EncodingListAppender<ILoggingEvent> getStringAppender() {
    final ch.qos.logback.classic.Logger logger = loggerContext().getLogger(ROOT_LOGGER_NAME);
    final Iterator<Appender<ILoggingEvent>> iterator = logger.iteratorForAppenders();
    for (Appender<ILoggingEvent> a; iterator.hasNext(); ) {
      a = iterator.next();
      if (a instanceof LogstashFieldAppender) {
        LogstashFieldAppender ta = (LogstashFieldAppender) a;
        return (EncodingListAppender<ILoggingEvent>) ta.getAppender("STRINGLIST");
      }
    }
    throw new IllegalStateException("No string appender found!");
  }
}
