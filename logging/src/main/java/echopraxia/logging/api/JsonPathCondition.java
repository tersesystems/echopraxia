package echopraxia.logging.api;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface JsonPathCondition extends Condition {

  boolean jsonPathTest(Level level, LoggingContextWithFindPathMethods context);

  default boolean test(Level level, LoggingContext context) {
    return jsonPathTest(level, (LoggingContextWithFindPathMethods) context);
  }

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
