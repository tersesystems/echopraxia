package com.tersesystems.echopraxia.api;

import org.jetbrains.annotations.NotNull;

/**
 * The default field creator.
 *
 * @since 3.0
 */
public class DefaultFieldCreator implements FieldCreator<DefaultField> {
  @Override
  public @NotNull DefaultField create(
      @NotNull String name, @NotNull Value<?> value, @NotNull Attributes attributes) {
    return new DefaultField(name, value, attributes);
  }

  @Override
  public boolean canServe(@NotNull Class<?> t) {
    return t.isAssignableFrom(DefaultField.class);
  }
}
