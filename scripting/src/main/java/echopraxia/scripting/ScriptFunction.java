package echopraxia.scripting;

import static com.twineworks.tweakflow.lang.values.Values.*;

import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.*;
import echopraxia.api.Field;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class contains a builder for creating user defined script functions, and exposes some
 * utility methods.
 */
public final class ScriptFunction {

  public static Builder builder() {
    return new Builder();
  }

  /** A builder class for ScriptFunction. */
  public static final class Builder {

    private final List<FunctionParameter> parameterList = new ArrayList<>();
    private Type resultType = Types.ANY;

    private UserFunction userFunction;

    /**
     * Adds an input function parameter for the user function.
     *
     * @param parameter the function parameter
     * @return the builder
     */
    public Builder parameter(FunctionParameter parameter) {
      this.parameterList.add(parameter);
      return this;
    }

    /**
     * Sets a result type for the user function. If this method is not called, the default type is
     * ANY.
     *
     * @param resultType a type from the Types class, i.e. Types.DATETIME.
     * @return the builder.
     */
    public Builder result(Type resultType) {
      this.resultType = resultType;
      return this;
    }

    /**
     * Sets a supplier for the user function. You should not set any parameters.
     *
     * @return the new builder
     */
    public Builder supplier(Supplier<Value> supplier) {
      this.userFunction = (Arity0UserFunction) userCtx -> supplier.get();
      return this;
    }

    /**
     * Sets a function for the user function.
     *
     * @return the new builder
     */
    public Builder function(Function<Value, Value> fn) {
      this.userFunction = (Arity1UserFunction) (context, pathValue) -> fn.apply(pathValue);
      return this;
    }

    /**
     * Sets an optional function for the user function. You should not set the result type, it
     * should be ANY.
     *
     * @return the new builder
     */
    public Builder optionalFunction(Function<Value, Optional<Value>> optFunction) {
      this.userFunction =
          (Arity1UserFunction) (context, pathValue) -> optFunction.apply(pathValue).orElse(NIL);
      return this;
    }

    /**
     * Sets a bifunction for the user function.
     *
     * @return the new builder
     */
    public Builder biFunction(BiFunction<Value, Value, Value> booleanFunction) {
      this.userFunction =
          (Arity2UserFunction) (context, first, second) -> booleanFunction.apply(first, second);
      return this;
    }

    /**
     * Sets an optional bifunction for the user function. You should not set the result type, it
     * should be ANY.
     *
     * @return the new builder
     */
    public Builder optionalBiFunction(BiFunction<Value, Value, Optional<Value>> booleanFunction) {
      this.userFunction =
          (Arity2UserFunction)
              (context, first, second) -> booleanFunction.apply(first, second).orElse(NIL);
      return this;
    }

    /**
     * Sets a varadic function for the user function.
     *
     * @return the new builder
     */
    public Builder varadicFunction(Function<Value[], Value> varadicFunction) {
      this.userFunction = (ArityNUserFunction) (context, args) -> varadicFunction.apply(args);
      return this;
    }

    /**
     * Sets an optional varadic function for the user function. You should not set the result type,
     * it should be ANY.
     *
     * @return the new builder
     */
    public Builder optionalVaradicFunction(Function<Value[], Optional<Value>> varadicFunction) {
      this.userFunction =
          (ArityNUserFunction) (context, args) -> varadicFunction.apply(args).orElse(NIL);
      return this;
    }

    /**
     * Builds the user function value
     *
     * @return the user function value
     */
    public UserFunctionValue build() {
      FunctionSignature signature = new FunctionSignature(parameterList, resultType);
      return new UserFunctionValue(signature, userFunction);
    }
  }

  public static Value convertFields(List<Field> fields) {
    Map<String, Value> fieldMap = new HashMap<>();
    for (Field field : fields) {
      Value fieldValue = convertValue(field.value());
      fieldMap.put(field.name(), fieldValue);
    }
    return make(new DictValue(fieldMap));
  }

  public static Value convertValue(echopraxia.api.Value<?> value) {
    switch (value.type()) {
      case ARRAY:
        return convertArray((echopraxia.api.Value<List<echopraxia.api.Value<?>>>) value);
      case OBJECT:
        //noinspection unchecked
        List<Field> fields = (List<Field>) value.raw();
        return convertFields(fields);
      case STRING:
        String s = (String) value.raw();
        return make(s);
      case NUMBER:
        return convertNumber(value);
      case BOOLEAN:
        Boolean b = (Boolean) value.raw();
        return make(b);
      case EXCEPTION:
        return convertThrowable((Throwable) value.raw());
      case NULL:
        return NIL;
      default:
        throw new IllegalStateException("Unknown state " + value.type());
    }
  }

  public static Value convertNumber(echopraxia.api.Value<?> value) {
    // Speed up conversion by using overloaded value directly
    Number o = (Number) value.raw();
    if (o instanceof Byte) return make(o.longValue());
    if (o instanceof Short) return make(o.longValue());
    if (o instanceof Long) return make(o.longValue());
    if (o instanceof Integer) return make(o.longValue());
    if (o instanceof Float) return make((Float) o);
    if (o instanceof Double) return make((Double) o);
    if (o instanceof BigDecimal) return make((BigDecimal) o);
    // Tweakflow doesn't have a BigInteger representation, we must hack it for now
    // this is fixed in
    // https://github.com/twineworks/tweakflow/commit/cd0d2412d9826028ccd9ce412a35e2d17086e985
    if (o instanceof BigInteger) return make(new BigDecimal((BigInteger) o));
    throw new IllegalStateException("Unknown number type " + o.getClass().getName());
  }

  private static Value convertArray(echopraxia.api.Value<List<echopraxia.api.Value<?>>> value) {
    List<echopraxia.api.Value<?>> values = value.raw();
    List<Value> rawList = new ArrayList<>(values.size());
    for (echopraxia.api.Value<?> v : values) {
      Value v2 = convertValue(v);
      rawList.add(v2);
    }
    return make(new ListValue(rawList));
  }

  public static Value convertThrowable(Throwable t) {
    String message = t.getMessage();
    String stackTrace = getStackTrace(t);

    Map<String, Value> exceptionMap = new HashMap<>();
    exceptionMap.put("message", make(message));
    exceptionMap.put("stackTrace", make(stackTrace));
    if (t.getCause() != null) {
      Value cause = convertThrowable(t.getCause());
      exceptionMap.put("cause", cause);
    }
    return make(new DictValue(exceptionMap));
  }

  private static String getStackTrace(Throwable t) {
    StringWriter stringWriter = new StringWriter();
    t.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }
}
