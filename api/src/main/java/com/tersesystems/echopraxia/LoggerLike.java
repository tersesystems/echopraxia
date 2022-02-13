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
public interface LoggerLike<FB extends Field.Builder, SELF> {

  @NotNull
  String getName();

  @NotNull
  CoreLogger core();

  @NotNull
  FB fieldBuilder();


  /**
   * Creates a new logger with context fields from thread context / MDC mapped as context fields.
   *
   * <p>Note that the context map is lazily evaluated on every logging statement.
   *
   * @return the new logger.
   */
  @NotNull
  SELF withThreadContext();

  @NotNull
  <T extends Field.Builder> LoggerLike<T, ?> withFieldBuilder(@NotNull T newBuilder);

  /**
   * Creates a new logger with the given context fields.
   *
   * <p>Note that the field builder function is lazily evaluated on every logging statement.
   *
   * @param f the given function producing fields from a field builder.
   * @return the new logger.
   */
  @NotNull
  SELF withFields(@NotNull Field.BuilderFunction<FB> f);

  /**
   * Creates a new logger with the given condition.
   *
   * <p>Note that the condition is lazily evaluated on every logging statement.
   *
   * @param condition the given condition.
   * @return the new logger.
   */
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

  /**
   * Interface which does mostly nothing.
   *
   * @param <NFB>
   * @param <SELF>
   */
  interface Never<NFB extends Field.Builder, SELF> extends LoggerLike<NFB, SELF> {

    default boolean isTraceEnabled() {
      return false;
    }

    default boolean isTraceEnabled(@NotNull Condition condition) {
      return false;
    }

    @Override
    default void trace(@Nullable String message) {
      // do nothing
    }

    @Override
    default void trace(@Nullable String message, Field.@NotNull BuilderFunction<NFB> f) {
      // do nothing
    }

    @Override
    default void trace(@Nullable String message, @NotNull Throwable e) {
      // do nothing
    }

    @Override
    default void trace(@NotNull Condition condition, @Nullable String message) {
      // do nothing
    }

    @Override
    default void trace(
            @NotNull Condition condition,
            @Nullable String message,
            Field.@NotNull BuilderFunction<NFB> f) {
      // do nothing
    }

    @Override
    default void trace(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
      // do nothing
    }

    /** @return true if the logger level is DEBUG or higher. */
    default boolean isDebugEnabled() {
      return false;
    }

    /**
     * @param condition the given condition.
     * @return true if the logger level is DEBUG or higher and the condition is met.
     */
    default boolean isDebugEnabled(@NotNull Condition condition) {
      return false;
    }

    @Override
    default void debug(@Nullable String message) {
      // do nothing
    }

    @Override
    default void debug(@Nullable String message, Field.@NotNull BuilderFunction<NFB> f) {
      // do nothing
    }

    @Override
    default void debug(@Nullable String message, @NotNull Throwable e) {
      // do nothing
    }

    @Override
    default void debug(@NotNull Condition condition, @Nullable String message) {
      // do nothing
    }

    @Override
    default void debug(
            @NotNull Condition condition,
            @Nullable String message,
            Field.@NotNull BuilderFunction<NFB> f) {
      // do nothing
    }

    @Override
    default void debug(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
      // do nothing
    }

    /** @return true if the logger level is INFO or higher. */
    default boolean isInfoEnabled() {
      return false;
    }

    /**
     * @param condition the given condition.
     * @return true if the logger level is INFO or higher and the condition is met.
     */
    default boolean isInfoEnabled(@NotNull Condition condition) {
      return false;
    }

    @Override
    default void info(@Nullable String message) {
      // do nothing
    }

    @Override
    default void info(@Nullable String message, Field.@NotNull BuilderFunction<NFB> f) {
      // do nothing
    }

    @Override
    default void info(@Nullable String message, @NotNull Throwable e) {
      // do nothing
    }

    @Override
    default void info(@NotNull Condition condition, @Nullable String message) {
      // do nothing
    }

    @Override
    default void info(
            @NotNull Condition condition,
            @Nullable String message,
            Field.@NotNull BuilderFunction<NFB> f) {
      // do nothing
    }

    @Override
    default void info(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
      // do nothing
    }

    /** @return true if the logger level is WARN or higher. */
    default boolean isWarnEnabled() {
      return false;
    }

    /**
     * @param condition the given condition.
     * @return true if the logger level is WARN or higher and the condition is met.
     */
    default boolean isWarnEnabled(@NotNull Condition condition) {
      return false;
    }

    @Override
    default void warn(@Nullable String message, Field.@NotNull BuilderFunction<NFB> f) {
      // do nothing
    }

    @Override
    default void warn(@Nullable String message, @NotNull Throwable e) {
      // do nothing
    }

    @Override
    default void warn(@NotNull Condition condition, @Nullable String message) {
      // do nothing
    }

    @Override
    default void warn(
            @NotNull Condition condition,
            @Nullable String message,
            Field.@NotNull BuilderFunction<NFB> f) {
      // do nothing
    }

    @Override
    default void warn(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
      // do nothing
    }

    /** @return true if the logger level is ERROR or higher. */
    default boolean isErrorEnabled() {
      return false;
    }

    /**
     * @param condition the given condition.
     * @return true if the logger level is ERROR or higher and the condition is met.
     */
    default boolean isErrorEnabled(@NotNull Condition condition) {
      return false;
    }

    @Override
    default void error(@Nullable String message) {
      // do nothing
    }

    @Override
    default void error(@Nullable String message, Field.@NotNull BuilderFunction<NFB> f) {
      // do nothing
    }

    @Override
    default void error(@Nullable String message, @NotNull Throwable e) {
      // do nothing
    }

    @Override
    default void error(@NotNull Condition condition, @Nullable String message) {
      // do nothing
    }

    @Override
    default void error(
            @NotNull Condition condition,
            @Nullable String message,
            Field.@NotNull BuilderFunction<NFB> f) {
      // do nothing
    }

    @Override
    default void error(@NotNull Condition condition, @Nullable String message, @NotNull Throwable e) {
      // do nothing
    }
  }

}
