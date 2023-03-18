package com.tersesystems.echopraxia.scripting;

import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.Level;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public final class ScriptFunctions {

  public static final Value TRACE_VALUE = Values.make(Level.TRACE.name());
  public static final Value DEBUG_VALUE = Values.make(Level.DEBUG.name());
  public static final Value INFO_VALUE = Values.make(Level.INFO.name());
  public static final Value WARN_VALUE = Values.make(Level.WARN.name());
  public static final Value ERROR_VALUE = Values.make(Level.ERROR.name());

  public static final FunctionSignature JSON_TYPE_FUNCTION_SIGNATURE =
      new FunctionSignature(
          Collections.singletonList(new FunctionParameter(0, "jsonType", Types.STRING, Values.NIL)),
          Types.ANY);
  public static final FunctionSignature ANY_FUNCTION_SIGNATURE =
      new FunctionSignature(Collections.emptyList(), Types.ANY);

  public static Arity1UserFunction optionalFunction(Function<String, Optional<?>> contextFunction) {
    return (context, pathValue) -> {
      final String path = pathValue.string();
      final Optional<?> opt = contextFunction.apply(path);
      if (opt.isPresent()) {
        return Values.make(opt.get());
      }
      return Values.NIL;
    };
  }

  public static Arity1UserFunction listFunction(Function<String, List<?>> listFunction) {
    return (context, pathValue) -> {
      final String path = pathValue.string();
      final List<?> list = listFunction.apply(path);
      return Values.makeList(list);
    };
  }

  public static Arity1UserFunction nullFunction(Function<String, Boolean> nullFunction) {
    return (context, pathValue) -> {
      final String path = pathValue.string();
      final Boolean result = nullFunction.apply(path);
      return Values.make(result);
    };
  }

  public static ValueMapEntry addUserFunction(String functionName, Supplier<Value> valueSupplier) {
    return new ValueMapEntry(functionName, arity0FunctionValue(userCtx -> valueSupplier.get()));
  }

  @NotNull
  public static Value userFunctionValue(Arity1UserFunction userFunction) {
    return Values.make(new UserFunctionValue(JSON_TYPE_FUNCTION_SIGNATURE, userFunction));
  }

  public static Value arity0FunctionValue(Arity0UserFunction userFunction) {
    return Values.make(new UserFunctionValue(ANY_FUNCTION_SIGNATURE, userFunction));
  }

  public static Value convertFields(List<Field> fields) {
    ValueMapEntry[] array = new ValueMapEntry[fields.size()];
    for (int i = 0; i < fields.size(); i++) {
      Field field = fields.get(i);
      Value fieldValue = convertValue(field.value());
      array[i] = new ValueMapEntry(field.name(), fieldValue);
    }
    return Values.make(new DictValue(array));
  }

  public static Value convertValue(com.tersesystems.echopraxia.api.Value<?> value) {
    switch (value.type()) {
      case ARRAY:
        return convertArray(value);
      case OBJECT:
        //noinspection unchecked
        List<Field> fields = (List<Field>) value.raw();
        return convertFields(fields);
      case STRING:
        String s = (String) value.raw();
        return Values.make(s);
      case NUMBER:
        return convertNumber(value);
      case BOOLEAN:
        Boolean b = (Boolean) value.raw();
        return Values.make(b);
      case EXCEPTION:
        return createThrowable((Throwable) value.raw());
      case NULL:
        return Values.NIL;
      default:
        throw new IllegalStateException("Unknown state " + value.type());
    }
  }

  public static Value convertNumber(com.tersesystems.echopraxia.api.Value<?> value) {
    // Speed up conversion by using overloaded value directly
    Number o = (Number) value.raw();
    if (o instanceof Byte) return Values.make(o.longValue());
    if (o instanceof Short) return Values.make(o.longValue());
    if (o instanceof Long) return Values.make(o.longValue());
    if (o instanceof Integer) return Values.make(o.longValue());
    if (o instanceof Float) return Values.make((Float) o);
    if (o instanceof Double) return Values.make((Double) o);
    if (o instanceof BigDecimal) return Values.make((BigDecimal) o);
    // Tweakflow doesn't have a BigInteger representation, we must hack it for now
    // this is fixed in
    // https://github.com/twineworks/tweakflow/commit/cd0d2412d9826028ccd9ce412a35e2d17086e985
    if (o instanceof BigInteger) return Values.make(new BigDecimal((BigInteger) o));
    throw new IllegalStateException("Unknown number type " + o.getClass().getName());
  }

  public static Value convertArray(com.tersesystems.echopraxia.api.Value<?> value) {
    //noinspection unchecked
    List<com.tersesystems.echopraxia.api.Value<?>> values =
        (List<com.tersesystems.echopraxia.api.Value<?>>) value.raw();
    List<Value> rawList = new ArrayList<>(values.size());
    for (com.tersesystems.echopraxia.api.Value<?> v : values) {
      Value v2 = convertValue(v);
      rawList.add(v2);
    }
    return Values.make(new ListValue(rawList));
  }

  public static Value createThrowable(Throwable t) {
    String message = t.getMessage();
    String stackTrace = getStackTrace(t);

    ValueMapEntry messageValue = new ValueMapEntry("message", Values.make(message));
    ValueMapEntry stacktraceValue = new ValueMapEntry("stackTrace", Values.make(stackTrace));
    if (t.getCause() != null) {
      Value cause = createThrowable(t.getCause());
      ValueMapEntry causeValue = new ValueMapEntry("cause", cause);
      ValueMapEntry[] array = {messageValue, stacktraceValue, causeValue};
      return Values.make(new DictValue(array));
    } else {
      ValueMapEntry[] array = {messageValue, stacktraceValue};
      return Values.make(new DictValue(array));
    }
  }

  private static String getStackTrace(Throwable t) {
    StringWriter stringWriter = new StringWriter();
    t.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }
}
