package com.tersesystems.echopraxia.fluent;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Level;
import com.tersesystems.echopraxia.core.CoreLogger;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * FluentLogger class.
 *
 * @param <FB> the field builder type.
 */
public class FluentLogger<FB extends Field.Builder> {

  private final CoreLogger core;
  private final FB builder;

  protected FluentLogger(CoreLogger core, FB builder) {
    this.core = core;
    this.builder = builder;
  }

  @NotNull
  public CoreLogger core() {
    return core;
  }

  @NotNull
  public FB fieldBuilder() {
    return builder;
  }

  @NotNull
  public FluentLogger<FB> withCondition(@NotNull Condition c) {
    CoreLogger coreLogger = core.withCondition(c);
    return new FluentLogger<>(coreLogger, builder);
  }

  @NotNull
  public FluentLogger<FB> withFields(@NotNull Field.BuilderFunction<FB> f) {
    CoreLogger coreLogger = core.withFields(f, builder);
    return new FluentLogger<>(coreLogger, builder);
  }

  @NotNull
  public <T extends Field.Builder> FluentLogger<T> withFieldBuilder(@NotNull T newBuilder) {
    return new FluentLogger<>(core, newBuilder);
  }

  @NotNull
  public <T extends Field.Builder> FluentLogger<T> withFieldBuilder(
      @NotNull Class<T> newBuilderClass) {
    try {
      final T newInstance = newBuilderClass.getDeclaredConstructor().newInstance();
      return new FluentLogger<>(core, newInstance);
    } catch (NoSuchMethodException
        | SecurityException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  @NotNull
  public FluentLogger<FB> withThreadContext() {
    Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform =
        mapSupplier ->
            () ->
                mapSupplier.get().entrySet().stream()
                    .map(e -> builder.string(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
    CoreLogger coreLogger = core.withThreadContext(mapTransform);
    return new FluentLogger<>(coreLogger, builder);
  }

  public boolean isErrorEnabled() {
    return core.isEnabled(Level.ERROR);
  }

  public boolean isErrorEnabled(Condition condition) {
    return core.isEnabled(Level.ERROR, condition);
  }

  public boolean isWarnEnabled() {
    return core.isEnabled(Level.WARN);
  }

  public boolean isWarnEnabled(Condition condition) {
    return core.isEnabled(Level.WARN, condition);
  }

  public boolean isInfoEnabled() {
    return core.isEnabled(Level.INFO);
  }

  public boolean isInfoEnabled(Condition condition) {
    return core.isEnabled(Level.INFO, condition);
  }

  public boolean isDebugEnabled() {
    return core.isEnabled(Level.DEBUG);
  }

  public boolean isDebugEnabled(Condition condition) {
    return core.isEnabled(Level.DEBUG, condition);
  }

  public boolean isTraceEnabled() {
    return core.isEnabled(Level.TRACE);
  }

  public boolean isTraceEnabled(Condition condition) {
    return core.isEnabled(Level.TRACE, condition);
  }

  public boolean isEnabled(Level level) {
    return core.isEnabled(level);
  }

  public boolean isEnabled(Level level, Condition condition) {
    return core.isEnabled(level, condition);
  }

  @NotNull
  public EntryBuilder atError() {
    return atLevel(Level.ERROR);
  }

  @NotNull
  public EntryBuilder atWarn() {
    return atLevel(Level.WARN);
  }

  @NotNull
  public EntryBuilder atInfo() {
    return atLevel(Level.INFO);
  }

  @NotNull
  public EntryBuilder atDebug() {
    return atLevel(Level.DEBUG);
  }

  @NotNull
  public EntryBuilder atTrace() {
    return atLevel(Level.TRACE);
  }

  @NotNull
  public EntryBuilder atLevel(Level level) {
    return new EntryBuilder(level);
  }

  public class EntryBuilder {
    private final Level level;
    private String message;
    private Condition condition = Condition.always();
    private final List<Function<FB, Field>> argumentFnList = new ArrayList<>();

    EntryBuilder(Level level) {
      this.level = level;
    }

    @NotNull
    public EntryBuilder condition(@NotNull Condition condition) {
      this.condition = this.condition.and(condition);
      return this;
    }

    @NotNull
    public EntryBuilder message(@Nullable String message) {
      this.message = message;
      return this;
    }

    @NotNull
    public EntryBuilder argument(@NotNull Function<FB, Field> f) {
      this.argumentFnList.add(f);
      return this;
    }

    @NotNull
    public EntryBuilder exception(@NotNull Throwable t) {
      return argument(b -> b.exception(t));
    }

    public void log() {
      core.log(
          level,
          condition,
          message,
          b -> argumentFnList.stream().map(f -> f.apply(b)).collect(Collectors.toList()),
          builder);
    }
  }
}
