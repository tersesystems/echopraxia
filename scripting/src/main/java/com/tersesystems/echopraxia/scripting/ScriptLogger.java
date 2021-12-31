package com.tersesystems.echopraxia.scripting;

import com.tersesystems.echopraxia.CoreLogger;
import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.Logger;

public class ScriptLogger<FB extends Field.Builder> extends Logger<FB> {

  public ScriptLogger(CoreLogger core, FB fieldBuilder, ScriptManager scriptManager) {
    super(core, fieldBuilder);
  }
}
