package echopraxia.scripting;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A script handle that uses a direct path to a file.
 *
 * <p>Errors are sent to the reporter.
 */
public class FileScriptHandle implements ScriptHandle {

  private final Path path;
  private final Consumer<Throwable> reporter;

  FileScriptHandle(Path path, Consumer<Throwable> reporter) {
    this.path = path;
    this.reporter = reporter;
  }

  @Override
  public boolean isInvalid() {
    return false;
  }

  @Override
  public String script() {
    if (!Files.exists(path)) {
      throw new ScriptException("No path found at " + path.toAbsolutePath());
    }

    try {
      return readString(path);
    } catch (IOException e) {
      throw new ScriptException(e);
    }
  }

  @Override
  public String path() {
    return path.toString();
  }

  protected String readString(Path path) throws IOException {
    try (BufferedReader reader = Files.newBufferedReader(path)) {
      return reader.lines().collect(Collectors.joining("\n"));
    }
  }

  public void report(Throwable e) {
    reporter.accept(e);
  }

  @Override
  public void close() throws IOException {
    // do nothing
  }
}
