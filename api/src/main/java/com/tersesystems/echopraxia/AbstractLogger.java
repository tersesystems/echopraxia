package com.tersesystems.echopraxia;

import com.tersesystems.echopraxia.core.CoreLogger;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractLogger<FB extends Field.Builder> implements LoggerLike<FB> {
  protected final CoreLogger core;
  protected final FB fieldBuilder;

  // https://github.com/assertj/assertj-core/blob/main/src/main/java/org/assertj/core/api/AbstractAssert.java
  protected AbstractLogger(@NotNull CoreLogger core, @NotNull FB fieldBuilder) {
    this.core = core;
    this.fieldBuilder = fieldBuilder;
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

  @NotNull
  abstract SELF withThreadContext();

  @NotNull
  abstract SELF withFields(@NotNull Field.BuilderFunction<FB> f);

  @NotNull
  abstract SELF withCondition(@NotNull Condition condition);

  @NotNull
  abstract <T extends Field.Builder> SELF<T> withFieldBuilder(@NotNull T newBuilder);

}
