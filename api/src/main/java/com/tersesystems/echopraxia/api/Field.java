package com.tersesystems.echopraxia.api;

import java.util.Collection;
import java.util.concurrent.atomic.LongAdder;
import org.jetbrains.annotations.NotNull;

/**
 * The Field interface. This is a core part of structured data, and consists of a name and a Value,
 * where a value corresponds roughly to the JSON infoset: string, number, boolean, null, array, and
 * object.
 *
 * <p>The attributes in the field are used to determine additional metadata and details on how to
 * render the field. Fields are immutable, and so adding and removing attributes creates a new field
 * and does not modify the existing field.
 *
 * <p>The field builder interface and custom field builders go a long way to building up more
 * complex structures, please see documentation for how to use them.
 */
public interface Field extends FieldBuilderResult {

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
   * @since 3.0
   */
  @NotNull
  Attributes attributes();

  /**
   * @return a field with the given attribute added.
   * @since 3.0
   */
  @NotNull
  <A> Field withAttribute(@NotNull Attribute<A> attr);

  /**
   * @return a field with the given attributes added.
   * @since 3.0
   */
  @NotNull
  Field withAttributes(@NotNull Attributes attrs);

  /**
   * @return a field without the attribute with the given key.
   * @since 3.0
   */
  @NotNull
  <A> Field withoutAttribute(@NotNull AttributeKey<A> key);

  /**
   * @return a field without the attributes with the given keys.
   * @since 3.0
   */
  @NotNull
  Field withoutAttributes(@NotNull Collection<AttributeKey<?>> keys);

  /**
   * @return a field without no attributes set.
   * @since 3.0
   */
  @NotNull
  Field clearAttributes();

  /**
   * Creates a value only field exposing only the Field interface.
   *
   * @return a field with the given name and value, displayed as value only.
   */
  @NotNull
  static Field value(@NotNull String name, @NotNull Value<?> value) {
    return value(name, value, DefaultField.class);
  }

  /**
   * Creates a value only field using the fieldClass as the returned type
   *
   * @return a field with the given name and value, displayed as value only.
   * @since 3.0
   */
  @NotNull
  static <F extends Field> F value(
      @NotNull String name, @NotNull Value<?> value, Class<F> fieldClass) {
    return EchopraxiaService.getInstance()
        .getFieldCreator(fieldClass)
        .create(name, value, FieldAttributes.valueOnlyAttributes());
  }

  /**
   * Creates a field exposing only the Field interface.
   *
   * @return a field with the given name and value displayed as key=value
   */
  @NotNull
  static Field keyValue(@NotNull String name, @NotNull Value<?> value) {
    return keyValue(name, value, DefaultField.class);
  }

  /**
   * Creates a field using the fieldClass as the returned type.
   *
   * @return a field with the given name and value displayed as key=value
   * @since 3.0
   */
  static <F extends Field> F keyValue(
      @NotNull String name, @NotNull Value<?> value, Class<F> fieldClass) {
    return EchopraxiaService.getInstance()
        .getFieldCreator(fieldClass)
        .create(name, value, Attributes.empty());
  }

  // construct a field name so that json is happy and keep going.
  static String requireName(String name) {
    if (name != null) {
      return name;
    }
    unknownFieldAdder.increment();
    return ECHOPRAXIA_UNKNOWN + unknownFieldAdder.longValue();
  }

  static Value<?> requireValue(Value<?> value) {
    if (value != null) {
      return value;
    }
    return Value.nullValue();
  }

  String ECHOPRAXIA_UNKNOWN = "echopraxia-unknown-";
  LongAdder unknownFieldAdder = new LongAdder();
}
