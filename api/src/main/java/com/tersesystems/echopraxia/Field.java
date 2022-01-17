package com.tersesystems.echopraxia;

import static java.util.Collections.singletonList;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
  String name();

  /**
   * The field value.
   *
   * @return the field value.
   */
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

    static Builder instance() {
      return DefaultFieldBuilder.singleton();
    }

    /**
     * Wraps a single field in a singleton list.
     *
     * <p>{@code fb.only(fb.string("correlation_id", "match"))}
     *
     * @param field the given field
     * @return a list containing a single field element.
     */
    default List<Field> only(Field field) {
      return singletonList(field);
    }

    /**
     * Wraps fields in a list. More convenient than {@code Arrays.asList}.
     *
     * <p>{@code fb.list( fb.string("correlation_id", "match"), fb.number("some_number", 12345) )}
     *
     * @param fields the given fields
     * @return a list containing the fields.
     */
    default List<Field> list(Field... fields) {
      return Arrays.asList(fields);
    }

    /**
     * Creates a field.
     *
     * @param name the field name
     * @param value the field value
     * @return the field.
     */
    default Field value(String name, Field.Value<?> value) {
      return new ValueField(name, value);
    }

    default Field keyValue(String name, Field.Value<?> value) {
      return new KeyValueField(name, value);
    }

    // string

    /**
     * Creates a field out of a name and a raw string value.
     *
     * @param name the name of the field.
     * @param value the value of the field.
     * @return the field.
     */
    default Field string(String name, String value) {
      return value(name, Value.string(value));
    }

    // number

    /**
     * Creates a field out of a name and a raw number value.
     *
     * @param name the name of the field.
     * @param value the value of the field.
     * @return a list containing a single field.
     */
    default Field number(String name, Number value) {
      return value(name, Value.number(value));
    }

    // boolean

    /**
     * Creates a field out of a name and a raw boolean value.
     *
     * @param name the name of the field.
     * @param value the value of the field.
     * @return a list containing a single field.
     */
    default Field bool(String name, Boolean value) {
      return value(name, Value.bool(value));
    }

    // array

    /**
     * Creates a field out of a name and array values.
     *
     * @param name the name of the field.
     * @param values the array of values.
     * @return a list containing a single field.
     */
    default Field array(String name, Value<?>... values) {
      // in logstash, StructuredArguments.array() ALWAYS returns key=value,
      // so it may not be possible to override this.
      return keyValue(name, Value.array(values));
    }

    /**
     * Creates a field out of a name and string array values.
     *
     * @param name the name of the field.
     * @param values the array of values.
     * @return a list containing a single field.
     */
    default Field array(String name, String... values) {
      return keyValue(name, Value.array(Value.asList(values, Value::string)));
    }

    /**
     * Creates a field out of a name and number array values.
     *
     * @param name the name of the field.
     * @param values the array of values.
     * @return a list containing a single field.
     */
    default Field array(String name, Number... values) {
      return keyValue(name, Value.array(Value.asList(values, Value::number)));
    }

    /**
     * Creates a field out of a name and boolean array values.
     *
     * @param name the name of the field.
     * @param values the array of values.
     * @return a list containing a single field.
     */
    default Field array(String name, Boolean... values) {
      return keyValue(name, Value.array(Value.asList(values, Value::bool)));
    }

    /**
     * Creates a field out of a name and a list of array values.
     *
     * @param name the name of the field.
     * @param values the list of values.
     * @return a list containing a single field.
     */
    default Field array(String name, List<Value<?>> values) {
      return keyValue(name, Value.array(values));
    }

    // object
    /**
     * Creates a field object out of a name and field values.
     *
     * @param name the name of the field.
     * @param values the values.
     * @return a single field.
     */
    default Field object(String name, Field... values) {
      return keyValue(name, Value.object(values));
    }

    /**
     * Creates a field object out of a name and field values.
     *
     * @param name the name of the field.
     * @param values the values.
     * @return a field.
     */
    default Field object(String name, List<Field> values) {
      return keyValue(name, Value.object(values));
    }

    // null

    /**
     * Creates a field with a null as a value.
     *
     * @param name the name of the field.
     * @return a field.
     */
    default Field nullValue(String name) {
      return value(name, Value.nullValue());
    }

    // exception

    /**
     * Creates a field from an exception, using the default exception name "exception".
     *
     * @param t the exception.
     * @return a field.
     */
    default Field exception(Throwable t) {
      return keyValue(EXCEPTION, Value.exception(t));
    }

    /**
     * Creates a field from an exception, using an explicit name.
     *
     * @param name the field name
     * @param t the exception.
     * @return a field.
     */
    default Field exception(String name, Throwable t) {
      return keyValue(name, Value.exception(t));
    }

    /**
     * Creates a list of fields out of a name and a raw string value.
     *
     * @param name the name of the field.
     * @param value the value of the field.
     * @return a list containing a single field.
     */
    default List<Field> onlyString(String name, String value) {
      return only(string(name, value));
    }

    /**
     * Creates a singleton list of fields out of a name and a raw number value.
     *
     * @param name the name of the field.
     * @param value the value of the field.
     * @return a list containing a single field.
     */
    default List<Field> onlyNumber(String name, Number value) {
      return only(number(name, value));
    }

    /**
     * Creates a singleton list of fields out of a name and a raw boolean value.
     *
     * @param name the name of the field.
     * @param value the value of the field.
     * @return a list containing a single field.
     */
    default List<Field> onlyBool(String name, Boolean value) {
      return only(bool(name, value));
    }

    /**
     * Creates a singleton list of an array field out of a name and array values.
     *
     * @param name the name of the field.
     * @param values the values.
     * @return a list containing a single field.
     */
    default List<Field> onlyArray(String name, Value<?>... values) {
      return only(array(name, values));
    }

    /**
     * Creates a singleton list of an array field out of a name and a list of values.
     *
     * @param name the name of the field.
     * @param values the values.
     * @return a list containing a single field.
     */
    default List<Field> onlyArray(String name, List<Value<?>> values) {
      return only(array(name, values));
    }

    /**
     * Creates a singleton list of an object out of a name and array values.
     *
     * @param name the name of the field.
     * @param values the values.
     * @return a list containing a single field.
     */
    default List<Field> onlyObject(String name, Field... values) {
      return only(object(name, values));
    }

    /**
     * Creates a singleton list of an object out of a name and a list of values.
     *
     * @param name the name of the field.
     * @param values the values.
     * @return a list containing a single field.
     */
    default List<Field> onlyObject(String name, List<Field> values) {
      return only(object(name, values));
    }

    /**
     * Creates a singleton list of a exception using the default exception name.
     *
     * @param t the value of the field.
     * @return a list containing a single field.
     */
    default List<Field> onlyException(Throwable t) {
      return only(exception(t));
    }

    /**
     * Creates a singleton list of a exception using an explicit name.
     *
     * @param name the name of the field.
     * @param t the value of the field.
     * @return a list containing a single field.
     */
    default List<Field> onlyException(String name, Throwable t) {
      return only(exception(name, t));
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

    protected Value() {}

    /**
     * The underlying raw value.
     *
     * @return the underlying raw value.
     */
    public abstract V raw();

    public abstract ValueType type();

    public String toString() {
      return valueToString(this);
    }

    private String valueToString(Value<?> v) {
      return v.raw() == null ? "null" : v.raw().toString();
    }

    /**
     * Wraps a string with a Value.
     *
     * @param raw the raw string value.
     * @return the Value
     */
    public static Value<String> string(String raw) {
      return new StringValue(raw);
    }

    /**
     * Wraps a number with a Value.
     *
     * @param n the raw number value.
     * @return the Value
     */
    public static Value<Number> number(Number n) {
      return new NumberValue(n);
    }

    /**
     * Wraps a boolean with a Value.
     *
     * @param b the raw boolean value.
     * @return the Value.
     */
    public static Value<Boolean> bool(Boolean b) {
      return new BooleanValue(b);
    }

    /**
     * Returns a `null` value.
     *
     * @return the Value.
     */
    public static NullValue nullValue() {
      return NullValue.instance;
    }

    /**
     * Wraps an exception with a Value.
     *
     * @param t the raw exception value.
     * @return the Value.
     */
    public static Value<Throwable> exception(Throwable t) {
      return new ExceptionValue(t);
    }

    /**
     * Wraps an array of values with a Value.
     *
     * @param values varadic elements of values.
     * @return the Value.
     */
    public static Value<List<Value<?>>> array(Value<?>... values) {
      return new ArrayValue(Arrays.asList(values));
    }

    /**
     * Returns a list of values as a Value.
     *
     * @param values a list of values.
     * @return the Value.
     */
    public static Value<List<Value<?>>> array(List<Value<?>> values) {
      return new ArrayValue(values);
    }

    /**
     * Wraps an array of fields with a Value as an object.
     *
     * @param fields varadic elements of fields.
     * @return the Value.
     */
    public static Value<List<Field>> object(Field... fields) {
      return new ObjectValue(Arrays.asList(fields));
    }

    /**
     * Wraps an list of fields with a Value as an object.
     *
     * @param fields the list of fields.
     * @return the Value.
     */
    public static Value<List<Field>> object(List<Field> fields) {
      return new ObjectValue(fields);
    }

    /**
     * Utility method to turn an array into a list of values with a transformer.
     *
     * @param array the raw array.
     * @param f the function transforming a raw T to a value of T
     * @return list of values.
     * @param <T> the raw type
     */
    public static <T> List<Value<?>> asList(T[] array, Function<T, Value<T>> f) {
      return Arrays.stream(array).map(f).collect(Collectors.toList());
    }

    public enum ValueType {
      ARRAY,
      OBJECT,
      STRING,
      NUMBER,
      BOOLEAN,
      EXCEPTION,
      NULL
    }

    public static final class BooleanValue extends Value<Boolean> {
      private final Boolean bool;

      private BooleanValue(Boolean bool) {
        this.bool = bool;
      }

      @Override
      public Boolean raw() {
        return this.bool;
      }

      @Override
      public ValueType type() {
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
      public ValueType type() {
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
      public ValueType type() {
        return ValueType.STRING;
      }
    }

    public static final class ArrayValue extends Value<List<Field.Value<?>>> {
      private final List<Field.Value<?>> raw;

      private ArrayValue(List<Field.Value<?>> raw) {
        this.raw = raw;
      }

      @Override
      public List<Field.Value<?>> raw() {
        return raw;
      }

      @Override
      public ValueType type() {
        return ValueType.ARRAY;
      }
    }

    public static final class ObjectValue extends Value<List<Field>> {
      private final List<Field> raw;

      private ObjectValue(List<Field> raw) {
        this.raw = raw;
      }

      @Override
      public ValueType type() {
        return ValueType.OBJECT;
      }

      @Override
      public List<Field> raw() {
        return raw;
      }
    }

    public static final class NullValue extends Value<Object> {
      // Should not be able to instantiate this outside of class.
      private NullValue() {}

      @Override
      public Object raw() {
        return null;
      }

      @Override
      public ValueType type() {
        return ValueType.NULL;
      }

      public static NullValue instance = new NullValue();
    }

    public static final class ExceptionValue extends Value<Throwable> {
      private final Throwable raw;

      private ExceptionValue(Throwable raw) {
        this.raw = raw;
      }

      @Override
      public ValueType type() {
        return ValueType.EXCEPTION;
      }

      @Override
      public Throwable raw() {
        return raw;
      }
    }
  }
}
