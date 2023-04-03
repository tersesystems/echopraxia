package com.tersesystems.echopraxia.api;

import java.math.BigDecimal;
import java.math.BigInteger;
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

  /** @return this value as an object value. */
  @NotNull
  public ObjectValue asObject() {
    return (ObjectValue) this;
  }

  /** @return this value as an array value */
  @NotNull
  public ArrayValue asArray() {
    return (ArrayValue) this;
  }

  /** @return this value as a string value */
  @NotNull
  public StringValue asString() {
    return (StringValue) this;
  }

  /** @return this value as a number value */
  @NotNull
  public <T extends Number & Comparable<T>> NumberValue<T> asNumber() {
    return (NumberValue<T>) this;
  }

  /** @return this value as an exception value */
  public ExceptionValue asException() {
    return (ExceptionValue) this;
  }

  /** @return this value as a null value */
  public NullValue asNull() {
    return (NullValue) this;
  }

  /** @return this value as a boolean value */
  public BooleanValue asBoolean() {
    return (BooleanValue) this;
  }

  @NotNull
  public String toString() {
    final Object raw = raw();
    final Type type = type();
    if (raw == null
        || type == Type.NULL) { // if null value or a raw value was set to null, keep going.
      return "null";
    }

    if (type == Type.STRING) {
      return ((String) raw);
    }

    if (type == Type.BOOLEAN) {
      return Boolean.toString((Boolean) raw);
    }

    if (type == Type.NUMBER) {
      return raw.toString();
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

  @NotNull
  public static Value<Byte> number(@NotNull Byte value) {
    final int offset = 128;
    if (value == null) return NumberValue.ByteValue.Cache.cache[offset];
    return NumberValue.ByteValue.Cache.cache[(int) value + offset];
  }

  @NotNull
  public static Value<Short> number(@NotNull Short s) {
    if (s == null) return NumberValue.ShortValue.ZERO;
    final int offset = 128;
    int sAsInt = s;
    if (sAsInt >= -128 && sAsInt <= 127) { // must cache
      return NumberValue.ShortValue.Cache.cache[sAsInt + offset];
    }
    return new NumberValue.ShortValue(s);
  }

  /**
   * Wraps a number with a Value.
   *
   * @param value the raw number value.
   * @return the Value
   */
  @NotNull
  public static NumberValue<Integer> number(@NotNull Integer value) {
    if (value == null)
      return NumberValue.IntegerValue.Cache.cache[-NumberValue.IntegerValue.Cache.low];
    if (value >= NumberValue.IntegerValue.Cache.low && value <= NumberValue.IntegerValue.Cache.high)
      return NumberValue.IntegerValue.Cache.cache[value + (-NumberValue.IntegerValue.Cache.low)];
    return new NumberValue.IntegerValue(value);
  }

  public static NumberValue<Long> number(@NotNull Long value) {
    final int offset = 128;
    if (value == null) return NumberValue.LongValue.Cache.cache[offset];
    if (value >= -128 && value <= 127) { // will cache
      return NumberValue.LongValue.Cache.cache[value.intValue() + offset];
    }
    return new NumberValue.LongValue(value);
  }

  public static NumberValue<Float> number(@NotNull Float value) {
    if (value == null || Objects.equals(value, 0.0f)) return NumberValue.FloatValue.ZERO;
    return new NumberValue.FloatValue(value);
  }

  public static NumberValue<Double> number(@NotNull Double value) {
    if (value == null || Objects.equals(value, 0.0d)) return NumberValue.DoubleValue.ZERO;
    return new NumberValue.DoubleValue(value);
  }

  public static NumberValue<BigInteger> number(@NotNull BigInteger value) {
    if (value == null || Objects.equals(value, BigInteger.ZERO))
      return NumberValue.BigIntegerValue.ZERO;
    return new NumberValue.BigIntegerValue(value);
  }

  public static NumberValue<BigDecimal> number(@NotNull BigDecimal value) {
    if (value == null || Objects.equals(value, BigDecimal.ZERO))
      return NumberValue.BigDecimalValue.ZERO;
    return new NumberValue.BigDecimalValue(value);
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
   * @param values variadic elements of values.
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
   * @param values variadic elements of values.
   * @return the Value.
   */
  @NotNull
  public static ArrayValue array(Byte @NotNull ... values) {
    if (values.length == 0) {
      return ArrayValue.EMPTY;
    }
    return new ArrayValue(asList(values, Value::number));
  }

  public static ArrayValue array(Short @NotNull ... values) {
    if (values.length == 0) {
      return ArrayValue.EMPTY;
    }
    return new ArrayValue(asList(values, Value::number));
  }

  public static ArrayValue array(Integer @NotNull ... values) {
    if (values.length == 0) {
      return ArrayValue.EMPTY;
    }
    return new ArrayValue(asList(values, Value::number));
  }

  public static ArrayValue array(Long @NotNull ... values) {
    if (values.length == 0) {
      return ArrayValue.EMPTY;
    }
    return new ArrayValue(asList(values, Value::number));
  }

  public static ArrayValue array(Double @NotNull ... values) {
    if (values.length == 0) {
      return ArrayValue.EMPTY;
    }
    return new ArrayValue(asList(values, Value::number));
  }

  public static ArrayValue array(Float @NotNull ... values) {
    if (values.length == 0) {
      return ArrayValue.EMPTY;
    }
    return new ArrayValue(asList(values, Value::number));
  }

  public static ArrayValue array(BigInteger @NotNull ... values) {
    if (values.length == 0) {
      return ArrayValue.EMPTY;
    }
    return new ArrayValue(asList(values, Value::number));
  }

  public static ArrayValue array(BigDecimal @NotNull ... values) {
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

  public static <T> boolean equals(Value<? extends T> value1, Value<? extends T> value2) {
    return Objects.equals(value1, value2);
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

    private final Boolean raw;

    private BooleanValue(Boolean bool) {
      this.raw = bool;
    }

    @Override
    public @NotNull Boolean raw() {
      return this.raw;
    }

    @Override
    public @NotNull Value.Type type() {
      return Type.BOOLEAN;
    }
  }

  public static class NumberValue<N extends Number & Comparable<N>> extends Value<N>
      implements Comparable<NumberValue<N>> {
    private final N raw;

    private NumberValue(N number) {
      this.raw = number;
    }

    @Override
    public N raw() {
      return raw;
    }

    @Override
    public @NotNull Value.Type type() {
      return Type.NUMBER;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      NumberValue<?> that = (NumberValue<?>) o;
      // follow the Java example of not allowing general comparison here
      // integer != long, long != float, float != double etc.
      // if you want specific comparison, then cast the raw value.
      return Objects.equals(raw, that.raw);
    }

    @Override
    public int hashCode() {
      return raw != null ? raw.hashCode() : 0;
    }

    @Override
    public int compareTo(@NotNull Value.NumberValue<N> o) {
      return this.raw.compareTo(o.raw);
    }

    private static final class ByteValue extends NumberValue<Byte> {
      private ByteValue(Byte number) {
        super(number);
      }

      private static class Cache {
        private Cache() {}

        static final ByteValue[] cache = new ByteValue[-(-128) + 127 + 1];

        static {
          for (int i = 0; i < cache.length; i++) cache[i] = new ByteValue((byte) (i - 128));
        }
      }
    }

    private static final class ShortValue extends NumberValue<Short> {
      public static final ShortValue ZERO = new ShortValue((short) 0);

      private ShortValue(Short number) {
        super(number);
      }

      private static class Cache {
        private Cache() {}

        static final ShortValue[] cache = new ShortValue[-(-128) + 127 + 1];

        static {
          for (int i = 0; i < cache.length; i++) cache[i] = new ShortValue((short) (i - 128));
        }
      }
    }

    private static final class IntegerValue extends NumberValue<Integer> {
      private IntegerValue(Integer number) {
        super(number);
      }

      private static class Cache {
        static final int low = -128;
        static final int high = 127;

        static final IntegerValue[] cache = new IntegerValue[(high - low) + 1];

        static {
          for (int i = 0; i < cache.length; i++) cache[i] = new IntegerValue((i - 128));
        }

        private Cache() {}
      }
    }

    private static final class LongValue extends NumberValue<Long> {
      private LongValue(Long number) {
        super(number);
      }

      private static class Cache {
        private Cache() {}

        static final LongValue[] cache = new LongValue[-(-128) + 127 + 1];

        static {
          for (int i = 0; i < cache.length; i++) cache[i] = new LongValue((long) (i - 128));
        }
      }
    }

    private static final class DoubleValue extends NumberValue<Double> {
      public static final DoubleValue ZERO = new DoubleValue(0.0d);

      private DoubleValue(Double number) {
        super(number);
      }
    }

    private static final class FloatValue extends NumberValue<Float> {
      public static final FloatValue ZERO = new FloatValue(0.0f);

      private FloatValue(Float number) {
        super(number);
      }
    }

    private static final class BigIntegerValue extends NumberValue<BigInteger> {
      public static final BigIntegerValue ZERO = new BigIntegerValue(java.math.BigInteger.ZERO);

      private BigIntegerValue(BigInteger number) {
        super(number);
      }
    }

    private static final class BigDecimalValue extends NumberValue<BigDecimal> {
      public static final BigDecimalValue ZERO = new BigDecimalValue(java.math.BigDecimal.ZERO);

      private BigDecimalValue(BigDecimal number) {
        super(number);
      }
    }
  }

  public static final class StringValue extends Value<String> implements Comparable<StringValue> {
    private final String raw;

    private StringValue(String s) {
      this.raw = s;
    }

    @Override
    public String raw() {
      return raw;
    }

    @Override
    public @NotNull Value.Type type() {
      return Type.STRING;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      StringValue that = (StringValue) o;
      return Objects.equals(raw, that.raw);
    }

    @Override
    public int hashCode() {
      return raw != null ? raw.hashCode() : 0;
    }

    @Override
    public int compareTo(@NotNull Value.StringValue o) {
      return o.raw.compareTo(this.raw);
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

    public ArrayValue add(Value<?> value) {
      ArrayList<Value<?>> values = new ArrayList<>(this.raw);
      values.add(value);
      return new ArrayValue(values);
    }

    public ArrayValue addAll(Collection<Value<?>> values) {
      ArrayList<Value<?>> joinedValues = new ArrayList<>(this.raw);
      joinedValues.addAll(values);
      return new ArrayValue(joinedValues);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ArrayValue that = (ArrayValue) o;
      return Objects.equals(raw, that.raw);
    }

    @Override
    public int hashCode() {
      return raw != null ? raw.hashCode() : 0;
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

    public ObjectValue add(Field field) {
      ArrayList<Field> fields = new ArrayList<>(this.raw);
      fields.add(field);
      return new ObjectValue(fields);
    }

    public ObjectValue addAll(Collection<Field> fields) {
      ArrayList<Field> joinedFields = new ArrayList<>(this.raw);
      joinedFields.addAll(fields);
      return new ObjectValue(joinedFields);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ObjectValue that = (ObjectValue) o;
      return Objects.equals(raw, that.raw);
    }

    @Override
    public int hashCode() {
      return raw != null ? raw.hashCode() : 0;
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

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ExceptionValue that = (ExceptionValue) o;
      return Objects.equals(raw, that.raw);
    }

    @Override
    public int hashCode() {
      return raw != null ? raw.hashCode() : 0;
    }
  }
}
