package com.tersesystems.echopraxia.semantic;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import java.util.function.Function;

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

  boolean isErrorEnabled();

  boolean isErrorEnabled(Condition c);

  void error(DataType data);

  void error(Condition c, DataType data);

  boolean isWarnEnabled();

  boolean isWarnEnabled(Condition c);

  void warn(DataType data);

  void warn(Condition c, DataType data);

  boolean isInfoEnabled();

  boolean isInfoEnabled(Condition c);

  void info(DataType data);

  void info(Condition c, DataType data);

  boolean isDebugEnabled();

  boolean isDebugEnabled(Condition c);

  void debug(DataType data);

  void debug(Condition c, DataType data);

  boolean isTraceEnabled();

  boolean isTraceEnabled(Condition c);

  void trace(DataType data);

  void trace(Condition c, DataType data);

  SemanticLogger<DataType> withCondition(Condition c);

  SemanticLogger<DataType> withFields(Field.BuilderFunction<Field.Builder> f);

  <FB extends Field.Builder> SemanticLogger<DataType> withFields(
      Field.BuilderFunction<FB> f, FB builder);

  SemanticLogger<DataType> withMessage(Function<DataType, String> messageFunction);
}
