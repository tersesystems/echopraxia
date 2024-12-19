package echopraxia.scripting;

import static echopraxia.scripting.ScriptFunction.*;

import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.*;
import echopraxia.api.Level;
import echopraxia.api.LoggingContext;
import java.util.*;
import java.util.function.Function;

/**
 * ScriptManager class.
 *
 * <p>This does the work of evaluating a Tweakflow script from a ScriptHandle.
 *
 * <p>You can add custom and "impure" functions by using addUserFunction.
 */
public class ScriptManager {

  public static final Value TRACE_VALUE = Values.make(Level.TRACE.name());
  public static final Value DEBUG_VALUE = Values.make(Level.DEBUG.name());
  public static final Value INFO_VALUE = Values.make(Level.INFO.name());
  public static final Value WARN_VALUE = Values.make(Level.WARN.name());
  public static final Value ERROR_VALUE = Values.make(Level.ERROR.name());

  private static final List<FunctionParameter> JSON_PARAMETER =
      Collections.singletonList(new FunctionParameter(0, "jsonPath", Types.STRING, Values.NIL));

  private static final FunctionSignature JSON_PATH_BOOLEAN_FUNCTION_SIGNATURE =
      new FunctionSignature(JSON_PARAMETER, Types.BOOLEAN);

  private static final FunctionSignature JSON_PATH_LIST_FUNCTION_SIGNATURE =
      new FunctionSignature(JSON_PARAMETER, Types.LIST);

  private static final FunctionSignature JSON_PATH_ANY_FUNCTION_SIGNATURE =
      new FunctionSignature(JSON_PARAMETER, Types.ANY);

  // () => Any
  private static final FunctionSignature SUPPLIER_ANY_SIGNATURE =
      new FunctionSignature(Collections.emptyList(), Types.ANY);

  private final ScriptHandle handle;
  private Arity2CallSite callSite;

  private final Object lock = new Object();

  private Function<LoggingContext, List<ValueMapEntry>> userFunctions =
      ctx -> Collections.emptyList();

  public ScriptManager(ScriptHandle handle) {
    this.handle = handle;
  }

  public void setUserFunctions(Function<LoggingContext, List<ValueMapEntry>> userFunctions) {
    this.userFunctions = userFunctions;
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
      List<ValueMapEntry> functionMapList = new ArrayList<>(userFunctions.apply(context));
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
        ValueMapEntry.make(
            "fields",
            new UserFunctionValue(
                SUPPLIER_ANY_SIGNATURE,
                (Arity0UserFunction) userCtx -> convertFields(ctx.getFields()))));
    functionMapList.add(
        ValueMapEntry.make(
            "find_number",
            new UserFunctionValue(
                JSON_PATH_ANY_FUNCTION_SIGNATURE, optionalFunction(ctx::findNumber))));
    functionMapList.add(
        ValueMapEntry.make(
            "find_string",
            new UserFunctionValue(
                JSON_PATH_ANY_FUNCTION_SIGNATURE, optionalFunction(ctx::findString))));
    functionMapList.add(
        ValueMapEntry.make(
            "find_boolean",
            new UserFunctionValue(
                JSON_PATH_ANY_FUNCTION_SIGNATURE, optionalFunction(ctx::findBoolean))));
    functionMapList.add(
        ValueMapEntry.make(
            "find_object",
            new UserFunctionValue(
                JSON_PATH_ANY_FUNCTION_SIGNATURE, optionalFunction(ctx::findObject))));
    functionMapList.add(
        ValueMapEntry.make(
            "find_list",
            new UserFunctionValue(JSON_PATH_LIST_FUNCTION_SIGNATURE, listFunction(ctx::findList))));
    functionMapList.add(
        ValueMapEntry.make(
            "find_null",
            new UserFunctionValue(
                JSON_PATH_BOOLEAN_FUNCTION_SIGNATURE, booleanFunction(ctx::findNull))));
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

  private Arity1UserFunction optionalFunction(Function<String, Optional<?>> contextFunction) {
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
      return Values.makeList(list);
    };
  }

  private Arity1UserFunction booleanFunction(Function<String, Boolean> booleanFunction) {
    return (context, pathValue) -> {
      final String path = pathValue.string();
      final Boolean result = booleanFunction.apply(path);
      return Values.make(result);
    };
  }
}
