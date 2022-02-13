package com.tersesystems.echopraxia;

import com.tersesystems.echopraxia.core.CoreLogger;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;
import org.jetbrains.annotations.NotNull;

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
public class Logger<FB extends Field.Builder> extends AbstractLogger<Logger<FB>, FB> {

  protected Logger(@NotNull CoreLogger core, @NotNull FB fieldBuilder) {
    super(Logger.class, core, fieldBuilder);
  }

  /**
   * Creates a new logger with the given field builder.
   *
   * @param newBuilder the given field builder.
   * @param <T> the type of the field builder.
   * @return a new logger using the given field builder.
   */
  @NotNull
  public <T extends Field.Builder> Logger<T> withFieldBuilder(@NotNull T newBuilder) {
    if (this.fieldBuilder == newBuilder) {
      //noinspection unchecked
      return (Logger<T>) this;
    }
    return newLogger(core(), newBuilder);
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
      final T newFieldBuilder = newBuilderClass.getDeclaredConstructor().newInstance();
      return new Logger<>(core(), newFieldBuilder);
    } catch (NoSuchMethodException
        | SecurityException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  // not overridden, not sure if should be part of LoggerLike
  public AsyncLogger<FB> withExecutor(Executor executor) {
    return new AsyncLogger<>(core().withExecutor(executor), fieldBuilder);
  }

  private <T extends Field.Builder> Logger<T> newLogger(CoreLogger coreLogger, T newBuilder) {
    return new Logger<>(coreLogger, newBuilder);
  }

  @Override
  protected Logger<FB> newLogger(CoreLogger coreLogger) {
    return new Logger<>(coreLogger, fieldBuilder);
  }

  @Override
  protected Logger<FB> neverLogger(CoreLogger coreLogger) {
    return new Logger<>(coreLogger, fieldBuilder);
  }
}
