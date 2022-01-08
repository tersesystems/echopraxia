package com.tersesystems.echopraxia.filewatch.dirwatcher;

import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import com.tersesystems.echopraxia.filewatch.FileWatchEvent;
import com.tersesystems.echopraxia.filewatch.FileWatchService;
import io.methvin.watcher.DirectoryChangeEvent;
import io.methvin.watcher.DirectoryChangeListener;
import io.methvin.watcher.DirectoryWatcher;
import io.methvin.watchservice.MacOSXListeningWatchService;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Internal class used for file watch that uses directory watcher. */
public class DefaultFileWatchService implements FileWatchService {

  private static final Logger<?> logger = LoggerFactory.getLogger();

  private final boolean disableFileHashCheck;

  public DefaultFileWatchService(boolean disableFileHashCheck) {
    this.disableFileHashCheck = disableFileHashCheck;
  }

  enum OS {
    Windows,
    Linux,
    Mac,
    Other;

    static OS os() {
      String osName = System.getProperty("os.name", OS.Other.name()).toLowerCase(Locale.ENGLISH);
      if (osName.contains("darwin") || osName.contains("mac")) {
        return OS.Mac;
      }
      if (osName.contains("windows")) {
        return OS.Windows;
      }
      if (osName.contains("linux")) {
        return OS.Linux;
      }
      return OS.Other;
    }
  }

  /** Creates a new thread for every watch. */
  @Override
  public FileWatcher watch(
      ThreadFactory factory, List<Path> filesToWatch, Consumer<FileWatchEvent> onChange) {
    try {
      Stream<Path> dirsToWatch =
          filesToWatch.stream()
              .filter(
                  path -> {
                    if (Files.isDirectory(path)) {
                      return true;
                    } else if (Files.isRegularFile(path)) {
                      logger.warn(
                          "An attempt has been made to watch the file: {}",
                          fb -> fb.onlyString("path", path.toString()));
                      return false;
                    } else return false;
                  });

      boolean isMac = OS.os() == OS.Mac;
      WatchService watchService =
          (isMac) ? new MacOSXListeningWatchService() : FileSystems.getDefault().newWatchService();
      DirectoryChangeListener adapter =
          event -> {
            final Path path = event.path();
            FileWatchEvent.EventType eventType = convertEventType(event.eventType());
            FileWatchEvent fileWatchEvent = new FileWatchEvent(path, eventType);
            onChange.accept(fileWatchEvent);
          };
      DirectoryWatcher directoryWatcher =
          DirectoryWatcher.builder()
              .paths(dirsToWatch.collect(Collectors.toList()))
              .listener(adapter)
              .fileHashing(!disableFileHashCheck)
              .watchService(watchService)
              .build();

      final ExecutorService executor = Executors.newSingleThreadExecutor(factory);
      directoryWatcher.watchAsync(executor);

      return new FileWatcher() {
        @Override
        public void stop() {
          try {
            directoryWatcher.close();
          } catch (IOException e) {
            // XXX should complain loudly
            // e.printStackTrace();
          }
        }
      };
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private FileWatchEvent.EventType convertEventType(DirectoryChangeEvent.EventType eventType) {
    switch (eventType) {
      case CREATE:
        return FileWatchEvent.EventType.CREATE;
      case MODIFY:
        return FileWatchEvent.EventType.MODIFY;
      case DELETE:
        return FileWatchEvent.EventType.DELETE;
      case OVERFLOW:
        return FileWatchEvent.EventType.OVERFLOW;
    }
    throw new IllegalStateException("No valid event type found for " + eventType);
  }
}
