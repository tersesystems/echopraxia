package echopraxia.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
public interface Field
    extends FieldBuilderResult, PresentationHints<Field>, AttributesAware<Field> {

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
   * Creates a value only field exposing only the Field interface.
   *
   * @return a field with the given name and value, displayed as value only.
   */
  @NotNull
  static Field value(@NotNull String name, @NotNull Value<?> value) {
    return create(
        name, value, PresentationHintAttributes.valueOnlyAttributes(), DefaultField.class);
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
    return create(name, value, PresentationHintAttributes.valueOnlyAttributes(), fieldClass);
  }

  /**
   * Creates a field exposing only the Field interface.
   *
   * @return a field with the given name and value displayed as key=value
   */
  @NotNull
  static Field keyValue(@NotNull String name, @NotNull Value<?> value) {
    return create(name, value, Attributes.empty(), DefaultField.class);
  }

  /**
   * Creates a field using the fieldClass as the returned type.
   *
   * @return a field with the given name and value displayed as key=value
   * @since 3.0
   */
  static <F extends Field> F keyValue(
      @NotNull String name, @NotNull Value<?> value, Class<F> fieldClass) {
    return create(name, value, Attributes.empty(), fieldClass);
  }

  /**
   * The base create method for fields.
   *
   * @param name the name of the field
   * @param value the value of the field
   * @param attributes the attributes of the field
   * @param fieldClass the field's class.
   * @return a new instance of the field.
   * @param <F> field type.
   */
  static <F extends Field> @NotNull F create(
      @NotNull String name, @NotNull Value<?> value, Attributes attributes, Class<F> fieldClass) {
    if (fieldClass == DefaultField.class) {
      //noinspection unchecked
      return (F) new DefaultField(name, value, attributes);
    } else {
      try {
        Constructor<F> constructor =
            fieldClass.getConstructor(String.class, Value.class, Attributes.class);
        return constructor.newInstance(name, value, attributes);
      } catch (NoSuchMethodException
          | InstantiationException
          | IllegalAccessException
          | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }
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
