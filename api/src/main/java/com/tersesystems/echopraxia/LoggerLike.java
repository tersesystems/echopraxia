package com.tersesystems.echopraxia;

import com.tersesystems.echopraxia.core.CoreLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface covering base common functionality between AsyncLogger and Logger.
 *
 * @param <FB> the field builder type
 * @param <SELF> the logger type
 */
public interface LoggerLike<FB extends Field.Builder, SELF extends LoggerLike<FB, SELF>> {

  @NotNull
  CoreLogger core();

  @NotNull
  FB fieldBuilder();

  @NotNull
  SELF withThreadContext();

  @NotNull
  <T extends Field.Builder> LoggerLike<T, ?> withFieldBuilder(@NotNull T newBuilder);

  @NotNull
  SELF withFields(@NotNull Field.BuilderFunction<FB> f);

  @NotNull
  SELF withCondition(@NotNull Condition condition);

  // ------------------------------------------------------------------------
  // TRACE

  void trace(@Nullable String message);

  void trace(@Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  void trace(@Nullable String message, @NotNull Throwable e);

  void trace(@NotNull Condition condition, @Nullable String message);

  void trace(
      @NotNull Condition condition, @Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  void trace(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e);

  // ------------------------------------------------------------------------
  // DEBUG

  void debug(@Nullable String message);

  void debug(@Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  void debug(@Nullable String message, @NotNull Throwable e);

  void debug(@NotNull Condition condition, @Nullable String message);

  void debug(
      @NotNull Condition condition, @Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  void debug(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e);

  // ------------------------------------------------------------------------
  // INFO

  void info(@Nullable String message);

  void info(@Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  void info(@Nullable String message, @NotNull Throwable e);

  void info(@NotNull Condition condition, @Nullable String message);

  void info(
      @NotNull Condition condition, @Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  void info(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e);

  // ------------------------------------------------------------------------
  // WARN

  void warn(@Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  void warn(@Nullable String message, @NotNull Throwable e);

  void warn(@NotNull Condition condition, @Nullable String message);

  void warn(
      @NotNull Condition condition, @Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  void warn(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e);

  // ------------------------------------------------------------------------
  // ERROR

  void error(@Nullable String message);

  void error(@Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  void error(@Nullable String message, @NotNull Throwable e);

  void error(@NotNull Condition condition, @Nullable String message);

  void error(
      @NotNull Condition condition, @Nullable String message, @NotNull Field.BuilderFunction<FB> f);

  void error(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e);
}
