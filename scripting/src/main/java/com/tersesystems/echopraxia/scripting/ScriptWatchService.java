package com.tersesystems.echopraxia.scripting;

import static java.util.Collections.singletonList;

import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.filewatch.FileWatchService;
import com.tersesystems.echopraxia.filewatch.FileWatchServiceFactory;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * A service that watches a directory of scripts, and invalidates a script handle if the file has
 * been touched.
 */
public class ScriptWatchService implements Closeable {
  private final Logger<?> logger = LoggerFactory.getLogger();

  private static final FileWatchService watchService = FileWatchServiceFactory.fileWatchService();

  private final FileWatchService.FileWatcher watcher;

  private final Map<Path, AtomicBoolean> touchedMap = new ConcurrentHashMap<>();

  ScriptWatchService(Path watchedDirectory) {
    ThreadFactory threadFactory =
        r -> {
          Thread t = new Thread(r);
          t.setDaemon(true);
          t.setName("filewatcher-" + System.currentTimeMillis());
          return t;
        };

    watcher =
        watchService.watch(
            threadFactory,
            singletonList(watchedDirectory),
            event -> {
              switch (event.eventType()) {
                case CREATE:
                  logger.debug("CREATE: {}", fb -> fb.onlyString("path", event.path().toString()));
                  // XXX is there a script handle with this path?  There shouldn't be, but
                  // could it be deleted and then created again?
                  break;
                case MODIFY:
                  logger.debug("MODIFY: {}", fb -> fb.onlyString("path", event.path().toString()));
                  final AtomicBoolean touched = touchedMap.get(event.path());
                  if (touched != null) {
                    touched.set(true);
                  }

                  break;
                case DELETE:
                  logger.debug("DELETE: {}", fb -> fb.onlyString("path", event.path().toString()));

                  // XXX if there's a file script handle with this path, disable it
                  // and complain.
                  break;
                case OVERFLOW:
                  logger.debug(
                      "OVERFLOW: {}", fb -> fb.onlyString("path", event.path().toString()));
                  // XXX complain.
                  break;
              }
            });
  }

  public ScriptHandle create(Path scriptPath, Consumer<Throwable> throwableConsumer) {
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
          throw new ScriptException(e);
        }
      }

      @Override
      public String path() {
        return scriptPath.toString();
      }

      @Override
      public void report(Throwable e) {
        throwableConsumer.accept(e);
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
}
