package com.tersesystems.echopraxia.scripting;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Level;
import com.tersesystems.echopraxia.LoggingContext;
import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.Arity2CallSite;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ScriptManager class.
 *
 * <p>This does the work of evaluating a Tweakflow script from a ScriptHandle.
 */
public class ScriptManager {

  private final ScriptHandle handle;
  private final AtomicReference<Runtime.Module> mref = new AtomicReference<>();

  // Callsite is faster but not thread-safe, so use a thread-local
  private final ThreadLocal<Arity2CallSite> callSiteThreadLocal = new ThreadLocal<>();

  public ScriptManager(ScriptHandle handle) {
    this.handle = handle;
  }

  public boolean execute(boolean df, Level level, LoggingContext context) {
    try {
      if (mref.get() == null || handle.isInvalid()) {
        String script = handle.script();
        eval(script);
      }
      if (callSiteThreadLocal.get() == null) {
        refreshCallSite();
      }
      Value levelV = Values.make(level.name());
      Value fieldsV = convertFields(context.getFields());
      return call(levelV, fieldsV);
    } catch (Exception e) {
      handle.report(e);
      return df; // pass the default through on exception.
    }
  }

  private Value convertFields(List<Field> fields) {
    Map<String, Value> objectMap = new HashMap<>();
    for (Field field : fields) {
      Value fieldValue = convertValue(field.value());
      objectMap.put(field.name(), fieldValue);
    }

    return Values.make(objectMap);
  }

  private Value convertValue(Field.Value<?> value) {
    switch (value.type()) {
      case ARRAY:
        //noinspection unchecked
        List<Field.Value<?>> values = (List<Field.Value<?>>) value.raw();
        List<Value> rawList = new ArrayList<>();
        for (Field.Value<?> v : values) {
          Value v2 = convertValue(v);
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

  private Value createThrowable(Throwable t) {
    final String message = t.getMessage();
    StringWriter stringWriter = new StringWriter();
    t.printStackTrace(new PrintWriter(stringWriter));
    String stackTrace = stringWriter.toString();

    Map<String, Value> throwMap = new HashMap<>();
    throwMap.put("message", Values.make(message));
    throwMap.put("stackTrace", Values.make(stackTrace));

    if (t.getCause() != null) {
      Value cause = createThrowable(t.getCause());
      throwMap.put("cause", cause);
    }
    return Values.makeDict(throwMap);
  }

  Runtime.Module compileModule(String script) {
    String path = handle.path();
    MemoryLocation memLocation = new MemoryLocation.Builder().add(path, script).build();
    LoadPath loadPath = new LoadPath.Builder().addStdLocation().add(memLocation).build();
    Runtime runtime = TweakFlow.compile(loadPath, path);
    return runtime.getModules().get(runtime.unitKey(path));
  }

  protected boolean call(Value level, Value fields) {
    Value call = callSiteThreadLocal.get().call(level, fields);
    if (call.isBoolean()) {
      return call.bool();
    } else {
      throw new ScriptException(
          "Your function needs to return a boolean value!  Invalid return type: " + call.type());
    }
  }

  void eval(String script) {
    Runtime.Module module = compileModule(script);
    module.evaluate();
    mref.set(module);
    callSiteThreadLocal.set(null);
  }

  void refreshCallSite() {
    Runtime.Var var = mref.get().getLibrary(handle.libraryName()).getVar(handle.functionName());
    callSiteThreadLocal.set(var.arity2CallSite());
  }
}
