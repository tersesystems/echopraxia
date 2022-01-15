package com.tersesystems.echopraxia.scripting;

/** A script handle returns the script source, and can say if the script is invalid. */
public interface ScriptHandle extends AutoCloseable {

  /** @return true if the script is invalid and should be re-evaluated, false otherwise. */
  boolean isInvalid();

  /** @return the code of the script. */
  String script();

  /**
   * The path to use when debugging / evaulating the script.
   *
   * @return the path
   */
  String path();

  default String libraryName() {
    return "echopraxia";
  }

  default String functionName() {
    return "evaluate";
  }

  /**
   * If evaluating or parsing the script throws an exception, this method is called.
   *
   * @param e throwable caused by failure in script
   */
  void report(Throwable e);
}
