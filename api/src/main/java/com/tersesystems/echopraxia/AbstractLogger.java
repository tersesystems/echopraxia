package com.tersesystems.echopraxia;

import com.tersesystems.echopraxia.core.CoreLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractLogger<
        SELF extends AbstractLogger<SELF, FB>, FB extends Field.Builder>
    extends AbstractSelf<SELF> implements LoggerLike<FB> {

  protected final CoreLogger core;
  protected final FB fieldBuilder;

  // https://github.com/assertj/assertj-core/blob/main/src/main/java/org/assertj/core/api/AbstractAssert.java
  protected AbstractLogger(Class<?> selfType, @NotNull CoreLogger core, @NotNull FB fieldBuilder) {
    super(selfType);
    this.core = core;
    this.fieldBuilder = fieldBuilder;
  }

  @Override
  public @NotNull String getName() {
    return core().getName();
  }

  @Override
  public @NotNull CoreLogger core() {
    return core;
  }

  @Override
  public @NotNull FB fieldBuilder() {
    return fieldBuilder;
  }

  /**
   * Creates a new logger with the given condition.
   *
   * <p>Note that the condition is lazily evaluated on every logging statement.
   *
   * @param condition the given condition.
   * @return the new logger.
   */
  @NotNull
  public SELF withCondition(@NotNull Condition condition) {
    if (condition == Condition.always()) {
      return self();
    }
    if (condition == Condition.never()) {
      return neverLogger(core().withCondition(Condition.never()));
    }

    // Reduce allocation if we can help it
    final CoreLogger coreLogger = core().withCondition(condition);
    if (coreLogger == core()) {
      return self();
    }
    return newLogger(coreLogger);
  }

  protected abstract SELF newLogger(CoreLogger coreLogger);

  protected abstract SELF neverLogger(CoreLogger coreLogger);

  /**
   * Creates a new logger with the given context fields.
   *
   * <p>Note that the field builder function is lazily evaluated on every logging statement.
   *
   * @param f the given function producing fields from a field builder.
   * @return the new logger.
   */
  @NotNull
  public SELF withFields(@NotNull Field.BuilderFunction<FB> f) {
    return newLogger(core().withFields(f, fieldBuilder));
  }

  /**
   * Creates a new logger with context fields from thread context / MDC mapped as context fields.
   *
   * <p>Note that the context map is lazily evaluated on every logging statement.
   *
   * @return the new logger.
   */
  @NotNull
  public SELF withThreadContext() {
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
    return newLogger(core().withThreadContext(mapTransform));
  }

  /**
   * Creates a new logger with the given field builder, using reflection.
   *
   * @param newBuilderClass the class of given field builder.
   * @param <T> the type of the field builder.
   * @return a new logger using the given field builder.
   */
  //  @NotNull
  //  public <LOGGER, T extends Field.Builder> LOGGER withFieldBuilder(@NotNull Class<T>
  // newBuilderClass, Class<LOGGER> loggerClass) {
  //    try {
  //      final T newInstance = newBuilderClass.getDeclaredConstructor().newInstance();
  //      return newLogger(core(), newInstance);
  //    } catch (NoSuchMethodException
  //             | SecurityException
  //             | InstantiationException
  //             | IllegalAccessException
  //             | InvocationTargetException e) {
  //      throw new IllegalStateException(e);
  //    }
  //  }

}
