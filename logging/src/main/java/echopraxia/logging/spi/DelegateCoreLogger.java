package echopraxia.logging.spi;

import echopraxia.api.*;
import echopraxia.logging.api.Condition;
import echopraxia.logging.api.Level;
import echopraxia.logging.api.LoggerHandle;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A convenient core logger that delegates. This is useful for overrides. */
public class DelegateCoreLogger implements CoreLogger {

  protected CoreLogger core;

  public DelegateCoreLogger(CoreLogger core) {
    this.core = core;
  }

  @Override
  @NotNull
  public String getName() {
    return core.getName();
  }

  @Override
  public @NotNull LoggerContext getLoggerContext() {
    return core.getLoggerContext();
  }

  @Override
  @NotNull
  public Condition condition() {
    return core.condition();
  }

  @Override
  @NotNull
  public String fqcn() {
    return core.fqcn();
  }

  @Override
  @NotNull
  public <FB> CoreLogger withFields(
      @NotNull Function<FB, FieldBuilderResult> f, @NotNull FB builder) {
    return core.withFields(f, builder);
  }

  @Override
  @NotNull
  public CoreLogger withThreadContext(
      @NotNull Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform) {
    return core.withThreadContext(mapTransform);
  }

  @Override
  @NotNull
  public CoreLogger withThreadLocal(Supplier<Runnable> newSupplier) {
    return core.withThreadLocal(newSupplier);
  }

  @Override
  @NotNull
  public CoreLogger withCondition(@NotNull Condition condition) {
    return core.withCondition(condition);
  }

  @Override
  @NotNull
  public CoreLogger withFQCN(@NotNull String fqcn) {
    return core.withFQCN(fqcn);
  }

  @Override
  public boolean isEnabled(@NotNull Level level) {
    return core.isEnabled(level);
  }

  @Override
  public boolean isEnabled(@NotNull Level level, @NotNull Condition condition) {
    return core.isEnabled(level, condition);
  }

  @Override
  public boolean isEnabled(@NotNull Level level, @NotNull Supplier<List<Field>> extraFields) {
    return core.isEnabled(level, extraFields);
  }

  @Override
  public boolean isEnabled(
      @NotNull Level level,
      @NotNull Condition condition,
      @NotNull Supplier<List<Field>> extraFields) {
    return core.isEnabled(level, condition, extraFields);
  }

  @Override
  public void log(@NotNull Level level, @Nullable String message) {
    core.log(level, message);
  }

  @Override
  public void log(
      @NotNull Level level, @NotNull Supplier<List<Field>> extraFields, @Nullable String message) {
    core.log(level, extraFields, message);
  }

  @Override
  public <FB> void log(
      @NotNull Level level,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    core.log(level, message, f, builder);
  }

  @Override
  public <FB> void log(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    core.log(level, extraFields, message, f, builder);
  }

  @Override
  public void log(@NotNull Level level, @NotNull Condition condition, @Nullable String message) {
    core.log(level, condition, message);
  }

  @Override
  public void log(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @NotNull Condition condition,
      @Nullable String message) {
    core.log(level, extraFields, condition, message);
  }

  @Override
  public <FB> void log(
      @NotNull Level level,
      @NotNull Condition condition,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    core.log(level, condition, message, f, builder);
  }

  @Override
  public <FB> void log(
      @NotNull Level level,
      @NotNull Supplier<List<Field>> extraFields,
      @NotNull Condition condition,
      @Nullable String message,
      @NotNull Function<FB, FieldBuilderResult> f,
      @NotNull FB builder) {
    core.log(level, extraFields, condition, message, f, builder);
  }

  @Override
  @NotNull
  public <FB> LoggerHandle<FB> logHandle(@NotNull Level level, @NotNull FB builder) {
    return core.logHandle(level, builder);
  }
}
