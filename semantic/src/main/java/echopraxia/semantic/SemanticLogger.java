package echopraxia.semantic;

import com.tersesystems.echopraxia.api.*;
import echopraxia.api.Condition;
import echopraxia.api.FieldBuilderResult;
import echopraxia.spi.CoreLogger;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * SemanticLogger interface built around a datatype.
 *
 * <p>You will want to create this using a `SemanticLoggerFactory.getLogger` method.
 *
 * <p>This interface is less flexible than using a field builder, but is far simpler to use.
 *
 * @param <DataType> the data type to use as an argument.
 */
public interface SemanticLogger<DataType> {

  /**
   * @return the name associated with the logger
   */
  @NotNull
  String getName();

  /**
   * @return The core logger underlying this logger
   */
  @NotNull
  CoreLogger core();

  /**
   * @return true if the level is enabled, otherwise false
   */
  boolean isErrorEnabled();

  /**
   * @param c the condition
   * @return true if the level is enabled with the condition, otherwise false
   */
  boolean isErrorEnabled(@NotNull Condition c);

  /**
   * Logs the data.
   *
   * @param data the data to log.
   */
  void error(@NotNull DataType data);

  /**
   * Logs the data if it meets condition.
   *
   * @param c the condition to meet
   * @param data the data to log.
   */
  void error(@NotNull Condition c, @NotNull DataType data);

  /**
   * @return true if the level is enabled, otherwise false
   */
  boolean isWarnEnabled();

  /**
   * @param c the condition
   * @return true if the level is enabled with the condition, otherwise false
   */
  boolean isWarnEnabled(@NotNull Condition c);

  /**
   * Logs the data.
   *
   * @param data the data to log.
   */
  void warn(@NotNull DataType data);

  /**
   * Logs the data if it meets condition.
   *
   * @param c the condition to meet
   * @param data the data to log.
   */
  void warn(@NotNull Condition c, @NotNull DataType data);

  /**
   * @return true if the level is enabled, otherwise false
   */
  boolean isInfoEnabled();

  /**
   * @param c the condition
   * @return true if the level is enabled with the condition, otherwise false
   */
  boolean isInfoEnabled(@NotNull Condition c);

  /**
   * Logs the data.
   *
   * @param data the data to log.
   */
  void info(@NotNull DataType data);

  /**
   * Logs the data if it meets condition.
   *
   * @param c the condition to meet
   * @param data the data to log.
   */
  void info(@NotNull Condition c, @NotNull DataType data);

  /**
   * @return true if the level is enabled, otherwise false
   */
  boolean isDebugEnabled();

  /**
   * @param c the condition
   * @return true if the level is enabled with the condition, otherwise false
   */
  boolean isDebugEnabled(@NotNull Condition c);

  /**
   * Logs the data.
   *
   * @param data the data to log.
   */
  void debug(@NotNull DataType data);

  /**
   * Logs the data if it meets condition.
   *
   * @param c the condition to meet
   * @param data the data to log.
   */
  void debug(@NotNull Condition c, @NotNull DataType data);

  /**
   * @return true if the level is enabled, otherwise false
   */
  boolean isTraceEnabled();

  /**
   * @param c the condition
   * @return true if the level is enabled with the condition, otherwise false
   */
  boolean isTraceEnabled(@NotNull Condition c);

  /**
   * Logs the data.
   *
   * @param data the data to log.
   */
  void trace(@NotNull DataType data);

  /**
   * Logs the data if it meets condition.
   *
   * @param c the condition to meet
   * @param data the data to log.
   */
  void trace(@NotNull Condition c, @NotNull DataType data);

  /**
   * Associates the logger with a condition.
   *
   * @param c the condition to associate with the logger
   * @return a logger with the condition.
   */
  @NotNull
  SemanticLogger<DataType> withCondition(@NotNull Condition c);

  /**
   * Associates the logger with thread context (MDC).
   *
   * @return a logger with thread context enabled.
   */
  @NotNull
  SemanticLogger<DataType> withThreadContext();

  /**
   * Associates the logger with extra fields.
   *
   * @param f the field builder function
   * @param builder the field builder
   * @param <FB> the field builder type
   * @return a logger with extra fields.
   */
  @NotNull
  <FB> SemanticLogger<DataType> withFields(
          @NotNull Function<FB, FieldBuilderResult> f, @NotNull FB builder);

  /**
   * Associates the logger with a message function.
   *
   * @param messageFunction a function that takes in data and returns a string representing message
   * @return a logger with the messaage function.
   */
  @NotNull
  SemanticLogger<DataType> withMessage(@NotNull Function<DataType, String> messageFunction);
}
