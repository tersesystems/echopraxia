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
   * @return a field with the given name and value, displayed as value only.
   */
  @NotNull
  static Field value(@NotNull String name, @NotNull Value<?> value) {
    return EchopraxiaService.getInstance().getFieldCreator(DefaultField.class).value(name, value);
  }

  /**
   * @return a field with the given name and value, displayed as value only.
   */
  @NotNull
  static <F extends Field> F value(
      @NotNull String name, @NotNull Value<?> value, Class<F> fieldClass) {
    return EchopraxiaService.getInstance().getFieldCreator(fieldClass).value(name, value);
  }

  /**
   * @return a field with the given name and value displayed as key=value
   */
  @NotNull
  static Field keyValue(@NotNull String name, @NotNull Value<?> value) {
    return EchopraxiaService.getInstance()
        .getFieldCreator(DefaultField.class)
        .keyValue(name, value);
  }

  static <F extends Field> F keyValue(
      @NotNull String name, @NotNull Value<?> value, Class<F> fieldClass) {
    return EchopraxiaService.getInstance().getFieldCreator(fieldClass).keyValue(name, value);
  }

  // construct a field name so that json is happy and keep going.
  public static String requireName(String name) {
    if (name != null) {
      return name;
    }
    unknownFieldAdder.increment();
    return ECHOPRAXIA_UNKNOWN + unknownFieldAdder.longValue();
  }

  public static Value<?> requireValue(Value<?> value) {
    if (value != null) {
      return value;
    }
    return Value.nullValue();
  }

  public static final String ECHOPRAXIA_UNKNOWN = "echopraxia-unknown-";
  public static final LongAdder unknownFieldAdder = new LongAdder();
}
