package com.tersesystems.echopraxia;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

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
      final Object raw = raw();
      if (raw == null) { // if null value or a raw value was set to null, keep going.
        return "null";
      }
      final StringBuilder b = new StringBuilder(255);
      Internals.ValueFormatter.formatToBuffer(b, this);
      return b.toString();
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
     * @param transform the transform function
     * @return the Value.
     * @param <T> the type of object.
     */
    @NotNull
    public static <T> Value.ArrayValue array(
        @NotNull Function<T, Value<?>> transform, @NotNull List<T> values) {
      // Nulls are allowed in array.
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
      // nulls are explicitly allowed in array.
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
      // Null fields are not allowed.
      final List<Field> nonNullList =
          Arrays.stream(fields).filter(Objects::nonNull).collect(Collectors.toList());
      return new ObjectValue(nonNullList);
    }

    /**
     * Wraps a list of fields with a Value as an object.
     *
     * @param fields the list of fields.
     * @return the Value.
     */
    @NotNull
    public static Value.ObjectValue object(@NotNull List<Field> fields) {
      // Null fields are not allowed.
      final List<Field> nonNullList = new ArrayList<>();
      for (Field field : fields) {
        if (field != null) {
          nonNullList.add(field);
        }
      }
      return new ObjectValue(nonNullList);
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
      List<Field> fields =
          Arrays.stream(values)
              .map(transform)
              .filter(Objects::nonNull)
              .collect(Collectors.toList());
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
      List<Field> fields = new ArrayList<>();
      for (T value : values) {
        Field field = transform.apply(value);
        if (field != null) {
          fields.add(field);
        }
      }
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
      List<Value<?>> list = new ArrayList<>();
      for (T value : values) {
        Value<?> value1 = f.apply(value);
        list.add(value1);
      }
      return list;
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
      public Number raw() {
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
      public String raw() {
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
      public @NotNull List<Value<?>> raw() {
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
