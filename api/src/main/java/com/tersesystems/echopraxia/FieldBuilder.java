package com.tersesystems.echopraxia;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FieldBuilder {

  static @NotNull FieldBuilder instance() {
    return FieldBuilderInstance.getInstance();
  }

  /**
   * Wraps a single field in a singleton list.
   *
   * <p>{@code fb.only(fb.string("correlation_id", "match"))}
   *
   * @param field the given field, can be null in which case an empty list is returned.
   * @return a list containing a single field element.
   */
  @NotNull
  default List<Field> only(@Nullable Field field) {
    return (field == null) ? emptyList() : singletonList(field);
  }

  /**
   * Wraps fields in a list. More convenient than {@code Arrays.asList}.
   *
   * <p>{@code fb.list( fb.string("correlation_id", "match"), fb.number("some_number", 12345) )}
   *
   * @param fields the given fields
   * @return a list containing the fields.
   */
  @NotNull
  default List<Field> list(Field... fields) {
    return Arrays.asList(fields);
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
  default ValueField value(@NotNull String name, @NotNull Field.Value<?> value) {
    return ValueField.create(name, value);
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
  default KeyValueField keyValue(@NotNull String name, @NotNull Field.Value<?> value) {
    return KeyValueField.create(name, value);
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
    return value(name, Field.Value.string(value));
  }

  /**
   * Creates a field out of a name and a string value.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return the field.
   */
  @NotNull
  default Field string(@NotNull String name, @NotNull Field.Value.StringValue value) {
    return value(name, value);
  }

  /**
   * Creates a list of fields out of a name and a raw string value.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default List<Field> onlyString(@NotNull String name, @NotNull String value) {
    return only(string(name, value));
  }

  /**
   * Creates a list of fields out of a name and a string value.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default List<Field> onlyString(@NotNull String name, @NotNull Field.Value.StringValue value) {
    return only(string(name, value));
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
  default Field number(@NotNull String name, @NotNull Number value) {
    return value(name, Field.Value.number(value));
  }

  /**
   * Creates a field out of a name and a number value.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default Field number(@NotNull String name, @NotNull Field.Value.NumberValue value) {
    return value(name, value);
  }

  /**
   * Creates a singleton list of fields out of a name and a raw number value.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default List<Field> onlyNumber(@NotNull String name, @NotNull Number value) {
    return only(number(name, value));
  }

  /**
   * Creates a singleton list of fields out of a name and a number value.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default List<Field> onlyNumber(@NotNull String name, @NotNull Field.Value.NumberValue value) {
    return only(number(name, value));
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
    return value(name, Field.Value.bool(value));
  }

  /**
   * Creates a field out of a name and a boolean value.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default Field bool(@NotNull String name, @NotNull Field.Value.BooleanValue value) {
    return value(name, value);
  }

  /**
   * Creates a singleton list of fields out of a name and a raw boolean value.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default List<Field> onlyBool(@NotNull String name, @NotNull Boolean value) {
    return only(bool(name, value));
  }

  /**
   * Creates a singleton list of fields out of a name and a boolean value.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default List<Field> onlyBool(@NotNull String name, @NotNull Field.Value.BooleanValue value) {
    return only(bool(name, value));
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
  default Field array(@NotNull String name, @NotNull Field.Value.ObjectValue... values) {
    return keyValue(name, Field.Value.array(values));
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
    return keyValue(name, Field.Value.array(values));
  }

  /**
   * Creates a field out of a name and number array values.
   *
   * @param name the name of the field.
   * @param values the array of values.
   * @return a list containing a single field.
   */
  @NotNull
  default Field array(@NotNull String name, Number... values) {
    return keyValue(name, Field.Value.array(values));
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
    return keyValue(name, Field.Value.array(values));
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
  default Field array(@NotNull String name, @NotNull Field.Value.ArrayValue value) {
    // Don't allow Value.ArrayValue... it's far too easy to double nest an array.
    return keyValue(name, value);
  }

  /**
   * Creates a singleton list of an array field out of a name and object values.
   *
   * @param name the name of the field.
   * @param values the values.
   * @return a list containing a single field.
   */
  @NotNull
  default List<Field> onlyArray(@NotNull String name, @NotNull Field.Value.ObjectValue... values) {
    return only(array(name, values));
  }

  /**
   * Creates a singleton list of an array field out of a name and a variadic array of string.
   *
   * @param name the name of the field.
   * @param values the values.
   * @return a list containing a single field.
   */
  @NotNull
  default List<Field> onlyArray(@NotNull String name, String... values) {
    return only(array(name, values));
  }

  /**
   * Creates a singleton list of an array field out of a name and a variadic array of number.
   *
   * @param name the name of the field.
   * @param values the values.
   * @return a list containing a single field.
   */
  @NotNull
  default List<Field> onlyArray(@NotNull String name, Number... values) {
    return only(array(name, values));
  }

  /**
   * Creates a singleton list of an array field out of a name and a variadic array of boolean.
   *
   * @param name the name of the field.
   * @param values the values.
   * @return a list containing a single field.
   */
  @NotNull
  default List<Field> onlyArray(@NotNull String name, Boolean... values) {
    return only(array(name, values));
  }

  /**
   * Creates a singleton list of an array field out of a name and an array value.
   *
   * <p>{@code onlyArray(name, Value.array(1, "a", true))}
   *
   * @param name the name of the field.
   * @param value the array value.
   * @return a list containing a single field.
   */
  @NotNull
  default List<Field> onlyArray(@NotNull String name, @NotNull Field.Value.ArrayValue value) {
    return only(array(name, value));
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
    return keyValue(name, Field.Value.object(values));
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
    return keyValue(name, Field.Value.object(values));
  }

  /**
   * Creates a field object out of a name and a value.
   *
   * @param name the name of the field.
   * @param value the value.
   * @return a field.
   */
  @NotNull
  default Field object(@NotNull String name, @NotNull Field.Value.ObjectValue value) {
    // limited to object specifically -- if you want object or null,
    // use `value` or `keyValue`
    return keyValue(name, value);
  }

  /**
   * Creates a singleton list of an object out of a name and array values.
   *
   * @param name the name of the field.
   * @param values the values.
   * @return a list containing a single field.
   */
  @NotNull
  default List<Field> onlyObject(@NotNull String name, @NotNull Field... values) {
    return only(object(name, values));
  }

  /**
   * Creates a singleton list of an object out of a name and a list of values.
   *
   * @param name the name of the field.
   * @param values the values.
   * @return a list containing a single field.
   */
  @NotNull
  default List<Field> onlyObject(@NotNull String name, @NotNull List<Field> values) {
    return only(object(name, values));
  }

  /**
   * Creates a singleton list of an object out of a name and an object value.
   *
   * @param name the name of the field.
   * @param value the object values.
   * @return a list containing a single field.
   */
  @NotNull
  default List<Field> onlyObject(@NotNull String name, @NotNull Field.Value.ObjectValue value) {
    return only(object(name, value));
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
    return keyValue(Field.EXCEPTION, Field.Value.exception(t));
  }

  /**
   * Creates a field from an exception value, using the default exception name "exception".
   *
   * @param value the exception value.
   * @return a field.
   */
  @NotNull
  default Field exception(@NotNull Field.Value.ExceptionValue value) {
    return keyValue(Field.EXCEPTION, value);
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
    return keyValue(name, Field.Value.exception(t));
  }

  /**
   * Creates a field from an exception value, using an explicit name.
   *
   * @param name the field name
   * @param value the exception value.
   * @return a field.
   */
  @NotNull
  default Field exception(@NotNull String name, @NotNull Field.Value.ExceptionValue value) {
    return keyValue(name, value);
  }

  /**
   * Creates a singleton list of an exception using the default exception name.
   *
   * @param t the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default List<Field> onlyException(@NotNull Throwable t) {
    return only(exception(t));
  }

  // ---------------------------------------------------------
  // Null

  /**
   * Creates a singleton list of an exception value using the default exception name.
   *
   * @param t the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default List<Field> onlyException(@NotNull Field.Value.ExceptionValue t) {
    return only(exception(t));
  }

  /**
   * Creates a singleton list of an exception value using an explicit name.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default List<Field> onlyException(
      @NotNull String name, @NotNull Field.Value.ExceptionValue value) {
    return only(exception(name, value));
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
    return value(name, Field.Value.nullValue());
  }

  /**
   * Creates a singleton list with a null field.
   *
   * @param name the name of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default List<Field> onlyNullField(@NotNull String name) {
    return only(nullField(name));
  }
}

// internal class so interface doesn't have to expose it
class FieldBuilderInstance {
  private static final FieldBuilder instance = new FieldBuilder() {};

  static FieldBuilder getInstance() {
    return instance;
  }
}
