package com.tersesystems.echopraxia.scripting;

import com.tersesystems.echopraxia.api.Field;
import com.tersesystems.echopraxia.api.Level;
import com.tersesystems.echopraxia.api.LoggingContext;
import com.tersesystems.echopraxia.api.Value;
import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * ScriptManager class.
 *
 * <p>This does the work of evaluating a Tweakflow script from a ScriptHandle.
 */
public class ScriptManager {

  private final ScriptHandle handle;
  private Arity2CallSite callSite;

  private final Object lock = new Object();

  public ScriptManager(ScriptHandle handle) {
    this.handle = handle;
  }

  /*
   library echopraxia {
     function evaluate: (string level, dict ctx) -> boolean
        let {
          findNumber: ctx[:find_number];
          findString: ctx[:find_string];
        }
        findNumber("$.age") == 3 && findString("$.name") == "Will";
   }
  */
  public boolean execute(boolean df, Level level, LoggingContext context) {
    try {
      com.twineworks.tweakflow.lang.values.Value levelV = Values.make(level.name());
      com.twineworks.tweakflow.lang.values.Value functionMapValue =
          Values.make(createFunctionMap(context));
      com.twineworks.tweakflow.lang.values.Value retValue = call(levelV, functionMapValue);
      if (!retValue.isBoolean()) {
        throw new ScriptException(
            "Your function needs to return a boolean value!  Invalid return type: "
                + retValue.type());
      }
      return retValue.bool();
    } catch (Exception e) {
      handle.report(e);
      return df; // pass the default through on exception.
    }
  }

  protected DictValue createFunctionMap(LoggingContext ctx) {
    // protected because users should be able to override this given a custom logging context
    Map<String, com.twineworks.tweakflow.lang.values.Value> functionMap = new HashMap<>();
    functionMap.put("fields", arity0FunctionValue(userCtx -> convertFields(ctx.getFields())));
    functionMap.put("find_number", userFunctionValue(optionalFunction(ctx::findNumber)));
    functionMap.put("find_string", userFunctionValue(optionalFunction(ctx::findString)));
    functionMap.put("find_boolean", userFunctionValue(optionalFunction(ctx::findBoolean)));
    functionMap.put("find_object", userFunctionValue(optionalFunction(ctx::findObject)));
    functionMap.put("find_list", userFunctionValue(listFunction(ctx::findList)));
    functionMap.put("find_null", userFunctionValue(nullFunction(ctx::findNull)));

    return new DictValue(functionMap);
  }

  private com.twineworks.tweakflow.lang.values.Value call(
      com.twineworks.tweakflow.lang.values.Value level,
      com.twineworks.tweakflow.lang.values.Value fields) {
    synchronized (lock) {
      // if there's no callsite or the handle is bad, we need to eval the script
      // probably safest to do this in a single thread in synchronized block?
      //
      // The handle will only be invalid for _one_ call, so if you get an exception,
      // it won't try to recompile it again, and you'll get the previous successfully
      // evaluated call-site next round.
      //
      // If the callsite is null and the script is bad, then this is the first time
      // you've called the script and it WILL keep trying until it works.  It
      // will throw an exception and return the default value so that conditional
      // logging is not blocked in the meanwhile.
      if (callSite == null || handle.isInvalid()) {
        String script = handle.script();
        Runtime.Module module = compileModule(script);
        module.evaluate();
        Runtime.Var var = module.getLibrary(handle.libraryName()).getVar(handle.functionName());
        callSite = var.arity2CallSite();
      }
      // Callsite is not threadsafe, so only one thread can execute it at a time
      return callSite.call(level, fields);
    }
  }

  private Runtime.Module compileModule(String script) {
    String path = handle.path();
    MemoryLocation memLocation = new MemoryLocation.Builder().add(path, script).build();
    LoadPath loadPath = new LoadPath.Builder().addStdLocation().add(memLocation).build();
    Runtime runtime = TweakFlow.compile(loadPath, path);
    return runtime.getModules().get(runtime.unitKey(path));
  }

  @NotNull
  private com.twineworks.tweakflow.lang.values.Value userFunctionValue(
      Arity1UserFunction userFunction) {
    return Values.make(
        new UserFunctionValue(
            new FunctionSignature(
                Collections.singletonList(
                    new FunctionParameter(0, "jsonType", Types.STRING, Values.NIL)),
                Types.ANY),
            userFunction));
  }

  private com.twineworks.tweakflow.lang.values.Value arity0FunctionValue(
      Arity0UserFunction userFunction) {
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
    Map<String, com.twineworks.tweakflow.lang.values.Value> objectMap = new HashMap<>();
    for (Field field : fields) {
      com.twineworks.tweakflow.lang.values.Value fieldValue = convertValue(field.value());
      objectMap.put(field.name(), fieldValue);
    }
    return Values.make(objectMap);
  }

  private com.twineworks.tweakflow.lang.values.Value convertValue(Value<?> value) {
    switch (value.type()) {
      case ARRAY:
        //noinspection unchecked
        List<Value<?>> values = (List<Value<?>>) value.raw();
        List<com.twineworks.tweakflow.lang.values.Value> rawList = new ArrayList<>();
        for (Value<?> v : values) {
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
