package com.tersesystems.echopraxia.scripting;

import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.runtime.Runtime;

public class ScriptManager {

  // https://github.com/tersesystems/blindsight/blob/master/scripting/src/main/scala/com/tersesystems/blindsight/scripting/ScriptManager.scala

  public Runtime.Module compileModule(String script) {
    MemoryLocation memLocation = new MemoryLocation.Builder().add(path, script).build();
    LoadPath loadPath = new LoadPath.Builder().addStdLocation().add(memLocation).build();
    Runtime runtime = TweakFlow.compile(loadPath, path);
    return runtime.getModules().get(runtime.unitKey(path));
  }

  protected String path = "condition.tf";

  public Runtime.Module eval(String script) {
    Runtime.Module module = compileModule(script);
    module.evaluate();
    // mref.set(module);
    return module;
  }
}
