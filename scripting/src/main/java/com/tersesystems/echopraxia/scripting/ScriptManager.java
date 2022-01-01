package com.tersesystems.echopraxia.scripting;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Level;
import com.tersesystems.echopraxia.LoggingContext;
import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
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
  protected AtomicReference<Runtime.Module> mref = new AtomicReference<>();

  public ScriptManager(ScriptHandle handle) {
    this.handle = handle;
  }

  public boolean execute(boolean df, Level level, LoggingContext context) {
    try {
      if (mref.get() == null || handle.isInvalid()) {
        String script = handle.script();
        eval(script);
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
    // Tweakwork only understands simple values, so only things that
    // are not array or object
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
        List<Field.Value<?>> values = (List<Field.Value<?>>) value.raw();
        List<Value> rawList = new ArrayList<>();
        for (Field.Value<?> v : values) {
          Value v2 = convertValue(v);
          rawList.add(v2);
        }
        return Values.make(rawList);
      case OBJECT:
        List<Field> fields = (List<Field>) value.raw();
        return convertFields(fields);
      case STRING:
        String s = (String) value.raw();
        return Values.make(s);
      case NUMBER:
        Number n = (Number) value.raw();
        return Values.make(n);
      case BOOLEAN:
        Boolean b = (Boolean) value.raw();
        return Values.make(b);
      case EXCEPTION:
        Throwable t = (Throwable) value.raw();
        return Values.make(t);
      case NULL:
        return Values.make((Object) null);
      default:
        throw new IllegalStateException("Unknown state " + value.type());
    }
  }

  public Runtime.Module compileModule(String script) {
    String path = handle.path();
    MemoryLocation memLocation = new MemoryLocation.Builder().add(path, script).build();
    LoadPath loadPath = new LoadPath.Builder().addStdLocation().add(memLocation).build();
    Runtime runtime = TweakFlow.compile(loadPath, path);
    return runtime.getModules().get(runtime.unitKey(path));
  }

  protected boolean call(Value level, Value fields) {
    Runtime.Module module = mref.get();
    Runtime.Var callSite = module.getLibrary(handle.libraryName()).getVar(handle.functionName());

    Value call = callSite.call(level, fields);
    return call.bool();
  }

  public void eval(String script) {
    Runtime.Module module = compileModule(script);
    module.evaluate();
    mref.set(module);
  }
}
