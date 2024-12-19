package echopraxia.jsonpath;

import echopraxia.logging.api.Condition;
import echopraxia.logging.api.Level;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JsonPathCondition {

  public static Condition pathCondition(Function<LoggingContextWithFindPathMethods, Boolean> o) {
    return (level, context) -> {
      if (context instanceof LoggingContextWithFindPathMethods) {
        return o.apply((LoggingContextWithFindPathMethods) context);
      } else {
        // XXX should log something here.
        return false;
      }
    };
  }

  public static Condition pathCondition(
      BiFunction<Level, LoggingContextWithFindPathMethods, Boolean> o) {
    return (level, context) -> {
      if (context instanceof LoggingContextWithFindPathMethods) {
        return o.apply(level, (LoggingContextWithFindPathMethods) context);
      } else {
        // XXX should log something here.
        return false;
      }
    };
  }
}
