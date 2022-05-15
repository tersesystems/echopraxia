package com.tersesystems.echopraxia.api;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Extended field builder with "only" interfaces for backwards compatibility. */
public interface FieldBuilderWithOnly extends FieldBuilder {

  /**
   * Wraps a single field in a singleton list.
   *
   * <p>{@code fb.only(fb.string("correlation_id", "match"))}
   *
   * @param field the given field, can be null in which case an empty list is returned.
   * @return a list containing a single field element.
   */
  @NotNull
  default FieldBuilderResult only(@Nullable Field field) {
    return (field == null) ? FieldBuilderResult.empty() : FieldBuilderResult.only(field);
  }

  /**
   * Creates a list of fields out of a name and a raw string value.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default FieldBuilderResult onlyString(@NotNull String name, @NotNull String value) {
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
  default FieldBuilderResult onlyString(@NotNull String name, @NotNull Value.StringValue value) {
    return only(string(name, value));
  }

  /**
   * Creates a singleton list of fields out of a name and a raw number value.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default <N extends Number & Comparable<N>> FieldBuilderResult onlyNumber(
      @NotNull String name, @NotNull N value) {
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
  default <N extends Number & Comparable<N>> FieldBuilderResult onlyNumber(
      @NotNull String name, @NotNull Value.NumberValue<N> value) {
    return only(number(name, value));
  }

  /**
   * Creates a singleton list of fields out of a name and a raw boolean value.
   *
   * @param name the name of the field.
   * @param value the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default FieldBuilderResult onlyBool(@NotNull String name, @NotNull Boolean value) {
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
  default FieldBuilderResult onlyBool(@NotNull String name, @NotNull Value.BooleanValue value) {
    return only(bool(name, value));
  }

  /**
   * Creates a singleton list of an array field out of a name and object values.
   *
   * @param name the name of the field.
   * @param values the values.
   * @return a list containing a single field.
   */
  @NotNull
  default FieldBuilderResult onlyArray(@NotNull String name, @NotNull Value.ObjectValue... values) {
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
  default FieldBuilderResult onlyArray(@NotNull String name, String... values) {
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
  default <N extends Number & Comparable<N>> FieldBuilderResult onlyArray(
      @NotNull String name, N... values) {
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
  default FieldBuilderResult onlyArray(@NotNull String name, Boolean... values) {
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
  default FieldBuilderResult onlyArray(@NotNull String name, @NotNull Value.ArrayValue value) {
    return only(array(name, value));
  }

  /**
   * Creates a singleton list of an object out of a name and array values.
   *
   * @param name the name of the field.
   * @param values the values.
   * @return a list containing a single field.
   */
  @NotNull
  default FieldBuilderResult onlyObject(@NotNull String name, @NotNull Field... values) {
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
  default FieldBuilderResult onlyObject(@NotNull String name, @NotNull List<Field> values) {
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
  default FieldBuilderResult onlyObject(@NotNull String name, @NotNull Value.ObjectValue value) {
    return only(object(name, value));
  }

  /**
   * Creates a singleton list of an exception using the default exception name.
   *
   * @param t the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default FieldBuilderResult onlyException(@NotNull Throwable t) {
    return only(exception(t));
  }

  /**
   * Creates a singleton list of an exception value using the default exception name.
   *
   * @param t the value of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default FieldBuilderResult onlyException(@NotNull Value.ExceptionValue t) {
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
  default FieldBuilderResult onlyException(
      @NotNull String name, @NotNull Value.ExceptionValue value) {
    return only(exception(name, value));
  }

  /**
   * Creates a singleton list with a null field.
   *
   * @param name the name of the field.
   * @return a list containing a single field.
   */
  @NotNull
  default FieldBuilderResult onlyNullField(@NotNull String name) {
    return only(nullField(name));
  }
}
