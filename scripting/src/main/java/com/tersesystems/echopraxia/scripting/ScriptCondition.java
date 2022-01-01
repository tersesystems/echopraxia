package com.tersesystems.echopraxia.scripting;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Level;
import com.tersesystems.echopraxia.LoggingContext;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public class ScriptCondition implements Condition {

  private final ScriptManager scriptManager;
  private final boolean defaultValue;

  /**
   * Creates a condition from a script on the filesystem, checking the last modified date to reload
   * the script automatically if need be.
   *
   * @param defaultValue the value to return if there's an exception in the flow.
   * @param path the script path
   * @param reporter the reporter of any exceptions in the condition
   * @return the condition backed by script.
   */
  public static Condition create(boolean defaultValue, Path path, Consumer<Throwable> reporter) {
    ScriptHandle handle = new FileScriptHandle(path, reporter);
    return create(defaultValue, handle);
  }

  /**
   * Creates a condition from a passed in Tweakflow script.
   *
   * @param defaultValue the value to return if there's an exception in the flow.
   * @param script the script
   * @param reporter the reporter of any exceptions in the condition
   * @return the condition backed by script.
   */
  public static Condition create(
      boolean defaultValue, String script, Consumer<Throwable> reporter) {
    ScriptHandle handle =
        new ScriptHandle() {
          @Override
          public boolean isInvalid() {
            return false;
          }

          @Override
          public String script() throws IOException {
            return script;
          }

          @Override
          public String path() {
            return "<memory>";
          }

          @Override
          public void report(Throwable e) {
            reporter.accept(e);
          }
        };
    return create(defaultValue, handle);
  }

  public static Condition create(boolean defaultValue, ScriptHandle handle) {
    ScriptManager scriptManager = new ScriptManager(handle);
    return new ScriptCondition(scriptManager, defaultValue);
  }

  ScriptCondition(ScriptManager scriptManager, boolean defaultValue) {
    this.scriptManager = scriptManager;
    this.defaultValue = defaultValue;
  }

  @Override
  public boolean test(Level level, LoggingContext context) {
    return scriptManager.execute(defaultValue, level, context);
  }
}
