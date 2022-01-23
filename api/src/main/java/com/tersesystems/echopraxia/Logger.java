package com.tersesystems.echopraxia;

import static com.tersesystems.echopraxia.Level.*;

import com.tersesystems.echopraxia.core.CoreLogger;
import java.lang.reflect.InvocationTargetException;

/**
 * An echopraxia logger built around a field builder.
 *
 * <p>This class is explicitly designed to be subclassed so that end users can customize it and
 * avoid the parameterized type tax.
 *
 * <p>{@code <pre>
 * public class MyLogger extends Logger&lt;MyFieldBuilder&gt; {
 *   protected MyLogger(CoreLogger core, MyFieldBuilder fieldBuilder) { super(core, fieldBuilder); }
 * }
 *
 * static class MyLoggerFactory {
 *   public static MyLogger getLogger() {
 *     return new MyLogger(CoreLoggerFactory.getLogger(), myFieldBuilder);
 *   }
 * }
 *
 * MyLogger logger = MyLoggerFactory.getLogger();
 * </pre>
 * }
 * @param <FB> the field builder type.
 */
public class Logger<FB extends Field.Builder> {

  protected final CoreLogger core;
  protected final FB fieldBuilder;

  protected Logger(CoreLogger core, FB fieldBuilder) {
    this.core = core;
    this.fieldBuilder = fieldBuilder;
  }

  /** @return the internal core logger. */
  public CoreLogger core() {
    return core;
  }

  /** @return the field builder. */
  public FB fieldBuilder() {
    return fieldBuilder;
  }

  /**
   * Creates a new logger with the given field builder.
   *
   * @param newBuilder the given field builder.
   * @param <T> the type of the field builder.
   * @return a new logger using the given field builder.
   */
  public <T extends Field.Builder> Logger<T> withFieldBuilder(T newBuilder) {
    if (this.fieldBuilder == newBuilder) {
      //noinspection unchecked
      return (Logger<T>) this;
    }
    return new Logger<>(core(), newBuilder);
  }

  /**
   * Creates a new logger with the given field builder, using reflection.
   *
   * @param newBuilderClass the class of given field builder.
   * @param <T> the type of the field builder.
   * @return a new logger using the given field builder.
   */
  public <T extends Field.Builder> Logger<T> withFieldBuilder(Class<T> newBuilderClass) {
    try {
      final T newInstance = newBuilderClass.getDeclaredConstructor().newInstance();
      return new Logger<>(core(), newInstance);
    } catch (NoSuchMethodException
        | SecurityException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Creates a new logger with the given condition.
   *
   * @param condition the given condition.
   * @return the new logger.
   */
  public Logger<FB> withCondition(Condition condition) {
    if (condition == Condition.always()) {
      return this;
    }
    // Reduce allocation if we can help it
    final CoreLogger coreLogger = core().withCondition(condition);
    if (coreLogger == core()) {
      return this;
    }
    return new Logger<>(coreLogger, fieldBuilder);
  }

  /**
   * Creates a new logger with the given context fields.
   *
   * @param f the given function producing fields from a field builder.
   * @return the new logger.
   */
  public Logger<FB> withFields(Field.BuilderFunction<FB> f) {
    return new Logger<>(core().withFields(f, fieldBuilder), fieldBuilder);
  }

  /**
   * Creates a new logger with fields using a one-off field builder for the function that is
   * <b>not</b> passed through to the new logger.
   *
   * @param ctxBuilderF the given function producing fields from a field builder.
   * @param ctxBuilder the field builder to use for the function.
   * @param <CFB> the context field builder type.
   * @return the new logger.
   */
  public <CFB extends Field.Builder> Logger<FB> withFields(
      Field.BuilderFunction<CFB> ctxBuilderF, CFB ctxBuilder) {
    final CoreLogger coreLogger = core().withFields(ctxBuilderF, ctxBuilder);
    return new Logger<>(coreLogger, fieldBuilder);
  }

  // ------------------------------------------------------------------------
  // TRACE

  /** @return true if the logger level is TRACE or higher. */
  public boolean isTraceEnabled() {
    return core().isEnabled(TRACE);
  }

  /**
   * @param condition the given condition.
   * @return true if the logger level is TRACE or higher and the condition is met.
   */
  public boolean isTraceEnabled(Condition condition) {
    return core().isEnabled(TRACE, condition);
  }

  /**
   * Logs statement at TRACE level.
   *
   * @param message the given message.
   */
  public void trace(String message) {
    core().log(TRACE, message);
  }

  /**
   * Logs statement at TRACE level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  public void trace(String message, Field.BuilderFunction<FB> f) {
    core().log(TRACE, message, f, fieldBuilder);
  }

  /**
   * Logs statement at TRACE level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  public void trace(String message, Throwable e) {
    core().log(TRACE, message, e);
  }

  /**
   * Conditionally logs statement at TRACE level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  public void trace(Condition condition, String message) {
    core().log(TRACE, condition, message);
  }

  /**
   * Conditionally logs statement at TRACE level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  public void trace(Condition condition, String message, Field.BuilderFunction<FB> f) {
    core().log(TRACE, condition, message, f, fieldBuilder);
  }

  /**
   * Conditionally logs statement at TRACE level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  public void trace(Condition condition, String message, Throwable e) {
    core().log(TRACE, condition, message, e);
  }

  // ------------------------------------------------------------------------
  // DEBUG

  /** @return true if the logger level is DEBUG or higher. */
  public boolean isDebugEnabled() {
    return core().isEnabled(DEBUG);
  }

  /**
   * @param condition the given condition.
   * @return true if the logger level is DEBUG or higher and the condition is met.
   */
  public boolean isDebugEnabled(Condition condition) {
    return core().isEnabled(DEBUG, condition);
  }

  /**
   * Logs statement at DEBUG level.
   *
   * @param message the given message.
   */
  public void debug(String message) {
    core().log(DEBUG, message);
  }

  /**
   * Logs statement at DEBUG level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  public void debug(String message, Field.BuilderFunction<FB> f) {
    core().log(DEBUG, message, f, fieldBuilder);
  }

  /**
   * Logs statement at DEBUG level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  public void debug(String message, Throwable e) {
    core().log(DEBUG, message, e);
  }

  /**
   * Conditionally logs statement at DEBUG level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  public void debug(Condition condition, String message) {
    core().log(DEBUG, condition, message);
  }

  /**
   * Conditionally logs statement at DEBUG level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  public void debug(Condition condition, String message, Field.BuilderFunction<FB> f) {
    core().log(DEBUG, condition, message, f, fieldBuilder);
  }

  /**
   * Conditionally logs statement at DEBUG level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  public void debug(Condition condition, String message, Throwable e) {
    core().log(DEBUG, condition, message, e);
  }

  // ------------------------------------------------------------------------
  // INFO

  /** @return true if the logger level is INFO or higher. */
  public boolean isInfoEnabled() {
    return core().isEnabled(INFO);
  }

  /**
   * @param condition the given condition.
   * @return true if the logger level is INFO or higher and the condition is met.
   */
  public boolean isInfoEnabled(Condition condition) {
    return core().isEnabled(INFO, condition);
  }

  /**
   * Logs statement at INFO level.
   *
   * @param message the given message.
   */
  public void info(String message) {
    core().log(INFO, message);
  }

  /**
   * Logs statement at INFO level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  public void info(String message, Field.BuilderFunction<FB> f) {
    core().log(INFO, message, f, fieldBuilder);
  }

  /**
   * Logs statement at INFO level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  public void info(String message, Throwable e) {
    core().log(INFO, message, e);
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  public void info(Condition condition, String message) {
    core().log(INFO, condition, message);
  }

  /**
   * Conditionally logs statement at INFO level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  public void info(Condition condition, String message, Field.BuilderFunction<FB> f) {
    core().log(INFO, condition, message, f, fieldBuilder);
  }

  /**
   * Conditionally logs statement at INFO level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  public void info(Condition condition, String message, Throwable e) {
    core().log(INFO, condition, message, e);
  }

  // ------------------------------------------------------------------------
  // WARN

  /** @return true if the logger level is WARN or higher. */
  public boolean isWarnEnabled() {
    return core().isEnabled(WARN);
  }

  /**
   * @param condition the given condition.
   * @return true if the logger level is WARN or higher and the condition is met.
   */
  public boolean isWarnEnabled(Condition condition) {
    return core().isEnabled(WARN, condition);
  }

  /**
   * Logs statement at WARN level.
   *
   * @param message the given message.
   */
  public void warn(String message) {
    core().log(WARN, message);
  }

  /**
   * Logs statement at WARN level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  public void warn(String message, Field.BuilderFunction<FB> f) {
    core().log(WARN, message, f, fieldBuilder);
  }

  /**
   * Logs statement at WARN level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  public void warn(String message, Throwable e) {
    core().log(WARN, message, e);
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  public void warn(Condition condition, String message) {
    core().log(WARN, condition, message);
  }

  /**
   * Conditionally logs statement at INFO level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  public void warn(Condition condition, String message, Field.BuilderFunction<FB> f) {
    core().log(WARN, condition, message, f, fieldBuilder);
  }

  /**
   * Conditionally logs statement at INFO level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  public void warn(Condition condition, String message, Throwable e) {
    core().log(WARN, condition, message, e);
  }

  // ------------------------------------------------------------------------
  // ERROR

  /** @return true if the logger level is ERROR or higher. */
  public boolean isErrorEnabled() {
    return core().isEnabled(ERROR);
  }

  /**
   * @param condition the given condition.
   * @return true if the logger level is ERROR or higher and the condition is met.
   */
  public boolean isErrorEnabled(Condition condition) {
    return core().isEnabled(ERROR, condition);
  }

  /**
   * Logs statement at INFO level.
   *
   * @param message the given message.
   */
  public void error(String message) {
    core().log(ERROR, message);
  }

  /**
   * Logs statement at INFO level using a field builder function.
   *
   * @param message the message.
   * @param f the field builder function.
   */
  public void error(String message, Field.BuilderFunction<FB> f) {
    core().log(ERROR, message, f, fieldBuilder);
  }

  /**
   * Logs statement at INFO level with exception.
   *
   * @param message the message.
   * @param e the given exception.
   */
  public void error(String message, Throwable e) {
    core().log(ERROR, message, e);
  }

  /**
   * Conditionally logs statement at INFO level.
   *
   * @param condition the given condition.
   * @param message the message.
   */
  public void error(Condition condition, String message) {
    core().log(ERROR, condition, message);
  }

  /**
   * Conditionally logs statement at INFO level using a field builder function.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param f the field builder function.
   */
  public void error(Condition condition, String message, Field.BuilderFunction<FB> f) {
    core().log(ERROR, condition, message, f, fieldBuilder);
  }

  /**
   * Conditionally logs statement at INFO level with exception.
   *
   * @param condition the given condition.
   * @param message the message.
   * @param e the given exception.
   */
  public void error(Condition condition, String message, Throwable e) {
    core().log(ERROR, condition, message, e);
  }
}
