package echopraxia.fluent;

import echopraxia.api.Field;
import echopraxia.api.FieldBuilderResult;
import echopraxia.logging.api.Condition;
import echopraxia.logging.api.Level;
import echopraxia.logging.spi.AbstractLoggerSupport;
import echopraxia.logging.spi.CoreLogger;
import echopraxia.logging.spi.Utilities;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * FluentLogger class.
 *
 * @param <FB> the field builder type.
 */
public class FluentLogger<FB> extends AbstractLoggerSupport<FluentLogger<FB>, FB> {

  protected FluentLogger(@NotNull CoreLogger core, @NotNull FB fieldBuilder) {
    super(core, fieldBuilder, FluentLogger.class);
  }

  @NotNull
  public FluentLogger<FB> withCondition(@NotNull Condition c) {
    CoreLogger coreLogger = core.withCondition(c);
    return new FluentLogger<>(coreLogger, fieldBuilder);
  }

  @NotNull
  public FluentLogger<FB> withFields(@NotNull Function<FB, FieldBuilderResult> f) {
    CoreLogger coreLogger = core.withFields(f, fieldBuilder);
    return new FluentLogger<>(coreLogger, fieldBuilder);
  }

  @NotNull
  public <T> FluentLogger<T> withFieldBuilder(@NotNull T newBuilder) {
    return new FluentLogger<>(core, newBuilder);
  }

  @NotNull
  public FluentLogger<FB> withThreadContext() {
    CoreLogger coreLogger = core.withThreadContext(Utilities.threadContext());
    return new FluentLogger<>(coreLogger, fieldBuilder);
  }

  @Override
  protected @NotNull FluentLogger<FB> newLogger(CoreLogger core) {
    return new FluentLogger<>(core, fieldBuilder);
  }

  @Override
  protected @NotNull FluentLogger<FB> neverLogger() {
    return new FluentLogger<>(core.withCondition(Condition.never()), fieldBuilder);
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
    private final List<Function<FB, FieldBuilderResult>> argumentFnList = new ArrayList<>();

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
    public EntryBuilder argument(@NotNull Function<FB, FieldBuilderResult> f) {
      this.argumentFnList.add(f);
      return this;
    }

    public void log() {
      core.log(
          level,
          condition,
          message,
          b -> {
            List<Field> list = new ArrayList<>();
            for (Function<FB, FieldBuilderResult> f : argumentFnList) {
              FieldBuilderResult result = f.apply(b);
              list.addAll(result.fields());
            }
            return FieldBuilderResult.list(list);
          },
          fieldBuilder);
    }
  }
}
