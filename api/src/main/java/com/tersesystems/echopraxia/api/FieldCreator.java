package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

public interface FieldCreator<F extends Field> {

  @NotNull
  F create(@NotNull String name, @NotNull Value<?> value, @NotNull Attributes attributes);

  boolean canServe(@NotNull Class<?> t);
}
