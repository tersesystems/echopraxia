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
public class Logger<FB extends Field.Builder> extends AbstractLogger<FB, Logger<FB>> {

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
  public <T extends Field.Builder> Logger<T> withFieldBuilder(@NotNull Class<T> newBuilderClass) {
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

  /**
   * Creates a new logger with the given condition.
   *
   * <p>Note that the condition is lazily evaluated on every logging statement.
   *
   * @param condition the given condition.
   * @return the new logger.
   */
  @Override
  @NotNull
  public Logger<FB> withCondition(@NotNull Condition condition) {
    if (condition == Condition.always()) {
      return this;
    }
    if (condition == Condition.never()) {
      return new NeverLogger<>(core().withCondition(Condition.never()), fieldBuilder);
    }

    // Reduce allocation if we can help it
    final CoreLogger coreLogger = core().withCondition(condition);
    if (coreLogger == core()) {
      return this;
    }
    return new Logger<>(coreLogger, fieldBuilder);
  }

  /**
   * Creates a new logger with the given context fields.
   *
   * <p>Note that the field builder function is lazily evaluated on every logging statement.
   *
   * @param f the given function producing fields from a field builder.
   * @return the new logger.
   */
  @Override
  @NotNull
  public Logger<FB> withFields(@NotNull Field.BuilderFunction<FB> f) {
    return new Logger<>(core().withFields(f, fieldBuilder), fieldBuilder);
  }

  /**
   * Creates a new logger with context fields from thread context / MDC mapped as context fields.
   *
   * <p>Note that the context map is lazily evaluated on every logging statement.
   *
   * @return the new logger.
   */
  @Override
  @NotNull
  public Logger<FB> withThreadContext() {
    Function<Supplier<Map<String, String>>, Supplier<List<Field>>> mapTransform =
        mapSupplier ->
            () -> {
              List<Field> list = new ArrayList<>();
              for (Map.Entry<String, String> e : mapSupplier.get().entrySet()) {
                Field string = fieldBuilder.string(e.getKey(), e.getValue());
                list.add(string);
              }
              return list;
            };
    return new Logger<>(core().withThreadContext(mapTransform), fieldBuilder);
  }

  // not overridden, not sure if should be part of LoggerLike
  public AsyncLogger<FB> withExecutor(Executor executor) {
    return new AsyncLogger<>(core().withExecutor(executor), fieldBuilder);
  }

}
