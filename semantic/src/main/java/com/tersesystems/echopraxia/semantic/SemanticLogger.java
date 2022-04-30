package com.tersesystems.echopraxia.semantic;

import com.tersesystems.echopraxia.api.*;
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

  @NotNull
  String getName();

  @NotNull
  CoreLogger core();

  boolean isErrorEnabled();

  boolean isErrorEnabled(@NotNull Condition c);

  void error(@NotNull DataType data);

  void error(@NotNull Condition c, @NotNull DataType data);

  boolean isWarnEnabled();

  boolean isWarnEnabled(@NotNull Condition c);

  void warn(@NotNull DataType data);

  void warn(@NotNull Condition c, @NotNull DataType data);

  boolean isInfoEnabled();

  boolean isInfoEnabled(@NotNull Condition c);

  void info(@NotNull DataType data);

  void info(@NotNull Condition c, @NotNull DataType data);

  boolean isDebugEnabled();

  boolean isDebugEnabled(@NotNull Condition c);

  void debug(@NotNull DataType data);

  void debug(@NotNull Condition c, @NotNull DataType data);

  boolean isTraceEnabled();

  boolean isTraceEnabled(@NotNull Condition c);

  void trace(@NotNull DataType data);

  void trace(@NotNull Condition c, @NotNull DataType data);

  @NotNull
  SemanticLogger<DataType> withCondition(@NotNull Condition c);

  @NotNull
  SemanticLogger<DataType> withFields(@NotNull Function<FieldBuilder, FieldBuilderResult> f);

  @NotNull
  SemanticLogger<DataType> withThreadContext();

  @NotNull
  <FB> SemanticLogger<DataType> withFields(
      @NotNull Function<FB, FieldBuilderResult> f, @NotNull FB builder);

  @NotNull
  SemanticLogger<DataType> withMessage(@NotNull Function<DataType, String> messageFunction);
}
