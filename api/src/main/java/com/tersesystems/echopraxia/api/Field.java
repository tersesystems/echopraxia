package com.tersesystems.echopraxia.api;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/**
 * The Field interface. This is a core part of structured data, and consists of a name and a Value,
 * where a value corresponds roughly to the JSON infoset: string, number, boolean, null, array, and
 * object.
 *
 * <p>The field builder interface and custom field builders go a long way to building up more
 * complex structures, please see documentation for how to use them.
 */
public interface Field extends FieldBuilderResult, FieldAttributesAware {

  /**
   * The field name.
   *
   * @return the field name.
   */
  @NotNull
  String name();

  /**
   * The field value.
   *
   * @return the field value.
   */
  @NotNull
  Value<?> value();

  /** Attributes */
  @NotNull
  Attributes attributes();

  /**
   *
   */
  <A> Field withAttribute(Attribute<A> attr);

  /**
   *
   */
  Field withAttributes(Attributes attrs);


  /**
   *
   */
  <A> Field withoutAttribute(AttributeKey<A> key);

  /**
   *
   */
  Field withoutAttributes(Collection<AttributeKey<?>> keys);

  /**
   *
   */
  Field clearAttributes();

  @NotNull
  static Field value(@NotNull String name, @NotNull Value<?> value) {
    return new DefaultField(name, value, FieldAttributes.valueOnly());
  }

  @NotNull
  static Field keyValue(@NotNull String name, @NotNull Value<?> value) {
    return new DefaultField(name, value, Attributes.empty());
  }

}
