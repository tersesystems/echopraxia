package echopraxia.filewatch;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

/** The FileWatchService */
public interface FileWatchService {

  /**
   * Watches the given directories, sending events to the event consumer.
   *
   * @param factory the thread factory to use. Use setDaemon(true) in most cases.
   * @param watchList the directories to watch.
   * @param eventConsumer the event consumer
   * @return the file watcher
   * @throws IOException if there's a problem setting up the watcher
   */
  FileWatcher watch(
      ThreadFactory factory, List<Path> watchList, Consumer<FileWatchEvent> eventConsumer)
      throws IOException;

  /** A watcher, that watches files. */
  interface FileWatcher {
    /** Stop watching the files. */
    void stop();
  }
}
