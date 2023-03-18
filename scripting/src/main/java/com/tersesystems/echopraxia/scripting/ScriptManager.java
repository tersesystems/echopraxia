package com.tersesystems.echopraxia.scripting;

import static com.tersesystems.echopraxia.scripting.ScriptFunctions.*;

import com.tersesystems.echopraxia.api.Level;
import com.tersesystems.echopraxia.api.LoggingContext;
import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.*;
import java.time.Instant;
import java.util.*;

/**
 * ScriptManager class.
 *
 * <p>This does the work of evaluating a Tweakflow script from a ScriptHandle.
 */
public class ScriptManager {

  private static final ValueMapEntry NOW_FUNCTION =
      addUserFunction("now", () -> Values.make(Instant.now()));

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
      Value levelV = getLevelV(level);
      List<ValueMapEntry> functionMapList = new ArrayList<>();
      functionMapList.add(NOW_FUNCTION);
      addContextFunctions(functionMapList, context);
      DictValue dictValue = new DictValue(functionMapList.toArray(new ValueMapEntry[0]));
      Value functionMapValue = Values.make(dictValue);
      Value retValue = call(levelV, functionMapValue);
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

  private Value getLevelV(Level level) {
    switch (level) {
      case TRACE:
        return TRACE_VALUE;
      case DEBUG:
        return DEBUG_VALUE;
      case INFO:
        return INFO_VALUE;
      case WARN:
        return WARN_VALUE;
      case ERROR:
        return ERROR_VALUE;
      default:
        throw new IllegalStateException("Unknown level " + level);
    }
  }

  private void addContextFunctions(List<ValueMapEntry> functionMapList, LoggingContext ctx) {
    functionMapList.add(
        new ValueMapEntry(
            "fields", arity0FunctionValue(userCtx -> convertFields(ctx.getFields()))));
    functionMapList.add(
        new ValueMapEntry("find_number", userFunctionValue(optionalFunction(ctx::findNumber))));
    functionMapList.add(
        new ValueMapEntry("find_string", userFunctionValue(optionalFunction(ctx::findString))));
    functionMapList.add(
        new ValueMapEntry("find_boolean", userFunctionValue(optionalFunction(ctx::findBoolean))));
    functionMapList.add(
        new ValueMapEntry("find_list", userFunctionValue(listFunction(ctx::findList))));
    functionMapList.add(
        new ValueMapEntry("find_null", userFunctionValue(nullFunction(ctx::findNull))));
  }

  private Value call(Value level, Value fields) {
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
}
