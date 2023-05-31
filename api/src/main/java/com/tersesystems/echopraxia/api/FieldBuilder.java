package com.tersesystems.echopraxia.api;

import static com.tersesystems.echopraxia.api.FieldConstants.EXCEPTION;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface FieldBuilder {

  static @NotNull FieldBuilder instance() {
    return FieldBuilderInstance.getInstance();
  }

  /**
   * Wraps fields in a result.
   *
   * <p>{@code fb.list( fb.string("correlation_id", "match"), fb.number("some_number", 12345) )}
   *
   * @param fields the given fields
   * @return a list containing the fields.
   */
  @NotNull
  default FieldBuilderResult list(Field... fields) {
    return FieldBuilderResult.list(fields);
  }

  /**
   * Creates a field that renders in message template as value.
   *
   * <p>This method is intentionally value blind so any value, including null value, will work here.
   *
   * @param name the field name
   * @param value the field value
   * @return the field.
   */
  @NotNull
  default Field value(@NotNull String name, @NotNull Value<?> value) {
    return Field.value(name, value);
  }

  @NotNull
  default <F extends Field> F value(
      @NotNull String name, @NotNull Value<?> value, Class<F> fieldClass) {
    return Field.value(name, value, fieldClass);
  }

  /**
   * Creates a key value field that renders in message template as key=value.
   *
   * <p>This method is intentionally value blind so any value, including null value, will work here.
   *
   * @param name the field name
   * @param value the field value
   * @return the field.
   */
  @NotNull
  default Field keyValue(@NotNull String name, @NotNull Value<?> value) {
    return Field.keyValue(name, value);
  }

  @NotNull
  default <F extends Field> F keyValue(
      @NotNull String name, @NotNull Value<?> value, Class<F> clazz) {
    return Field.keyValue(name, value, clazz);
  }

  // ---------------------------------------------------------
  // String

  /**
   * Creates a field out of a name and a raw string value.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return the field.
   */
  @NotNull
  default Field string(@NotNull String name, @NotNull String value) {
    return keyValue(name, Value.string(value));
  }

  /**
   * Creates a field out of a name and a string value.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return the field.
   */
  @NotNull
  default Field string(@NotNull String name, @NotNull Value.StringValue value) {
    return keyValue(name, value);
  }

  // ---------------------------------------------------------
  // Number

  /**
   * Creates a field out of a name and a raw number value.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default Field number(@NotNull String name, @NotNull Byte value) {
    return keyValue(name, Value.number(value));
  }

  default Field number(@NotNull String name, @NotNull Short value) {
    return keyValue(name, Value.number(value));
  }

  default Field number(@NotNull String name, @NotNull Integer value) {
    return keyValue(name, Value.number(value));
  }

  default Field number(@NotNull String name, @NotNull Long value) {
    return keyValue(name, Value.number(value));
  }

  default Field number(@NotNull String name, @NotNull Float value) {
    return keyValue(name, Value.number(value));
  }

  default Field number(@NotNull String name, @NotNull Double value) {
    return keyValue(name, Value.number(value));
  }

  default Field number(@NotNull String name, @NotNull BigInteger value) {
    return keyValue(name, Value.number(value));
  }

  default Field number(@NotNull String name, @NotNull BigDecimal value) {
    return keyValue(name, Value.number(value));
  }

  /**
   * Creates a field out of a name and a number value.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return a list containing a single field.
   * @param <N> the type of number
   */
  @NotNull
  default <N extends Number & Comparable<N>> Field number(
      @NotNull String name, @NotNull Value.NumberValue<N> value) {
    return keyValue(name, value);
  }

  // ---------------------------------------------------------
  // Boolean

  /**
   * Creates a field out of a name and a raw boolean value.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default Field bool(@NotNull String name, @NotNull Boolean value) {
    return keyValue(name, Value.bool(value));
  }

  /**
   * Creates a field out of a name and a boolean value.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default Field bool(@NotNull String name, @NotNull Value.BooleanValue value) {
    return keyValue(name, value);
  }

  // ---------------------------------------------------------
  // Array

  /**
   * Creates a field out of a name and a list of object values.
   *
   * @param name the name of the field.
   * @param values the array of values.
   * @return a list containing a single field.
   */
  @NotNull
  default Field array(@NotNull String name, @NotNull Value.ObjectValue... values) {
    return keyValue(name, Value.array(values));
  }

  /**
   * Creates a field out of a name and string array values.
   *
   * @param name the name of the field.
   * @param values the array of values.
   * @return a list containing a single field.
   */
  @NotNull
  default Field array(@NotNull String name, String... values) {
    return keyValue(name, Value.array(values));
  }

  /**
   * Creates a field out of a name and number array values.
   *
   * @param name the name of the field.
   * @param values the array of values.
   * @return a list containing a single field.
   */
  @NotNull
  default Field array(@NotNull String name, Byte... values) {
    return keyValue(name, Value.array(values));
  }

  default Field array(@NotNull String name, Short... values) {
    return keyValue(name, Value.array(values));
  }

  default Field array(@NotNull String name, Integer... values) {
    return keyValue(name, Value.array(values));
  }

  default Field array(@NotNull String name, Long... values) {
    return keyValue(name, Value.array(values));
  }

  default Field array(@NotNull String name, Double... values) {
    return keyValue(name, Value.array(values));
  }

  default Field array(@NotNull String name, Float... values) {
    return keyValue(name, Value.array(values));
  }

  default Field array(@NotNull String name, BigInteger... values) {
    return keyValue(name, Value.array(values));
  }

  default Field array(@NotNull String name, BigDecimal... values) {
    return keyValue(name, Value.array(values));
  }

  /**
   * Creates a field out of a name and boolean array values.
   *
   * @param name the name of the field.
   * @param values the array of values.
   * @return a list containing a single field.
   */
  @NotNull
  default Field array(@NotNull String name, Boolean... values) {
    return keyValue(name, Value.array(values));
  }

  /**
   * Takes an array value as an array field. This is good for hetrogenous elements.
   *
   * <p>{@code array(name, Value.array(1, "a", true))}
   *
   * @param name the name of the field.
   * @param value the array value.
   * @return a list containing a single field.
   */
  @NotNull
  default Field array(@NotNull String name, @NotNull Value.ArrayValue value) {
    // Don't allow Value.ArrayValue... it's far too easy to double nest an array.
    return keyValue(name, value);
  }

  // ---------------------------------------------------------
  // Object

  /**
   * Creates a field object out of a name and field values.
   *
   * @param name the name of the field.
   * @param values the values.
   * @return a single field.
   */
  @NotNull
  default Field object(@NotNull String name, Field... values) {
    return keyValue(name, Value.object(values));
  }

  /**
   * Creates a field object out of a name and field values.
   *
   * @param name the name of the field.
   * @param values the values.
   * @return a field.
   */
  @NotNull
  default Field object(@NotNull String name, @NotNull List<Field> values) {
    return keyValue(name, Value.object(values));
  }

  /**
   * Creates a field object out of a name and a value.
   *
   * @param name the name of the field.
   * @param value the value.
   * @return a field.
   */
  @NotNull
  default Field object(@NotNull String name, @NotNull Value.ObjectValue value) {
    // limited to object specifically -- if you want object or null,
    // use `value` or `keyValue`
    return keyValue(name, value);
  }

  // ---------------------------------------------------------
  // Exception

  /**
   * Creates a field from an exception, using the default exception name "exception".
   *
   * @param t the exception.
   * @return a field.
   */
  @NotNull
  default Field exception(@NotNull Throwable t) {
    return keyValue(EXCEPTION, Value.exception(t));
  }

  /**
   * Creates a field from an exception value, using the default exception name "exception".
   *
   * @param value the exception value.
   * @return a field.
   */
  @NotNull
  default Field exception(@NotNull Value.ExceptionValue value) {
    return keyValue(EXCEPTION, value);
  }

  /**
   * Creates a field from an exception, using an explicit name.
   *
   * @param name the field name
   * @param t the exception.
   * @return a field.
   */
  @NotNull
  default Field exception(@NotNull String name, @NotNull Throwable t) {
    return keyValue(name, Value.exception(t));
  }

  /**
   * Creates a field from an exception value, using an explicit name.
   *
   * @param name the field name
   * @param value the exception value.
   * @return a field.
   */
  @NotNull
  default Field exception(@NotNull String name, @NotNull Value.ExceptionValue value) {
    return keyValue(name, value);
  }

  // ---------------------------------------------------------
  // Null

  /**
   * Creates a field with a null as a value.
   *
   * @param name the name of the field.
   * @return a field.
   */
  @NotNull
  default Field nullField(@NotNull String name) {
    return keyValue(name, Value.nullValue());
  }
}

// internal class so interface doesn't have to expose it
class FieldBuilderInstance {
  private static final FieldBuilder instance = new FieldBuilder() {};

  static FieldBuilder getInstance() {
    return instance;
  }
}
