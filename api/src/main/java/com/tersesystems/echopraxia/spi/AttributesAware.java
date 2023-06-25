package com.tersesystems.echopraxia.spi;

import com.tersesystems.echopraxia.api.Attribute;
import com.tersesystems.echopraxia.api.AttributeKey;
import com.tersesystems.echopraxia.api.Attributes;
import com.tersesystems.echopraxia.api.Field;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public interface AttributesAware<F extends Field> {

  /**
   * @return a field with the given attribute added.
   * @since 3.0
   */
  @NotNull
  <A> F withAttribute(@NotNull Attribute<A> attr);

  /**
   * @return a field with the given attributes added.
   * @since 3.0
   */
  @NotNull
  F withAttributes(@NotNull Attributes attrs);

  /**
   * @return a field without the attribute with the given key.
   * @since 3.0
   */
  @NotNull
  <A> F withoutAttribute(@NotNull AttributeKey<A> key);

  /**
   * @return a field without the attributes with the given keys.
   * @since 3.0
   */
  @NotNull
  F withoutAttributes(@NotNull Collection<AttributeKey<?>> keys);

  /**
   * @return a field without no attributes set.
   * @since 3.0
   */
  @NotNull
  F clearAttributes();
}
