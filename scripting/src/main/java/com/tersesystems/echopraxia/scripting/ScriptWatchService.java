package com.tersesystems.echopraxia.scripting;

import static java.util.Collections.singletonList;

import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.filewatch.FileWatchEvent;
import com.tersesystems.echopraxia.filewatch.FileWatchService;
import com.tersesystems.echopraxia.filewatch.FileWatchServiceFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * A service that watches a directory containing scripts, and
 * invalidates a script handle if the file has been touched.
 */
public class ScriptWatchService implements AutoCloseable {
  private static final Logger<?> logger = LoggerFactory.getLogger();

  private static final FileWatchService watchService = FileWatchServiceFactory.fileWatchService();

  private static final ScriptThreadFactory threadFactory = new ScriptThreadFactory();

  private final FileWatchService.FileWatcher watcher;

  private final Map<Path, AtomicBoolean> touchedMap;

  private final Path watchedDirectory;

  public ScriptWatchService(Path watchedDirectory) {
    if (watchedDirectory == null) {
      String msg = "Null watchedDirectory!";
      throw new ScriptException(msg);
    }

    if (!Files.exists(watchedDirectory)) {
      String msg = String.format("Path %s does not exist!", watchedDirectory);
      throw new ScriptException(msg);
    }

    if (!Files.isDirectory(watchedDirectory)) {
      String msg = String.format("Path %s is not a directory!", watchedDirectory);
      throw new ScriptException(msg);
    }

    if (!Files.isReadable(watchedDirectory)) {
      String msg = String.format("Path %s is not readable!", watchedDirectory);
      throw new ScriptException(msg);
    }

    this.touchedMap = new ConcurrentHashMap<>();
    this.watchedDirectory = watchedDirectory;
    final Consumer<FileWatchEvent> fileWatchEventConsumer =
        event -> {
          switch (event.eventType()) {
            case CREATE:
              // Creation is an automatic touch, and a full eval is needed after a file is deleted.
              logger.debug("CREATE: {}", fb -> fb.onlyString("path", event.path().toString()));
              final AtomicBoolean t = touchedMap.get(event.path());
              if (t != null) {
                t.set(true);
              }
              break;
            case MODIFY:
              // This is the normal use case.
              logger.debug("MODIFY: {}", fb -> fb.onlyString("path", event.path().toString()));
              final AtomicBoolean touched = touchedMap.get(event.path());
              if (touched != null) {
                touched.set(true);
              }

              break;
            case DELETE:
              logger.debug("DELETE: {}", fb -> fb.onlyString("path", event.path().toString()));
              final AtomicBoolean t3 = touchedMap.get(event.path());
              if (t3 != null) {
                logger.warn(
                    "Watched script {} was deleted!  Disabling touched flag until found again.",
                    fb -> fb.onlyString("path", event.path().toString()));
                t3.set(false);
              }
              break;
            case OVERFLOW:
              // Honestly not much we can do about this?
              logger.debug("OVERFLOW: {}", fb -> fb.onlyString("path", event.path().toString()));
              break;
          }
        };

    try {
      List<Path> watchList = singletonList(watchedDirectory);
      watcher = watchService.watch(threadFactory, watchList, fileWatchEventConsumer);
    } catch (IOException e) {
      throw new ScriptException("Error setting up watch service for " + watchedDirectory, e);
    }
  }

  /**
   * Watches a script, returning a handle that will be "invalid" if the watched directory sees that
   * the file has been touched recently.
   *
   * @param scriptPath the path to the script, must be within the script directory.
   * @param reporter the reporter for an exception.
   * @return the handle to the script.
   */
  public ScriptHandle watchScript(Path scriptPath, Consumer<Throwable> reporter) {
    if (scriptPath == null) {
      String msg = "Null scriptPath";
      throw new ScriptException(msg);
    }

    if (reporter == null) {
      String msg = "Null reporter";
      throw new ScriptException(msg);
    }

    if (!Files.isRegularFile(scriptPath)) {
      String msg = String.format("Path %s is not a file!", scriptPath);
      throw new ScriptException(msg);
    }

    if (!Files.isReadable(scriptPath)) {
      String msg = String.format("Path %s is not readable!", scriptPath);
      throw new ScriptException(msg);
    }

    if (!scriptPath.startsWith(watchedDirectory)) {
      String msg =
          String.format("Path %s is not in watched directory %s", scriptPath, watchedDirectory);
      throw new ScriptException(msg);
    }

    if (!Files.exists(scriptPath)) {
      String msg = String.format("Path %s does not exist!", scriptPath);
      throw new ScriptException(msg);
    }

    AtomicBoolean touched = new AtomicBoolean(false);
    registerPath(scriptPath, touched);

    return new ScriptHandle() {
      @Override
      public void close() throws IOException {
        removeHandle(this);
      }

      @Override
      public boolean isInvalid() {
        return touched.getAndSet(false);
      }

      @Override
      public String script() {
        // Should this cache internally?  Should only be read on eval...
        try {
          final byte[] bytes = Files.readAllBytes(scriptPath);
          return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
          throw new ScriptException("Cannot read from " + scriptPath, e);
        }
      }

      @Override
      public String path() {
        return scriptPath.toString();
      }

      @Override
      public void report(Throwable e) {
        reporter.accept(e);
      }
    };
  }

  @Override
  public void close() {
    watcher.stop();
  }

  private void removeHandle(ScriptHandle scriptHandle) {
    touchedMap.remove(Paths.get(scriptHandle.path()));
  }

  private void registerPath(Path scriptPath, AtomicBoolean touched) {
    touchedMap.put(scriptPath, touched);
  }

  private static class ScriptThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    @Override
    public Thread newThread(Runnable r) {
      Thread t = new Thread(r);
      t.setDaemon(true);
      t.setName("scriptwatchservice-thread-" + threadNumber.getAndIncrement());
      return t;
    }
  }
}
