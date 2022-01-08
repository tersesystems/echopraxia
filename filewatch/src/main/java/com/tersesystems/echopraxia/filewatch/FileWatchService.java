package com.tersesystems.echopraxia.filewatch;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

public interface FileWatchService {

  FileWatcher watch(
      ThreadFactory factory, List<Path> filesToWatch, Consumer<FileWatchEvent> onChange);

  /** A watcher, that watches files. */
  interface FileWatcher {
    /** Stop watching the files. */
    void stop();
  }
}
