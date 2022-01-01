package com.tersesystems.echopraxia.scripting;

import java.io.IOException;
import java.nio.file.*;

/** A script handle returns the script source, and can say if the script is invalid. */
public interface ScriptHandle {

  /** @return true if the script is invalid and should be re-evaluated, false otherwise. */
  boolean isInvalid();

  /** @return the code of the script. */
  String script() throws IOException;

  String path();

  default String libraryName() {
    return "echopraxia";
  }

  default String functionName() {
    return "evaluate";
  }

  /** If evaluating or parsing the script throws an exception, this method is called. */
  void report(Throwable e);
}
