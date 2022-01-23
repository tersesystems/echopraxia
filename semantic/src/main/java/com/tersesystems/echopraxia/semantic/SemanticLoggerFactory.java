package com.tersesystems.echopraxia.semantic;

import com.tersesystems.echopraxia.*;
import com.tersesystems.echopraxia.core.Caller;
import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The semantic logger factory. This is used to render complex objects specifically on their type.
 *
 * <p>For example, you may want to render dates using a semantic logger:
 *
 * <p>{@code SemanticLogger<Date> logger = SemanticLoggerFactory.getLogger( getClass(),
 * java.util.Date.class, date -> "date = {}", date -> b -> b.onlyString("date",
 * date.toInstant().toString()));}
 *
 * <p>You would then be able to log dates and <b>only</b> dates as follows:
 *
 * <p>{@code logger.info(new Date()); }
 */
public class SemanticLoggerFactory {

  /**
   * Creates a semantic logger using a logger class and explicit field builder.
   *
   * @param clazz the logger class.
   * @param dataTypeClass the class of the data type.
   * @param messageFunction the function to render a message template.
   * @param f the datatype to builder function.
   * @param builder the field builder to use in the builder function.
   * @param <DataType> the type of data to render as an argument.
   * @param <FB> the field builder type.
   * @return an implementation of semantic logger.
   */
  public static <DataType, FB extends Field.Builder> SemanticLogger<DataType> getLogger(
      Class<?> clazz,
      Class<DataType> dataTypeClass,
      Function<DataType, String> messageFunction,
      Function<DataType, Field.BuilderFunction<FB>> f,
      FB builder) {
    CoreLogger coreLogger = CoreLoggerFactory.getLogger(clazz);
    return getLogger(coreLogger, dataTypeClass, messageFunction, f, builder);
  }

  /**
   * Creates a semantic logger using a logger name and explicit field builder.
   *
   * @param name the logger name.
   * @param dataTypeClass the class of the data type.
   * @param messageFunction the function to render a message template.
   * @param f the datatype to builder function.
   * @param builder the field builder to use in the builder function.
   * @param <DataType> the type of data to render as an argument.
   * @param <FB> the field builder type.
   * @return an implementation of semantic logger.
   */
  public static <DataType, FB extends Field.Builder> SemanticLogger<DataType> getLogger(
      String name,
      Class<DataType> dataTypeClass,
      Function<DataType, String> messageFunction,
      Function<DataType, Field.BuilderFunction<FB>> f,
      FB builder) {
    CoreLogger coreLogger = CoreLoggerFactory.getLogger(name);
    return getLogger(coreLogger, dataTypeClass, messageFunction, f, builder);
  }

  /**
   * Creates a semantic logger using a logger name and a default field builder.
   *
   * @param name the logger name.
   * @param dataTypeClass the class of the data type.
   * @param messageFunction the function to render a message template.
   * @param f the datatype to builder function.
   * @param <DataType> the type of data to render as an argument.
   * @return an implementation of semantic logger.
   */
  public static <DataType> SemanticLogger<DataType> getLogger(
      String name,
      Class<DataType> dataTypeClass,
      Function<DataType, String> messageFunction,
      Function<DataType, Field.BuilderFunction<Field.Builder>> f) {
    return getLogger(name, dataTypeClass, messageFunction, f, Field.Builder.instance());
  }

  /**
   * Creates a semantic logger using a logger class and a default field builder.
   *
   * @param clazz the logger class.
   * @param dataTypeClass the class of the data type.
   * @param messageFunction the function to render a message template.
   * @param f the datatype to builder function.
   * @param <DataType> the type of data to render as an argument.
   * @return an implementation of semantic logger.
   */
  public static <DataType> SemanticLogger<DataType> getLogger(
      Class<?> clazz,
      Class<DataType> dataTypeClass,
      Function<DataType, String> messageFunction,
      Function<DataType, Field.BuilderFunction<Field.Builder>> f) {
    return getLogger(clazz, dataTypeClass, messageFunction, f, Field.Builder.instance());
  }

  /**
   * Creates a semantic logger using the caller's class name and a default field builder.
   *
   * @param dataTypeClass the class of the data type.
   * @param messageFunction the function to render a message template.
   * @param f the datatype to builder function.
   * @param <DataType> the type of data to render as an argument.
   * @return an implementation of semantic logger.
   */
  public static <DataType> SemanticLogger<DataType> getLogger(
      Class<DataType> dataTypeClass,
      Function<DataType, String> messageFunction,
      Function<DataType, Field.BuilderFunction<Field.Builder>> f) {
    return getLogger(
        Caller.resolveClassName(), dataTypeClass, messageFunction, f, Field.Builder.instance());
  }

  /**
   * Creates a semantic logger using the caller's class name and an explicit field builder.
   *
   * @param dataTypeClass the class of the data type.
   * @param messageFunction the function to render a message template.
   * @param f the datatype to builder function.
   * @param builder the field builder to use in the builder function.
   * @param <DataType> the type of data to render as an argument.
   * @return an implementation of semantic logger.
   * @param <FB> the field builder type.
   */
  public static <DataType, FB extends Field.Builder> SemanticLogger<DataType> getLogger(
      Class<DataType> dataTypeClass,
      Function<DataType, String> messageFunction,
      Function<DataType, Field.BuilderFunction<Field.Builder>> f,
      FB builder) {
    return getLogger(Caller.resolveClassName(), dataTypeClass, messageFunction, f, builder);
  }

  /**
   * Creates a semantic logger using a core logger.
   *
   * <p>Useful when you need an escape hatch for an implementation.
   *
   * @param coreLogger a core logger.
   * @param dataTypeClass the class of the data type.
   * @param messageFunction the function to render a message template.
   * @param f the datatype to builder function.
   * @param builder the field builder to use in the builder function.
   * @param <DataType> the type of data to render as an argument.
   * @return an implementation of semantic logger.
   * @param <FB> the field builder type.
   */
  public static <DataType, FB extends Field.Builder> SemanticLogger<DataType> getLogger(
      CoreLogger coreLogger,
      Class<DataType> dataTypeClass,
      Function<DataType, String> messageFunction,
      Function<DataType, Field.BuilderFunction<FB>> f,
      FB builder) {
    return new Impl<>(coreLogger, builder, messageFunction, f);
  }

  // The implementation uses a field builder type, but we can cheat and hide this by only
  // exposing the interface, on the basis that people will generally put up with so many
  // magic generic angle bracket type things.
  public static class Impl<DataType, FB extends Field.Builder> implements SemanticLogger<DataType> {

    private final CoreLogger core;
    private final Function<DataType, Field.BuilderFunction<FB>> builderFunction;
    private final FB builder;
    private final Function<DataType, String> messageFunction;

    protected Impl(
        CoreLogger core,
        FB builder,
        Function<DataType, String> messageFunction,
        Function<DataType, Field.BuilderFunction<FB>> builderFunction) {
      this.core = core;
      this.builderFunction = builderFunction;
      this.messageFunction = messageFunction;
      this.builder = builder;
    }

    @Override
    public CoreLogger core() {
      return core;
    }

    public FB fieldBuilder() {
      return builder;
    }

    public Function<DataType, String> messageFunction() {
      return messageFunction;
    }

    public Function<DataType, Field.BuilderFunction<FB>> builderFunction() {
      return builderFunction;
    }

    @Override
    public boolean isErrorEnabled() {
      return core.isEnabled(Level.ERROR);
    }

    @Override
    public boolean isErrorEnabled(Condition c) {
      return core.isEnabled(Level.ERROR, c);
    }

    @Override
    public void error(DataType data) {
      if (core.isEnabled(Level.ERROR)) {
        core.log(Level.ERROR, convertMessage(data), builderFunction.apply(data), builder);
      }
    }

    @Override
    public void error(Condition c, DataType data) {
      if (core.isEnabled(Level.ERROR, c)) {
        core.log(Level.ERROR, convertMessage(data), builderFunction.apply(data), builder);
      }
    }

    @Override
    public boolean isWarnEnabled() {
      return core.isEnabled(Level.WARN);
    }

    @Override
    public boolean isWarnEnabled(Condition c) {
      return core.isEnabled(Level.WARN, c);
    }

    private String convertMessage(DataType data) {
      return messageFunction.apply(data);
    }

    @Override
    public void warn(DataType data) {
      if (core.isEnabled(Level.WARN)) {
        core.log(Level.WARN, convertMessage(data), builderFunction.apply(data), builder);
      }
    }

    @Override
    public void warn(Condition c, DataType data) {
      if (core.isEnabled(Level.WARN, c)) {
        core.log(Level.WARN, convertMessage(data), builderFunction.apply(data), builder);
      }
    }

    @Override
    public boolean isInfoEnabled() {
      return core.isEnabled(Level.INFO);
    }

    @Override
    public boolean isInfoEnabled(Condition c) {
      return core.isEnabled(Level.INFO, c);
    }

    @Override
    public void info(DataType data) {
      if (core.isEnabled(Level.INFO)) {
        core.log(Level.INFO, convertMessage(data), builderFunction.apply(data), builder);
      }
    }

    @Override
    public void info(Condition c, DataType data) {
      if (core.isEnabled(Level.INFO, c)) {
        core.log(Level.INFO, convertMessage(data), builderFunction.apply(data), builder);
      }
    }

    @Override
    public boolean isDebugEnabled() {
      return core.isEnabled(Level.DEBUG);
    }

    @Override
    public boolean isDebugEnabled(Condition c) {
      return core.isEnabled(Level.DEBUG, c);
    }

    @Override
    public void debug(DataType data) {
      if (core.isEnabled(Level.DEBUG)) {
        core.log(Level.DEBUG, convertMessage(data), builderFunction.apply(data), builder);
      }
    }

    @Override
    public void debug(Condition c, DataType data) {
      if (core.isEnabled(Level.DEBUG, c)) {
        core.log(Level.DEBUG, convertMessage(data), builderFunction.apply(data), builder);
      }
    }

    @Override
    public boolean isTraceEnabled() {
      return core.isEnabled(Level.TRACE);
    }

    @Override
    public boolean isTraceEnabled(Condition c) {
      return core.isEnabled(Level.TRACE, c);
    }

    @Override
    public void trace(DataType data) {
      if (core.isEnabled(Level.TRACE)) {
        core.log(Level.TRACE, convertMessage(data), builderFunction.apply(data), builder);
      }
    }

    @Override
    public void trace(Condition c, DataType data) {
      if (core.isEnabled(Level.TRACE, c)) {
        core.log(Level.TRACE, convertMessage(data), builderFunction.apply(data), builder);
      }
    }

    @Override
    public SemanticLogger<DataType> withCondition(Condition c) {
      final CoreLogger coreLogger = core.withCondition(c);
      return new SemanticLoggerFactory.Impl<>(
          coreLogger, builder, messageFunction, builderFunction);
    }

    @Override
    public SemanticLogger<DataType> withFields(Field.BuilderFunction<Field.Builder> f) {
      return withFields(f, builder);
    }

    @Override
    public SemanticLogger<DataType> withThreadContext() {
      Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform =
          mapSupplier ->
              () ->
                  mapSupplier.get().entrySet().stream()
                      .map(e -> builder.string(e.getKey(), e.getValue()))
                      .collect(Collectors.toList());
      final CoreLogger coreLogger = core.withThreadContext(mapTransform);
      return new SemanticLoggerFactory.Impl<>(
          coreLogger, builder, messageFunction, builderFunction);
    }

    @Override
    public <CFB extends Field.Builder> SemanticLogger<DataType> withFields(
        Field.BuilderFunction<CFB> ctxBuilderF, CFB ctxBuilder) {
      final CoreLogger coreLogger = core.withFields(ctxBuilderF, ctxBuilder);
      return new SemanticLoggerFactory.Impl<>(
          coreLogger, builder, messageFunction, builderFunction);
    }

    @Override
    public SemanticLogger<DataType> withMessage(Function<DataType, String> messageFunction) {
      return new SemanticLoggerFactory.Impl<>(core, builder, messageFunction, builderFunction);
    }
  }
}
