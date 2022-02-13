package com.tersesystems.echopraxia;

import com.tersesystems.echopraxia.core.CoreLogger;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class CustomLogger<FB extends Field.Builder> extends AbstractLogger<FB, CustomLogger<FB>> {

  protected CustomLogger(@NotNull CoreLogger core, @NotNull FB fieldBuilder) {
    super(core, fieldBuilder);
  }

  @Override
  @NotNull
  public <T extends Field.Builder> CustomLogger<T> withFieldBuilder(@NotNull T newBuilder) {
    if (this.fieldBuilder == newBuilder) {
      //noinspection unchecked
      return (CustomLogger<T>) this;
    }
    return new CustomLogger<>(core(), newBuilder);
  }

  /**
   * Creates a new logger with the given field builder, using reflection.
   *
   * @param newBuilderClass the class of given field builder.
   * @param <T> the type of the field builder.
   * @return a new logger using the given field builder.
   */
  @NotNull
  public <T extends Field.Builder> CustomLogger<T> withFieldBuilder(@NotNull Class<T> newBuilderClass) {
    try {
      final T newInstance = newBuilderClass.getDeclaredConstructor().newInstance();
      return new CustomLogger<>(core(), newInstance);
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
  public CustomLogger<FB> withCondition(@NotNull Condition condition) {
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
    return newLogger(coreLogger);
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
  public CustomLogger<FB> withFields(@NotNull Field.BuilderFunction<FB> f) {
    return newLogger(core().withFields(f, fieldBuilder));
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
  public CustomLogger<FB> withThreadContext() {
    return super.withThreadContext();
  }

  @Override
  @NotNull
  protected CustomLogger<FB> newLogger(CoreLogger coreLogger) {
    return new CustomLogger<>(coreLogger, fieldBuilder);
  }

}
