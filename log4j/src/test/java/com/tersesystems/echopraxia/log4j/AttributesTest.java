package com.tersesystems.echopraxia.log4j;

import static org.assertj.core.api.Assertions.assertThat;

import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.api.*;
import java.time.Instant;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class AttributesTest extends TestBase {
    public static final AttributeKey<Boolean> IN_BED_ATTR_KEY = AttributeKey.create("inbed");
    public static final Attribute<Boolean> IN_BED_ATTR = IN_BED_ATTR_KEY.bindValue(true);

    public static final AttributeKey<Class<?>> CLASS_TYPE_ATTR = AttributeKey.create("class");

    @Test
    public void testSimpleField() {
        //CoreLogger coreLogger = getCoreLogger().withFieldConverter(fieldConverter);
        //Logger<?> logger = LoggerFactory.getLogger(coreLogger, FieldBuilder.instance());
        //
        //String cookieSaying = "you will have a long and illustrious career";
        //logger.info(
        //    "fortune cookie says {}",
        //    fb -> fb.string("cookieSaying", cookieSaying).withAttribute(IN_BED_ATTR));
        //
        //final ListAppender<ILoggingEvent> listAppender = getListAppender();
        //assertThat(listAppender.list).isNotEmpty();
        //
        //final ILoggingEvent event = listAppender.list.get(0);
        //String message = event.getFormattedMessage();
        //assertThat(message)
        //    .isEqualTo("fortune cookie says you will have a long and illustrious career IN BED");
    }

    @Test
    public void testInstantToObject() {
        //CoreLogger coreLogger = getCoreLogger().withFieldConverter(fieldConverter);
        //Logger<MyFieldBuilder> logger = LoggerFactory.getLogger(coreLogger, MyFieldBuilder.instance());
        //logger.info("date shows {}", fb -> fb.instant(Instant.ofEpochMilli(0)));
        //
        //final ListAppender<ILoggingEvent> listAppender = getListAppender();
        //assertThat(listAppender.list).isNotEmpty();
        //
        //final ILoggingEvent event = listAppender.list.get(0);
        //String message = event.getFormattedMessage();
        //assertThat(message).isEqualTo("date shows 1970-01-01T00:00:00Z");
        //
        //final EncodingListAppender<ILoggingEvent> stringAppender = getStringAppender();
        //final String json = stringAppender.list.get(0);
        //System.out.println(json);
        //assertThat(json).contains("@type");
    }

    static class MyFieldBuilder implements FieldBuilder {
        static MyFieldBuilder instance() {
            return new MyFieldBuilder() {};
        }

        public Field instant(Instant instant) {
            return value("instant", Value.string(instant.toString()))
                    .withAttribute(CLASS_TYPE_ATTR.bindValue(instant.getClass()));
        }
    }

    public static final class UserID {
        private final String id;

        public UserID(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return this.id;
        }
    }
}
