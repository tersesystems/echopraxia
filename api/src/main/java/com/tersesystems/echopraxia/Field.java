package com.tersesystems.echopraxia;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import com.tersesystems.echopraxia.Constants.DefaultKeyValueField;
import com.tersesystems.echopraxia.Constants.DefaultValueField;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The Field interface. This is a core part of structured data, and consists of a name and a Value,
 * where a value corresponds roughly to the JSON infoset: string, number, boolean, null, array, and
 * object.
 *
 * <p>The field builder interface and custom field builders go a long way to building up more
 * complex structures, please see documentation for how to use them.
 */
public interface Field {

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
   * The field builder interface.
   *
   * <p>This is provided in a function for creating fields in context and arguments. You are
   * encourage to extend this interface to create your own custom field builders to create your own
   * domain specific logic for creating fields.
   */
  interface Builder {
    String EXCEPTION = "exception";

    static @NotNull Builder instance() {
      return Constants.builder();
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
     * <p>This method is intentionally value blind so any value, including null value, will work
     * here.
     *
     * @param name the field name
     * @param value the field value
     * @return the field.
     */
    @NotNull
    default Field value(@NotNull String name, @NotNull Value<?> value) {
      return new DefaultValueField(name, value);
    }

    /**
     * Creates a key value field that renders in message template as key=value.
     *
     * <p>This method is intentionally value blind so any value, including null value, will work
     * here.
     *
     * @param name the field name
     * @param value the field value
     * @return the field.
     */
    @NotNull
    default Field keyValue(@NotNull String name, @NotNull Value<?> value) {
      return new DefaultKeyValueField(name, value);
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
      return value(name, Value.string(value));
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
    default List<Field> onlyString(@NotNull String name, @NotNull Value.StringValue value) {
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
      return value(name, Value.number(value));
    }

    /**
     * Creates a field out of a name and a number value.
     *
     * @param name the name of the field.
     * @param value the value of the field.
     * @return a list containing a single field.
     */
    @NotNull
    default Field number(@NotNull String name, @NotNull Value.NumberValue value) {
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
    default List<Field> onlyNumber(@NotNull String name, @NotNull Value.NumberValue value) {
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
      return value(name, Value.bool(value));
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
    default List<Field> onlyBool(@NotNull String name, @NotNull Value.BooleanValue value) {
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
    default Field array(@NotNull String name, Number... values) {
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

    /**
     * Creates a singleton list of an array field out of a name and object values.
     *
     * @param name the name of the field.
     * @param values the values.
     * @return a list containing a single field.
     */
    @NotNull
    default List<Field> onlyArray(@NotNull String name, @NotNull Value.ObjectValue... values) {
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
    default List<Field> onlyArray(@NotNull String name, @NotNull Value.ArrayValue value) {
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
    default List<Field> onlyObject(@NotNull String name, @NotNull Value.ObjectValue value) {
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

    /**
     * Creates a singleton list of an exception using the default exception name.
     *
     * @param t the value of the field.
     * @return a list containing a single field.
     */
    // should probably deprecate this as  logger.error(msg, e) is the ideomatic form over fb ->
    // fb.onlyException(e)
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
    default List<Field> onlyException(@NotNull Value.ExceptionValue t) {
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
    default List<Field> onlyException(@NotNull String name, @NotNull Value.ExceptionValue value) {
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
      return value(name, Value.nullValue());
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

  /**
   * The BuilderFunction interface. This is used when logging arguments, so that a field builder can
   * return a list of fields.
   *
   * @param <FB> the field builder type.
   */
  @FunctionalInterface
  interface BuilderFunction<FB extends Builder> extends Function<FB, List<Field>> {}

  /**
   * The Value class. This consists of the basic JSON infoset values, and the throwable exception.
   *
   * <p>In general, you should use the static helpers i.e. {@code Value.string("foo")} or {@code
   * Value.exception(ex)}
   *
   * @param <V> the raw type of the underling value.
   */
  abstract class Value<V> {

    public enum ValueType {
      ARRAY,
      OBJECT,
      STRING,
      NUMBER,
      BOOLEAN,
      EXCEPTION,
      NULL
    }

    protected Value() {}

    /**
     * The underlying raw value, may be null in some cases.
     *
     * @return the underlying raw value.
     */
    public abstract V raw();

    /**
     * The value type.
     *
     * @return the value type.
     */
    @NotNull
    public abstract ValueType type();

    @NotNull
    public String toString() {
      return valueToString(this);
    }

    /**
     * Turns a given value into a string in a line oriented format.
     *
     * @param v the value
     * @return the value as a string.
     */
    @NotNull
    private static String valueToString(@NotNull Value<?> v) {
      final Object raw = v.raw();
      if (raw == null) { // if null value or a raw value was set to null, keep going.
        return "null";
      }

      // render an object with curly braces to distinguish from array.
      if (v.type() == ValueType.OBJECT) {
        final List<Field> fieldList = ((ObjectValue) v).raw();
        StringBuilder b = new StringBuilder("{");
        final String s = fieldList.stream().map(Field::toString).collect(Collectors.joining(", "));
        b.append(s);
        b.append("}");
        return b.toString();
      }

      return raw.toString();
    }

    /**
     * Wraps a string with a Value.
     *
     * @param value the raw string value.
     * @return the Value
     */
    @NotNull
    public static Value.StringValue string(@NotNull String value) {
      return new StringValue(value);
    }

    /**
     * Wraps a number with a Value.
     *
     * @param value the raw number value.
     * @return the Value
     */
    @NotNull
    public static Value.NumberValue number(@NotNull Number value) {
      return new NumberValue(value);
    }

    /**
     * Wraps a boolean with a Value.
     *
     * @param value the raw boolean value.
     * @return the Value.
     */
    @NotNull
    public static Value.BooleanValue bool(@NotNull Boolean value) {
      return new BooleanValue(value);
    }

    /**
     * Returns a `null` value.
     *
     * @return the Value.
     */
    @NotNull
    public static Value<?> nullValue() {
      return NullValue.instance;
    }

    /**
     * Wraps an exception with a Value.
     *
     * @param t the raw exception value.
     * @return the Value.
     */
    @NotNull
    public static Value.ExceptionValue exception(@NotNull Throwable t) {
      return new ExceptionValue(t);
    }

    /**
     * Wraps an array of values with a value.
     *
     * @param values variadic elements of values.
     * @return the Value.
     */
    @NotNull
    public static Value.ArrayValue array(Value<?>... values) {
      return new ArrayValue(Arrays.asList(values));
    }

    /**
     * Wraps an array of values with boolean values.
     *
     * @param values varadic elements of values.
     * @return the Value.
     */
    @NotNull
    public static Value.ArrayValue array(Boolean @NotNull ... values) {
      return new ArrayValue(asList(values, Value::bool));
    }

    /**
     * Wraps an array of values with string values.
     *
     * @param values varadic elements of values.
     * @return the Value.
     */
    @NotNull
    public static Value.ArrayValue array(String @NotNull ... values) {
      return new ArrayValue(asList(values, Value::string));
    }

    /**
     * Wraps an array of values with number values.
     *
     * @param values varadic elements of values.
     * @return the Value.
     */
    @NotNull
    public static Value.ArrayValue array(Number @NotNull ... values) {
      return new ArrayValue(asList(values, Value::number));
    }

    /**
     * Returns a list of values as a Value.
     *
     * @param values a list of values.
     * @return the Value.
     */
    public static Value.ArrayValue array(@NotNull List<Value<?>> values) {
      return new ArrayValue(values);
    }

    /**
     * Takes a list of objects and a transform function that maps from T to a value.
     *
     * @param values a list of values.
     * @param transform the tranform function
     * @return the Value.
     * @param <T> the type of object.
     */
    @NotNull
    public static <T> Value.ArrayValue array(
        @NotNull Function<T, Value<?>> transform, @NotNull List<T> values) {
      return new ArrayValue(asList(values, transform));
    }

    /**
     * Takes an array of objects and a transform function that maps from T to a value.
     *
     * @param values an array of values.
     * @param transform the transform function
     * @return the Value.
     * @param <T> the type of object.
     */
    @NotNull
    public static <T> Value.ArrayValue array(
        @NotNull Function<T, Value<?>> transform, T @NotNull [] values) {
      return new ArrayValue(asList(values, transform));
    }

    /**
     * Wraps an array of fields with a Value as an object.
     *
     * @param fields variadic elements of fields.
     * @return the Value.
     */
    @NotNull
    public static Value.ObjectValue object(Field @NotNull ... fields) {
      return new ObjectValue(Arrays.asList(fields));
    }

    /**
     * Wraps a list of fields with a Value as an object.
     *
     * @param fields the list of fields.
     * @return the Value.
     */
    @NotNull
    public static Value.ObjectValue object(@NotNull List<Field> fields) {
      return new ObjectValue(fields);
    }

    /**
     * Turns an array of T into a list of fields using the transform function.
     *
     * @param transform the transform function
     * @param values the list of fields.
     * @return the Value.
     * @param <T> THe type of the element
     */
    @NotNull
    public static <T> Value.ObjectValue object(
        @NotNull Function<T, Field> transform, T @NotNull [] values) {
      List<Field> fields = Arrays.stream(values).map(transform).collect(Collectors.toList());
      return new ObjectValue(fields);
    }

    /**
     * Turns a list of T into a list of fields using the transform function.
     *
     * @param transform the transform function
     * @param values the list of fields.
     * @return the Value.
     * @param <T> the type of the element
     */
    @NotNull
    public static <T> Value.ObjectValue object(
        @NotNull Function<T, Field> transform, @NotNull List<T> values) {
      List<Field> fields = values.stream().map(transform).collect(Collectors.toList());
      return new ObjectValue(fields);
    }

    /**
     * Wraps an optional value, returning nullValue() if the optional is empty.
     *
     * <p>Best used with {@code fb.value()} or {@code fb.keyValue()}.
     *
     * @param optionalValue the optional value.
     * @return the value, or null value if the optional is empty.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NotNull
    public static Value<?> optional(@NotNull Optional<? extends Value<?>> optionalValue) {
      if (optionalValue.isPresent()) {
        return optionalValue.get();
      }
      return nullValue();
    }

    /**
     * Utility method to turn an array into a list of values with a transformer.
     *
     * @param array the raw array.
     * @param f the function transforming a raw T to a value
     * @return list of values.
     * @param <T> the raw type
     */
    @NotNull
    private static <T> List<Value<?>> asList(
        T @NotNull [] array, @NotNull Function<T, Value<?>> f) {
      return Arrays.stream(array).map(f).collect(Collectors.toList());
    }

    /**
     * Utility method to turn a list into a list of values with a transformer.
     *
     * @param values the list of values
     * @param f the function transforming a raw T to a value
     * @return list of values.
     * @param <T> the raw type
     */
    @NotNull
    private static <T> List<Value<?>> asList(
        @NotNull List<T> values, @NotNull Function<T, Value<?>> f) {
      return values.stream().map(f).collect(Collectors.toList());
    }

    public static final class BooleanValue extends Value<Boolean> {
      private final Boolean bool;

      private BooleanValue(Boolean bool) {
        this.bool = bool;
      }

      @Override
      public @NotNull Boolean raw() {
        return this.bool;
      }

      @Override
      public @NotNull ValueType type() {
        return ValueType.BOOLEAN;
      }
    }

    public static final class NumberValue extends Value<Number> {
      private final Number number;

      private NumberValue(Number number) {
        this.number = number;
      }

      @Override
      public @NotNull Number raw() {
        return number;
      }

      @Override
      public @NotNull ValueType type() {
        return ValueType.NUMBER;
      }
    }

    public static final class StringValue extends Value<String> {
      private final String s;

      private StringValue(String s) {
        this.s = s;
      }

      @Override
      public @NotNull String raw() {
        return s;
      }

      @Override
      public @NotNull ValueType type() {
        return ValueType.STRING;
      }
    }

    public static final class ArrayValue extends Value<List<Value<?>>> {
      private final List<Value<?>> raw;

      private ArrayValue(List<Value<?>> raw) {
        this.raw = raw;
      }

      @Override
      public List<Value<?>> raw() {
        return raw;
      }

      @Override
      public @NotNull ValueType type() {
        return ValueType.ARRAY;
      }
    }

    public static final class ObjectValue extends Value<List<Field>> {
      private final List<Field> raw;

      private ObjectValue(List<Field> raw) {
        this.raw = raw;
      }

      @Override
      public @NotNull ValueType type() {
        return ValueType.OBJECT;
      }

      @Override
      public List<Field> raw() {
        return raw;
      }
    }

    public static final class NullValue extends Value<Void> {
      // Should not be able to instantiate this outside of class.
      private NullValue() {}

      @Override
      public Void raw() {
        return null; // can't really return an instance of void :-)
      }

      @Override
      public @NotNull ValueType type() {
        return ValueType.NULL;
      }

      public static @NotNull NullValue instance = new NullValue();
    }

    public static final class ExceptionValue extends Value<Throwable> {
      private final Throwable raw;

      private ExceptionValue(Throwable raw) {
        this.raw = raw;
      }

      @Override
      public @NotNull ValueType type() {
        return ValueType.EXCEPTION;
      }

      @Override
      public Throwable raw() {
        return raw;
      }
    }
  }
}
