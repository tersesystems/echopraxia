package com.tersesystems.echopraxia.api;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/**
 * The Field interface. This is a core part of structured data, and consists of a name and a Value,
 * where a value corresponds roughly to the JSON infoset: string, number, boolean, null, array, and
 * object.
 *
 * <p>The attributes in the field are used to determine additional metadata and details on how to
 * render the field.  Fields are immutable, and so adding and removing attributes creates a new
 * field and does not modify the existing field.
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

  /**
   * The attributes for this field.
   *
   * @return the attributes for this field.
   */
  @NotNull
  Attributes attributes();

  /**
   * @return a field with the given attribute added.
   */
  @NotNull
  <A> Field withAttribute(@NotNull Attribute<A> attr);

  /**
   * @return a field with the given attributes added.
   */
  @NotNull
  Field withAttributes(@NotNull Attributes attrs);


  /**
   * @return a field without the attribute with the given key.
   */
  @NotNull
  <A> Field withoutAttribute(@NotNull AttributeKey<A> key);

  /**
   * @return a field without the attributes with the given keys.
   */
   @NotNull
  Field withoutAttributes(@NotNull Collection<AttributeKey<?>> keys);

  /**
   * @return a field without no attributes set.
   */
  @NotNull
  Field clearAttributes();

  /**
   * @return a field with the given name and value, with the VALUE_ONLY attribute set.
   */
  @NotNull
  static Field value(@NotNull String name, @NotNull Value<?> value) {
    return new DefaultField(name, value, FieldAttributes.valueOnly());
  }

  /**
   * @return a field with the given name and value and no attributes set.
   */
  @NotNull
  static Field keyValue(@NotNull String name, @NotNull Value<?> value) {
    return new DefaultField(name, value, Attributes.empty());
  }

}
