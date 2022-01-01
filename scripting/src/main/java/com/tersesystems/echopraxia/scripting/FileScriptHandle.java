package com.tersesystems.echopraxia.scripting;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A script handle that uses a direct path and verifies it by checking the last modified time.
 *
 * Note that this does mean that there's a filesystem access on every script evaluation, but
 * since it's just checking the file metadata to ask if it's changed, my belief is that most
 * filesystems can return this information pretty fast.  (Don't blame me if this tanks your
 * application.)
 *
 * Errors are sent to the reporter.
 *
 */
public class FileScriptHandle implements ScriptHandle {

  private final Path path;
  private final Consumer<Throwable> reporter;
  private final AtomicReference<FileTime> lastModified;

  public FileScriptHandle(
      Path path, Consumer<Throwable> reporter) {
    this.path = path;
    this.reporter = reporter;
    try {
        lastModified = new AtomicReference<>(Files.getLastModifiedTime(path.toAbsolutePath()));
    } catch (IOException e) {
        throw new ScriptException(e);
    }
  }

  @Override
  public boolean isInvalid() {
    if (Files.exists(path)) {
      try {
        FileTime newTime = Files.getLastModifiedTime(path);
        return newTime.compareTo(lastModified.get()) > 0;
      } catch (IOException e) {
        report(e);
      }
    }
    return true;
  }

  @Override
  public String script() throws IOException {
    if (!Files.exists(path)) {
      throw new FileNotFoundException(path.toAbsolutePath().toString());
    }

    return readString(path);
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
}
