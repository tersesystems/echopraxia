package echopraxia.jsonpath;

import echopraxia.logging.api.Condition;
import echopraxia.logging.api.Level;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class JsonPathCondition {

  @Contract(pure = true)
  public static @NotNull Condition pathCondition(
      Function<LoggingContextWithFindPathMethods, Boolean> o) {
    return (level, context) -> {
      if (context instanceof LoggingContextWithFindPathMethods) {
        return o.apply((LoggingContextWithFindPathMethods) context);
      } else {
        throw new IllegalStateException(
            "pathCondition requires LoggingContextWithFindPathMethods instance!");
      }
    };
  }

  @Contract(pure = true)
  public static @NotNull Condition pathCondition(
      BiFunction<Level, LoggingContextWithFindPathMethods, Boolean> o) {
    return (level, context) -> {
      if (context instanceof LoggingContextWithFindPathMethods) {
        return o.apply(level, (LoggingContextWithFindPathMethods) context);
      } else {
        throw new IllegalStateException(
            "pathCondition requires LoggingContextWithFindPathMethods instance!");
      }
    };
  }
}
