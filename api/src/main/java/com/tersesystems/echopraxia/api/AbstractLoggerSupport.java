package com.tersesystems.echopraxia.api;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * An abstract class that implements the support methods and safe self-typed return methods.
 *
 * @param <SELF> the actual type of the logger.
 * @param <FB> the field builder.
 */
public abstract class AbstractLoggerSupport<
        SELF extends AbstractLoggerSupport<SELF, FB>, FB>
    implements DefaultMethodsSupport<FB> {

  protected final CoreLogger core;
  protected final FB fieldBuilder;
  private final SELF myself;

  @SuppressWarnings("unchecked")
  protected AbstractLoggerSupport(
      @NotNull CoreLogger core, @NotNull FB fieldBuilder, Class<?> selfType) {
    myself = (SELF) selfType.cast(this);
    this.core = core;
    this.fieldBuilder = fieldBuilder;
  }

  @Override
  public @NotNull String getName() {
    return core.getName();
  }

  @Override
  @NotNull
  public CoreLogger core() {
    return core;
  }

  @Override
  @NotNull
  public FB fieldBuilder() {
    return fieldBuilder;
  }

  @NotNull
  public SELF withCondition(@NotNull Condition condition) {
    if (condition == Condition.always()) {
      return self();
    } else if (condition == Condition.never()) {
      return neverLogger();
    } else {
      return newLogger(core().withCondition(condition));
    }
  }

  @NotNull
  public SELF withFields(@NotNull Function<FB, FieldBuilderResult> f) {
    return newLogger(core().withFields(f, fieldBuilder));
  }

  @NotNull
  public SELF withThreadContext() {
    return newLogger(core().withThreadContext(Utilities.threadContext()));
  }

  /**
   * The instantiation of the new logger.
   *
   * @param core the core logger.
   * @return a new instance of SELF.
   */
  @NotNull
  protected abstract SELF newLogger(CoreLogger core);

  @NotNull
  protected abstract SELF neverLogger();

  protected SELF self() {
    return myself;
  }
}