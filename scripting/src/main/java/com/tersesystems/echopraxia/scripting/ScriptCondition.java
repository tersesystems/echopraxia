package com.tersesystems.echopraxia.scripting;

import com.tersesystems.echopraxia.api.Condition;
import com.tersesystems.echopraxia.api.Level;
import com.tersesystems.echopraxia.api.LoggingContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * The ScriptCondition class.
 *
 * <p>This is a condition backed by a tweakflow script manager.
 */
public class ScriptCondition implements Condition {

  private final ScriptManager scriptManager;
  private final boolean defaultValue;

  /**
   * Creates a condition from a script on the filesystem.
   *
   * @param defaultValue the value to return if there's an exception in the flow.
   * @param path the script path
   * @param reporter the reporter of any exceptions in the condition
   * @return the condition backed by script.
   */
  public static Condition create(boolean defaultValue, Path path, Consumer<Throwable> reporter) {
    if (Files.isDirectory(path)) {
      throw new IllegalArgumentException("Path is a directory: " + path);
    }
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
          public void close() throws IOException {
            // do nothing
          }

          @Override
          public boolean isInvalid() {
            return false;
          }

          @Override
          public String script() {
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

  /**
   * Creates a new condition using a handle and a default value of false.
   *
   * @param handle the script handle, created externally.
   * @return the script condition
   */
  public static Condition create(ScriptHandle handle) {
    return create(false, handle);
  }

  /**
   * Creates a new condition using a default value and a handle.
   *
   * @param defaultValue the default value on exception or error.
   * @param handle the script handle, created externally.
   * @return the script condition
   */
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
