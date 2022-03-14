package com.tersesystems.echopraxia.scripting;

import static java.util.Collections.singletonList;

import com.tersesystems.echopraxia.Level;
import com.tersesystems.echopraxia.LoggingContext;
import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.*;

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
     function evaluate: (string level, function ctx) -> boolean
        let {
          findNumber: ctx("findNumber");
          findString: ctx("findString");
        }
        findNumber("$.age") == 3 && findString("$.name") == "Will";
   }
  */
  public boolean execute(boolean df, Level level, LoggingContext context) {
    try {
      Value levelV = Values.make(level.name());
      Value userFunctionValue = compileContextFunction(context);
      Value retValue = call(levelV, userFunctionValue);
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

  private Value compileContextFunction(LoggingContext context) {
    FunctionParameter functionParameter =
        new FunctionParameter(0, "functionName", Types.STRING, Values.NIL);
    FunctionSignature functionName =
        new FunctionSignature(singletonList(functionParameter), Types.FUNCTION);
    return Values.make(new UserFunctionValue(functionName, new ContextFunction(context)));
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
