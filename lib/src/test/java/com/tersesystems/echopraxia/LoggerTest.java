package com.tersesystems.echopraxia;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThat;

class LoggerTest {
    @Test void testDebug() {
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.debug("hello");
    }

    @Test
    @Disabled("Not implemented yet")
    void testAnnotation() {
        LogCaptor logCaptor = LogCaptor.forClass(getClass());

        // We want only objects that have the @Argument annotation to be useful in a
        // logger, or as an Arguments.of().
        // Should use https://github.com/Pante/elementary/tree/stable
        Logger logger = LoggerFactory.getLogger("some.logger");
        Argument argument =
        logger.debug("hello {}", Arguments.of(helloArg));
        fail();
    }
}
