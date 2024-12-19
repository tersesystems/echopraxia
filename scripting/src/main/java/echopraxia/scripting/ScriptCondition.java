package echopraxia.scripting;

import echopraxia.logging.api.Condition;
import echopraxia.logging.api.Level;
import echopraxia.logging.api.LoggingContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The ScriptCondition class.
 *
 * <p>This is a condition backed by a tweakflow script manager.
 */
public class ScriptCondition implements Condition {

  private static final Function<LoggingContext, List<ValueMapEntry>> NO_USER_FUNCTIONS =
      ctx -> Collections.emptyList();

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
    return create(NO_USER_FUNCTIONS, defaultValue, handle);
  }

  public static Condition create(
      boolean defaultValue, String script, Consumer<Throwable> reporter) {
    return create(NO_USER_FUNCTIONS, defaultValue, script, reporter);
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
      Function<LoggingContext, List<ValueMapEntry>> userFunctions,
      boolean defaultValue,
      String script,
      Consumer<Throwable> reporter) {
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
    return create(userFunctions, defaultValue, handle);
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

  /**
   * Creates a new condition with a default value, a handle, and a set of user defined functions
   * available to the script.
   *
   * @param defaultValue the default value on excepiton on error
   * @param handle the script handle
   * @param userFunctions the user functions
   * @return the script condition
   */
  public static Condition create(
      Function<LoggingContext, List<ValueMapEntry>> userFunctions,
      boolean defaultValue,
      ScriptHandle handle) {
    ScriptManager scriptManager = new ScriptManager(handle);
    scriptManager.setUserFunctions(userFunctions);
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
