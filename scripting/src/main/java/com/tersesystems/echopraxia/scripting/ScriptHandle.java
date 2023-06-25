package com.tersesystems.echopraxia.scripting;

/** A script handle returns the script source, and can say if the script is invalid. */
public interface ScriptHandle extends AutoCloseable {

  /**
   * @return true if the script is invalid and should be re-evaluated, false otherwise.
   */
  boolean isInvalid();

  /**
   * @return the code of the script.
   */
  String script();

  /**
   * The path to use when debugging / evaulating the script.
   *
   * @return the path
   */
  String path();

  /**
   * The library name for the script handle, "echopraxia" by default
   *
   * @return the library name
   */
  default String libraryName() {
    return "echopraxia";
  }

  /**
   * The function name for the script handle, "evaluate" by default
   *
   * @return the function name.
   */
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
