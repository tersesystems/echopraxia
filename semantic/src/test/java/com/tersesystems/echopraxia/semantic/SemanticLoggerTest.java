package com.tersesystems.echopraxia.semantic;

import com.tersesystems.echopraxia.Condition;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class SemanticLoggerTest {

  @Test
  public void testLogger() {

    SemanticLogger<Date> dateLogger =
        SemanticLoggerFactory.getLogger(
            getClass(),
            java.util.Date.class,
            date -> "date = {}",
            date -> b -> b.onlyString("date", date.toInstant().toString()));

    dateLogger.info(new Date());
    if (dateLogger.isDebugEnabled()) {
      dateLogger.debug(new Date());
    }

    Condition c =
        (level, context) ->
            context.getFields().stream()
                .anyMatch(
                    f ->
                        f.name().equals("something_iffy")
                            ? (Boolean) f.value().raw()
                            : Boolean.valueOf(false));

    SemanticLogger<Date> iffyLogger = dateLogger.withCondition(c);

    boolean iffy = true;
    iffyLogger
        .withMessage(date -> "the date is {} and something is iffy")
        .withFields(b -> b.onlyBool("something_iffy", iffy))
        .warn(new Date()); // should print out with new message
  }
}
