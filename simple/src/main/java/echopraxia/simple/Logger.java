package echopraxia.simple;

import echopraxia.api.Field;
import echopraxia.api.FieldBuilder;
import echopraxia.api.FieldBuilderResult;
import echopraxia.logging.api.Condition;
import echopraxia.logging.api.Level;
import echopraxia.logging.spi.CoreLogger;
import echopraxia.logging.spi.Utilities;
import org.jetbrains.annotations.NotNull;

public class Logger {
  @NotNull private final CoreLogger core;

  @NotNull private static final FieldBuilder FIELD_BUILDER = FieldBuilder.instance();

  @NotNull
  public String getName() {
    return core.getName();
  }

  @NotNull
  public CoreLogger core() {
    return core;
  }

  @NotNull
  public FieldBuilder fieldBuilder() {
    return FIELD_BUILDER;
  }

  public Logger(@NotNull CoreLogger logger) {
    this.core = logger;
  }

  public void trace(String template, FieldBuilderResult result) {
    core.log(Level.TRACE, template, fb -> result, FIELD_BUILDER);
  }

  public void trace(String template, Field... fields) {
    core.log(Level.TRACE, template, fb -> FieldBuilderResult.list(fields), FIELD_BUILDER);
  }

  public void debug(String template, FieldBuilderResult result) {
    core.log(Level.DEBUG, template, fb -> result, FIELD_BUILDER);
  }

  public void debug(String template, Field... fields) {
    core.log(Level.DEBUG, template, fb -> FieldBuilderResult.list(fields), FIELD_BUILDER);
  }

  public void info(String template, FieldBuilderResult result) {
    core.log(Level.INFO, template, fb -> result, FIELD_BUILDER);
  }

  public void info(String template, Field... fields) {
    core.log(Level.INFO, template, fb -> FieldBuilderResult.list(fields), FIELD_BUILDER);
  }

  public void warn(String template, FieldBuilderResult result) {
    core.log(Level.WARN, template, fb -> result, FIELD_BUILDER);
  }

  public void warn(String template, Field... fields) {
    core.log(Level.WARN, template, fb -> FieldBuilderResult.list(fields), FIELD_BUILDER);
  }

  public void error(String template, FieldBuilderResult result) {
    core.log(Level.ERROR, template, fb -> result, FIELD_BUILDER);
  }

  public void error(String template, Field... fields) {
    core.log(Level.ERROR, template, fb -> FieldBuilderResult.list(fields), FIELD_BUILDER);
  }

  public boolean isTraceEnabled() {
    return core().isEnabled(Level.TRACE);
  }

  public boolean isDebugEnabled() {
    return core().isEnabled(Level.DEBUG);
  }

  public boolean isInfoEnabled() {
    return core().isEnabled(Level.INFO);
  }

  public boolean isWarnEnabled() {
    return core().isEnabled(Level.WARN);
  }

  public boolean isErrorEnabled() {
    return core().isEnabled(Level.ERROR);
  }

  public boolean isEnabled(Level level) {
    return core().isEnabled(level);
  }

  public @NotNull Logger withThreadContext() {
    return new Logger(core.withThreadContext(Utilities.threadContext()));
  }

  public @NotNull Logger withFields(@NotNull FieldBuilderResult result) {
    return new Logger(core.withFields(f -> result, fieldBuilder()));
  }

  public @NotNull Logger withFields(@NotNull Field... fields) {
    return new Logger(core.withFields(f -> FieldBuilderResult.list(fields), fieldBuilder()));
  }

  public @NotNull Logger withCondition(@NotNull Condition condition) {
    return new Logger(core.withCondition(condition));
  }
}
