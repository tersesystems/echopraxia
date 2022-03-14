package com.tersesystems.echopraxia.scripting;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.LoggingContext;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class ContextFunction implements Arity1UserFunction {
  private final HashMap<String, Value> functionMap;

  public ContextFunction(LoggingContext ctx) {
    this.functionMap = new HashMap<>();
    functionMap.put("get_fields", arity0FunctionValue(userCtx -> convertFields(ctx.getFields())));
    functionMap.put("find_number", userFunctionValue(optionalFunction(ctx::findNumber)));
    functionMap.put("find_string", userFunctionValue(optionalFunction(ctx::findString)));
    functionMap.put("find_boolean", userFunctionValue(optionalFunction(ctx::findBoolean)));
    functionMap.put("find_object", userFunctionValue(optionalFunction(ctx::findObject)));
    functionMap.put("find_list", userFunctionValue(listFunction(ctx::findList)));
    functionMap.put("find_null", userFunctionValue(nullFunction(ctx::findNull)));
  }

  @Override
  public Value call(UserCallContext context, Value functionNameValue) {
    final String string = functionNameValue.string();
    final Value value = functionMap.get(string);
    if (value == null) {
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "Unknown function name " + string);
    }
    return value;
  }

  @NotNull
  private Value userFunctionValue(Arity1UserFunction userFunction) {
    return Values.make(
        new UserFunctionValue(
            new FunctionSignature(
                Collections.singletonList(
                    new FunctionParameter(0, "jsonType", Types.STRING, Values.NIL)),
                Types.ANY),
            userFunction));
  }

  private Value arity0FunctionValue(Arity0UserFunction userFunction) {
    return Values.make(
        new UserFunctionValue(
            new FunctionSignature(Collections.emptyList(), Types.ANY), userFunction));
  }

  Arity1UserFunction optionalFunction(Function<String, Optional<?>> contextFunction) {
    return (context, pathValue) -> {
      final String path = pathValue.string();
      final Optional<?> opt = contextFunction.apply(path);
      if (opt.isPresent()) {
        return Values.make(opt.get());
      }
      return Values.NIL;
    };
  }

  private Arity1UserFunction listFunction(Function<String, List<?>> listFunction) {
    return (context, pathValue) -> {
      final String path = pathValue.string();
      final List<?> list = listFunction.apply(path);
      return Values.make(list);
    };
  }

  private Arity1UserFunction nullFunction(Function<String, Boolean> nullFunction) {
    return (context, pathValue) -> {
      final String path = pathValue.string();
      final Boolean result = nullFunction.apply(path);
      return Values.make(result);
    };
  }

  private com.twineworks.tweakflow.lang.values.Value convertFields(List<Field> fields) {
    Map<String, Value> objectMap = new HashMap<>();
    for (Field field : fields) {
      com.twineworks.tweakflow.lang.values.Value fieldValue = convertValue(field.value());
      objectMap.put(field.name(), fieldValue);
    }
    return Values.make(objectMap);
  }

  private com.twineworks.tweakflow.lang.values.Value convertValue(Field.Value<?> value) {
    switch (value.type()) {
      case ARRAY:
        //noinspection unchecked
        List<Field.Value<?>> values = (List<Field.Value<?>>) value.raw();
        List<com.twineworks.tweakflow.lang.values.Value> rawList = new ArrayList<>();
        for (Field.Value<?> v : values) {
          com.twineworks.tweakflow.lang.values.Value v2 = convertValue(v);
          rawList.add(v2);
        }
        return Values.make(rawList);
      case OBJECT:
        //noinspection unchecked
        List<Field> fields = (List<Field>) value.raw();
        return convertFields(fields);
      case STRING:
        String s = (String) value.raw();
        return Values.make(s);
      case NUMBER:
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

  private com.twineworks.tweakflow.lang.values.Value createThrowable(Throwable t) {
    final String message = t.getMessage();
    StringWriter stringWriter = new StringWriter();
    t.printStackTrace(new PrintWriter(stringWriter));
    String stackTrace = stringWriter.toString();

    Map<String, com.twineworks.tweakflow.lang.values.Value> throwMap = new HashMap<>();
    throwMap.put("message", Values.make(message));
    throwMap.put("stackTrace", Values.make(stackTrace));

    if (t.getCause() != null) {
      com.twineworks.tweakflow.lang.values.Value cause = createThrowable(t.getCause());
      throwMap.put("cause", cause);
    }
    return Values.makeDict(throwMap);
  }
}
