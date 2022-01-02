package com.tersesystems.echopraxia;

import static com.tersesystems.echopraxia.Level.*;

import com.tersesystems.echopraxia.core.CoreLogger;
import java.lang.reflect.InvocationTargetException;

/**
 * An echopraxia logger built around a field builder.
 *
 * @param <FB> the field builder type.
 */
public class Logger<FB extends Field.Builder> {

  protected static final Field.Builder instance = new Default();

  static class Default implements Field.Builder {}

  // XXX field builder should be an SPI instance
  public static Field.Builder defaultFieldBuilder() {
    return instance;
  }

  protected final CoreLogger core;
  protected final FB fieldBuilder;

  protected Logger(CoreLogger core, FB fieldBuilder) {
    this.core = core;
    this.fieldBuilder = fieldBuilder;
  }

  // This is useful as an escape hatch so we can cast and call directly, and do
  // SLF4J specific things...
  //
  // LogstashCoreLogger core = (LogstashCoreLogger) logger.core();
  // var newCore = core.withMarkers(org.slf4j.MarkerFactory.getMarker("SECURITY"))
  // var newLogger = new Logger(newCore, logger.fieldBuilder());
  public CoreLogger core() {
    return core;
  }

  public FB fieldBuilder() {
    return fieldBuilder;
  }

  public <T extends Field.Builder> Logger<T> withFieldBuilder(T newBuilder) {
    return new Logger<>(core(), newBuilder);
  }

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

  public Logger<FB> withCondition(Condition condition) {
    return new Logger<>(core().withCondition(condition), fieldBuilder);
  }

  public Logger<FB> withFields(Field.BuilderFunction<FB> f) {
    return new Logger<>(core().withFields(f, fieldBuilder), fieldBuilder);
  }

  public <CFB extends Field.Builder> Logger<FB> withFields(
      Field.BuilderFunction<CFB> ctxBuilderF, CFB ctxBuilder) {
    final CoreLogger coreLogger = core().withFields(ctxBuilderF, ctxBuilder);
    return new Logger<>(coreLogger, fieldBuilder);
  }

  public boolean isTraceEnabled() {
    return core().isEnabled(TRACE);
  }

  public boolean isTraceEnabled(Condition condition) {
    return core().isEnabled(TRACE, condition);
  }

  public void trace(String message) {
    core().log(TRACE, message);
  }

  public void trace(String message, Field.BuilderFunction<FB> f) {
    core().log(TRACE, message, f, fieldBuilder);
  }

  public void trace(String message, Throwable e) {
    core().log(TRACE, message, e);
  }

  public void trace(Condition condition, String message) {
    core().log(TRACE, condition, message);
  }

  public void trace(Condition condition, String message, Throwable e) {
    core().log(TRACE, condition, message, e);
  }

  public void trace(Condition condition, String message, Field.BuilderFunction<FB> f) {
    core().log(TRACE, condition, message, f, fieldBuilder);
  }

  public boolean isDebugEnabled() {
    return core().isEnabled(DEBUG);
  }

  public boolean isDebugEnabled(Condition condition) {
    return core().isEnabled(DEBUG, condition);
  }

  public void debug(String message) {
    core().log(DEBUG, message);
  }

  public void debug(String message, Field.BuilderFunction<FB> f) {
    core().log(DEBUG, message, f, fieldBuilder);
  }

  public void debug(String message, Throwable e) {
    core().log(DEBUG, message, e);
  }

  public void debug(Condition condition, String message) {
    core().log(DEBUG, condition, message);
  }

  public void debug(Condition condition, String message, Throwable e) {
    core().log(DEBUG, condition, message, e);
  }

  public void debug(Condition condition, String message, Field.BuilderFunction<FB> f) {
    core().log(DEBUG, condition, message, f, fieldBuilder);
  }

  public boolean isInfoEnabled() {
    return core().isEnabled(INFO);
  }

  public boolean isInfoEnabled(Condition condition) {
    return core().isEnabled(INFO, condition);
  }

  public void info(String message) {
    core().log(INFO, message);
  }

  public void info(String message, Throwable e) {
    core().log(INFO, message, e);
  }

  public void info(Condition condition, String message) {
    core().log(INFO, condition, message);
  }

  public void info(Condition condition, String message, Throwable e) {
    core().log(INFO, condition, message, e);
  }

  public void info(Condition condition, String message, Field.BuilderFunction<FB> f) {
    core().log(INFO, condition, message, f, fieldBuilder);
  }

  public void info(String message, Field.BuilderFunction<FB> f) {
    core().log(INFO, message, f, fieldBuilder);
  }

  public boolean isWarnEnabled() {
    return core().isEnabled(WARN);
  }

  public boolean isWarnEnabled(Condition condition) {
    return core().isEnabled(WARN, condition);
  }

  public void warn(String message) {
    core().log(WARN, message);
  }

  public void warn(String message, Field.BuilderFunction<FB> f) {
    core().log(WARN, message, f, fieldBuilder);
  }

  public void warn(String message, Throwable e) {
    core().log(WARN, message, e);
  }

  public void warn(Condition condition, String message) {
    core().log(WARN, condition, message);
  }

  public void warn(Condition condition, String message, Throwable e) {
    core().log(WARN, condition, message, e);
  }

  public void warn(Condition condition, String message, Field.BuilderFunction<FB> f) {
    core().log(WARN, condition, message, f, fieldBuilder);
  }

  public boolean isErrorEnabled() {
    return core().isEnabled(ERROR);
  }

  public boolean isErrorEnabled(Condition condition) {
    return core().isEnabled(ERROR, condition);
  }

  public void error(String message) {
    core().log(ERROR, message);
  }

  public void error(String message, Throwable e) {
    core().log(ERROR, message, e);
  }

  public void error(Condition condition, String message) {
    core().log(ERROR, condition, message);
  }

  public void error(Condition condition, String message, Throwable e) {
    core().log(ERROR, condition, message, e);
  }

  public void error(Condition condition, String message, Field.BuilderFunction<FB> f) {
    core().log(ERROR, condition, message, f, fieldBuilder);
  }

  public void error(String message, Field.BuilderFunction<FB> f) {
    core().log(ERROR, message, f, fieldBuilder);
  }
}
