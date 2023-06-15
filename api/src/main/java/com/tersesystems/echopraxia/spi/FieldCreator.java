package com.tersesystems.echopraxia.spi;

import com.tersesystems.echopraxia.api.Attributes;
import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.Value;
import org.jetbrains.annotations.NotNull;

public interface FieldCreator<F extends Field> {

  @NotNull
  F create(@NotNull String name, @NotNull Value<?> value, @NotNull Attributes attributes);

  boolean canServe(@NotNull Class<?> t);
}
