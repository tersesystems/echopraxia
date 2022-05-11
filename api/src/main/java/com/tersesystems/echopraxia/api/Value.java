package com.tersesystems.echopraxia.api;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * The Value class. This consists of the basic JSON infoset values, and the throwable exception.
 *
 * <p>In general, you should use the static helpers i.e. {@code Value.string("foo")} or {@code
 * Value.exception(ex)}
 *
 * @param <V> the raw type of the underling value.
 */
public abstract class Value<V> {

  public enum Type {
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
  public abstract Value.Type type();

  @NotNull
  public String toString() {
    final Object raw = raw();
    final Type type = type();
    if (raw == null
        || type == Type.NULL) { // if null value or a raw value was set to null, keep going.
      return "null";
    }
    if (type == Type.STRING) {
      return ((String) raw());
    }

    if (type == Type.BOOLEAN) {
      return ((Boolean) raw()) ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
    }

    if (type == Type.NUMBER) {
      return raw().toString();
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
  public static StringValue string(@NotNull String value) {
    return new StringValue(value);
  }

  /**
   * Wraps a number with a Value.
   *
   * @param value the raw number value.
   * @return the Value
   */
  @NotNull
  public static NumberValue number(@NotNull Number value) {
    return new NumberValue(value);
  }

  /**
   * Wraps a boolean with a Value.
   *
   * @param value the raw boolean value.
   * @return the Value.
   */
  @NotNull
  public static BooleanValue bool(@NotNull Boolean value) {
    if (value == null) return BooleanValue.FALSE;
    else return (value) ? BooleanValue.TRUE : BooleanValue.FALSE;
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
  public static ExceptionValue exception(@NotNull Throwable t) {
    return new ExceptionValue(t);
  }

  /**
   * Wraps an array of values with a value.
   *
   * @param values variadic elements of values.
   * @return the Value.
   */
  @NotNull
  public static ArrayValue array(Value<?>... values) {
    if (values.length == 0) {
      return ArrayValue.EMPTY;
    }
    return new ArrayValue(Arrays.asList(values));
  }

  /**
   * Wraps an array of values with boolean values.
   *
   * @param values varadic elements of values.
   * @return the Value.
   */
  @NotNull
  public static ArrayValue array(Boolean @NotNull ... values) {
    if (values.length == 0) {
      return ArrayValue.EMPTY;
    }
    return new ArrayValue(asList(values, Value::bool));
  }

  /**
   * Wraps an array of values with string values.
   *
   * @param values varadic elements of values.
   * @return the Value.
   */
  @NotNull
  public static ArrayValue array(String @NotNull ... values) {
    if (values.length == 0) {
      return ArrayValue.EMPTY;
    }
    return new ArrayValue(asList(values, Value::string));
  }

  /**
   * Wraps an array of values with number values.
   *
   * @param values varadic elements of values.
   * @return the Value.
   */
  @NotNull
  public static ArrayValue array(Number @NotNull ... values) {
    if (values.length == 0) {
      return ArrayValue.EMPTY;
    }
    return new ArrayValue(asList(values, Value::number));
  }

  /**
   * Returns a list of values as a Value.
   *
   * @param values a list of values.
   * @return the Value.
   */
  public static ArrayValue array(@NotNull List<Value<?>> values) {
    if (values == null || values.size() == 0) {
      return ArrayValue.EMPTY;
    }
    return new ArrayValue(values);
  }

  /**
   * Takes a list of objects and a transform function that maps from T to a value.
   *
   * @param values a list of values.
   * @param transform the transform function
   * @param <T> the type of object.
   * @return the Value.
   */
  @NotNull
  public static <T> ArrayValue array(
      @NotNull Function<T, Value<?>> transform, @NotNull List<T> values) {
    if (values == null || values.size() == 0) {
      return ArrayValue.EMPTY;
    }
    // Nulls are allowed in array.
    return new ArrayValue(asList(values, transform));
  }

  /**
   * Takes an array of objects and a transform function that maps from T to a value.
   *
   * @param values an array of values.
   * @param transform the transform function
   * @param <T> the type of object.
   * @return the Value.
   */
  @NotNull
  public static <T> ArrayValue array(
      @NotNull Function<T, Value<?>> transform, T @NotNull [] values) {
    if (values == null || values.length == 0) {
      return ArrayValue.EMPTY;
    }
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
  public static ObjectValue object(Field @NotNull ... fields) {
    if (fields.length == 0) {
      return ObjectValue.EMPTY;
    }
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
  public static ObjectValue object(@NotNull List<Field> fields) {
    if (fields == null || fields.size() == 0) {
      return ObjectValue.EMPTY;
    }
    // Null fields are not allowed.
    final List<Field> nonNullList = new ArrayList<>(fields.size());
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
   * @param <T> THe type of the element
   * @return the Value.
   */
  @NotNull
  public static <T> ObjectValue object(
      @NotNull Function<T, Field> transform, T @NotNull [] values) {
    if (values == null || values.length == 0) {
      return ObjectValue.EMPTY;
    }
    List<Field> fields =
        Arrays.stream(values).map(transform).filter(Objects::nonNull).collect(Collectors.toList());
    return new ObjectValue(fields);
  }

  /**
   * Turns a list of T into a list of fields using the transform function.
   *
   * @param transform the transform function
   * @param values the list of fields.
   * @param <T> the type of the element
   * @return the Value.
   */
  @NotNull
  public static <T> ObjectValue object(
      @NotNull Function<T, Field> transform, @NotNull List<T> values) {
    if (values == null || values.size() == 0) {
      return ObjectValue.EMPTY;
    }
    List<Field> fields = new ArrayList<>(values.size());
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
   * @param <T> the raw type
   * @return list of values.
   */
  @NotNull
  private static <T> List<Value<?>> asList(T @NotNull [] array, @NotNull Function<T, Value<?>> f) {
    return Arrays.stream(array).map(f).collect(Collectors.toList());
  }

  /**
   * Utility method to turn a list into a list of values with a transformer.
   *
   * @param values the list of values
   * @param f the function transforming a raw T to a value
   * @param <T> the raw type
   * @return list of values.
   */
  @NotNull
  private static <T> List<Value<?>> asList(
      @NotNull List<T> values, @NotNull Function<T, Value<?>> f) {
    List<Value<?>> list = new ArrayList<>(values.size());
    for (T value : values) {
      Value<?> value1 = f.apply(value);
      list.add(value1);
    }
    return list;
  }

  public static final class BooleanValue extends Value<Boolean> {

    public static final BooleanValue TRUE = new BooleanValue(true);

    public static final BooleanValue FALSE = new BooleanValue(false);

    private final Boolean bool;

    private BooleanValue(Boolean bool) {
      this.bool = bool;
    }

    @Override
    public @NotNull Boolean raw() {
      return this.bool;
    }

    @Override
    public @NotNull Value.Type type() {
      return Type.BOOLEAN;
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
    public @NotNull Value.Type type() {
      return Type.NUMBER;
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
    public @NotNull Value.Type type() {
      return Type.STRING;
    }
  }

  public static final class ArrayValue extends Value<List<Value<?>>> {
    private final List<Value<?>> raw;

    public static final ArrayValue EMPTY = new ArrayValue(Collections.emptyList());

    private ArrayValue(List<Value<?>> raw) {
      this.raw = raw;
    }

    @Override
    public @NotNull List<Value<?>> raw() {
      return raw;
    }

    @Override
    public @NotNull Value.Type type() {
      return Type.ARRAY;
    }
  }

  public static final class ObjectValue extends Value<List<Field>> {
    public static final ObjectValue EMPTY = new ObjectValue(Collections.emptyList());

    private final List<Field> raw;

    private ObjectValue(List<Field> raw) {
      this.raw = raw;
    }

    @Override
    public @NotNull Value.Type type() {
      return Type.OBJECT;
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
    public @NotNull Value.Type type() {
      return Type.NULL;
    }

    public static final @NotNull NullValue instance = new NullValue();
  }

  public static final class ExceptionValue extends Value<Throwable> {
    private final Throwable raw;

    private ExceptionValue(Throwable raw) {
      this.raw = raw;
    }

    @Override
    public @NotNull Value.Type type() {
      return Type.EXCEPTION;
    }

    @Override
    public Throwable raw() {
      return raw;
    }
  }
}
