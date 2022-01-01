package com.tersesystems.echopraxia.semantic;

import com.tersesystems.echopraxia.*;
import java.util.function.Function;

public class SemanticLoggerFactory {

  public static <DataType, FB extends Field.Builder> SemanticLogger<DataType> getLogger(
      Class<?> clazz,
      Class<DataType> dataTypeClass,
      Function<DataType, String> messageFunction,
      Function<DataType, Field.BuilderFunction<FB>> f,
      FB builder) {
    CoreLogger coreLogger = LoggerFactory.getLogger(clazz).core();
    return new Impl<>(coreLogger, builder, messageFunction, f);
  }

  public static <DataType, FB extends Field.Builder> SemanticLogger<DataType> getLogger(
      String name,
      Class<DataType> dataTypeClass,
      Function<DataType, String> messageFunction,
      Function<DataType, Field.BuilderFunction<FB>> f,
      FB builder) {
    CoreLogger coreLogger = LoggerFactory.getLogger(name).core();
    return new Impl<>(coreLogger, builder, messageFunction, f);
  }

  public static <DataType> SemanticLogger<DataType> getLogger(
      String name,
      Class<DataType> dataTypeClass,
      Function<DataType, String> messageFunction,
      Function<DataType, Field.BuilderFunction<Field.Builder>> f) {
    return getLogger(name, dataTypeClass, messageFunction, f, Logger.defaultFieldBuilder());
  }

  public static <DataType> SemanticLogger<DataType> getLogger(
      Class<?> clazz,
      Class<DataType> dataTypeClass,
      Function<DataType, String> messageFunction,
      Function<DataType, Field.BuilderFunction<Field.Builder>> f) {
    return getLogger(clazz, dataTypeClass, messageFunction, f, Logger.defaultFieldBuilder());
  }

  static class Impl<DataType, FB extends Field.Builder> implements SemanticLogger<DataType> {

    private final CoreLogger core;
    private final Function<DataType, Field.BuilderFunction<FB>> f;
    private final FB builder;
    private final Function<DataType, String> messageFunction;

    Impl(
        CoreLogger core,
        FB builder,
        Function<DataType, String> messageFunction,
        Function<DataType, Field.BuilderFunction<FB>> f) {
      this.core = core;
      this.f = f;
      this.messageFunction = messageFunction;
      this.builder = builder;
    }

    @Override
    public boolean isErrorEnabled() {
      return core.isEnabled(Level.ERROR);
    }

    @Override
    public void error(DataType data) {
      if (core.isEnabled(Level.ERROR)) {
        core.log(Level.ERROR, convertMessage(data), f.apply(data), builder);
      }
    }

    @Override
    public boolean isWarnEnabled() {
      return core.isEnabled(Level.WARN);
    }

    private String convertMessage(DataType data) {
      return messageFunction.apply(data);
    }

    @Override
    public void warn(DataType data) {
      if (core.isEnabled(Level.WARN)) {
        core.log(Level.WARN, convertMessage(data), f.apply(data), builder);
      }
    }

    @Override
    public boolean isInfoEnabled() {
      return core.isEnabled(Level.INFO);
    }

    @Override
    public void info(DataType data) {
      if (core.isEnabled(Level.INFO)) {
        core.log(Level.INFO, convertMessage(data), f.apply(data), builder);
      }
    }

    @Override
    public boolean isDebugEnabled() {
      return core.isEnabled(Level.DEBUG);
    }

    @Override
    public void debug(DataType data) {
      if (core.isEnabled(Level.DEBUG)) {
        core.log(Level.DEBUG, convertMessage(data), f.apply(data), builder);
      }
    }

    @Override
    public boolean isTraceEnabled() {
      return core.isEnabled(Level.TRACE);
    }

    @Override
    public void trace(DataType data) {
      if (core.isEnabled(Level.TRACE)) {
        core.log(Level.TRACE, convertMessage(data), f.apply(data), builder);
      }
    }

    @Override
    public SemanticLogger<DataType> withCondition(Condition c) {
      final CoreLogger coreLogger = core.withCondition(c);
      return new SemanticLoggerFactory.Impl<>(coreLogger, builder, messageFunction, f);
    }

    @Override
    public SemanticLogger<DataType> withFields(Field.BuilderFunction<Field.Builder> f) {
      return withFields(f, builder);
    }

    @Override
    public <CFB extends Field.Builder> SemanticLogger<DataType> withFields(
        Field.BuilderFunction<CFB> ctxBuilderF, CFB ctxBuilder) {
      final CoreLogger coreLogger = core.withFields(ctxBuilderF, ctxBuilder);
      return new SemanticLoggerFactory.Impl<>(coreLogger, builder, messageFunction, f);
    }

    @Override
    public SemanticLogger<DataType> withMessage(Function<DataType, String> messageFunction) {
      return new SemanticLoggerFactory.Impl<>(core, builder, messageFunction, f);
    }
  }
}
