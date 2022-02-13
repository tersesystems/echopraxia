package com.tersesystems.echopraxia;

import static com.tersesystems.echopraxia.Level.*;

import com.tersesystems.echopraxia.core.CoreLogger;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An echopraxia logger built around a field builder.
 *
 * <p>This class is explicitly designed to be subclassed so that end users can customize it and
 * avoid the parameterized type tax.
 *
 * <pre>{@code
 * public class MyLogger extends Logger<MyFieldBuilder> {
 *   protected MyLogger(CoreLogger core, MyFieldBuilder fieldBuilder) { super(core, fieldBuilder); }
 * }
 *
 * static class MyLoggerFactory {
 *   public static MyLogger getLogger() { return new MyLogger(CoreLoggerFactory.getLogger(), myFieldBuilder); }
 * }
 *
 * MyLogger logger = MyLoggerFactory.getLogger();
 * }</pre>
 *
 * @param <FB> the field builder type.
 */
public class Logger<FB extends Field.Builder> extends AbstractLogger<FB, Logger<FB>>{

  protected Logger(@NotNull CoreLogger core, @NotNull FB fieldBuilder) {
    super(core, fieldBuilder);
  }

  /**
   * Creates a new logger with the given field builder.
   *
   * @param newBuilder the given field builder.
   * @param <T> the type of the field builder.
   * @return a new logger using the given field builder.
   */
  @Override
  @NotNull
  public <T extends Field.Builder> Logger<T> withFieldBuilder(@NotNull T newBuilder) {
    if (this.fieldBuilder == newBuilder) {
      //noinspection unchecked
      return (Logger<T>) this;
    }
    return new Logger<>(core(), newBuilder);
  }

  /**
   * Creates a new logger with the given field builder, using reflection.
   *
   * @param newBuilderClass the class of given field builder.
   * @param <T> the type of the field builder.
   * @return a new logger using the given field builder.
   */
  @NotNull
  public <T extends FB> Logger<T> withFieldBuilder(@NotNull Class<T> newBuilderClass) {
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

  @Override
  @NotNull
  public Logger<FB> withCondition(@NotNull Condition condition) {
    if (condition == Condition.always()) {
      return this;
    }
    if (condition == Condition.never()) {
      return new NeverLogger<FB>(core().withCondition(Condition.never()), fieldBuilder);
    }

    // Reduce allocation if we can help it
    final CoreLogger coreLogger = core().withCondition(condition);
    if (coreLogger == core()) {
      return this;
    }
    return newLogger(coreLogger);
  }

  // not overridden, not sure if should be part of LoggerLike
  public AsyncLogger<FB> withExecutor(Executor executor) {
    return new AsyncLogger<>(core().withExecutor(executor), fieldBuilder);
  }

  @Override
  @NotNull
  protected Logger<FB> newLogger(CoreLogger coreLogger) {
    return new Logger<>(coreLogger, fieldBuilder);
  }

  static class NeverLogger<FB extends Field.Builder> extends Logger<FB> implements LoggerLike.Never<FB, Logger<FB>> {

    protected NeverLogger(@NotNull CoreLogger core, @NotNull FB fieldBuilder) {
      super(core, fieldBuilder);
    }

    @Override
    public @NotNull String getName() {
      return core.getName();
    }

    @Override
    public @NotNull CoreLogger core() {
      return core;
    }

    @Override
    public @NotNull FB fieldBuilder() {
      return fieldBuilder;
    }

    @Override
    public @NotNull Logger<FB> withThreadContext() {
      return new NeverLogger(core().withThreadContext());
    }

    @Override
    public @NotNull <T extends Field.Builder> Logger<T> withFieldBuilder(@NotNull T newBuilder) {
      return new NeverLogger<>();
    }

    @Override
    public @NotNull Logger<FB> withFields(Field.@NotNull BuilderFunction<FB> f) {
      return this;
    }

    @Override
    public @NotNull Logger<FB> withCondition(@NotNull Condition condition) {
      return this;
    }
  }
}
