package com.tersesystems.echopraxia.semantic;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import java.util.function.Function;

public interface SemanticLogger<DataType> {

  boolean isErrorEnabled();

  void error(DataType data);

  boolean isWarnEnabled();

  void warn(DataType data);

  boolean isInfoEnabled();

  void info(DataType data);

  boolean isDebugEnabled();

  void debug(DataType data);

  boolean isTraceEnabled();

  void trace(DataType data);

  SemanticLogger<DataType> withCondition(Condition c);

  SemanticLogger<DataType> withFields(Field.BuilderFunction<Field.Builder> f);

  <FB extends Field.Builder> SemanticLogger<DataType> withFields(
      Field.BuilderFunction<FB> f, FB builder);

  SemanticLogger<DataType> withMessage(Function<DataType, String> messageFunction);
}
