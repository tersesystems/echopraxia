package com.tersesystems.echopraxia.filewatch;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Fail.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileWatchTest {

  volatile boolean changed = false;
  volatile long startTime = 0;
  volatile long endTime = 0;

  FileWatchService watchService = FileWatchServiceFactory.fileWatchService();

  // https://junit.org/junit5/docs/current/user-guide/#writing-tests-built-in-extensions-TempDirectory
  @Test
  public void testDetectNewFiles(@TempDir Path tempDir) throws IOException {
    watchFiles(
        watchService,
        singletonList(tempDir),
        () -> {
          try {
            Files.write(tempDir.resolve("test"), singletonList("new file"));
            assertChanged();
          } catch (IOException e) {
            fail(e.toString());
          }
        });
  }

  @Test
  public void testDetectChangesOnFiles(@TempDir Path tempDir) throws IOException {
    Files.write(tempDir.resolve("test"), singletonList("new file"));
    watchFiles(
        watchService,
        singletonList(tempDir),
        () -> {
          try {
            Files.write(tempDir.resolve("test"), singletonList("changed"));
            assertChanged();
          } catch (IOException e) {
            fail(e.toString());
          }
        });
  }

  @Test
  public void testDetectChangesOnSubdir(@TempDir Path tempDir) throws IOException {
    final Path subdir = Files.createDirectory(tempDir.resolve("subdir"));
    watchFiles(
        watchService,
        singletonList(tempDir),
        () -> {
          try {
            Files.write(subdir.resolve("test"), singletonList("new"));
            assertChanged();
          } catch (IOException e) {
            fail(e.toString());
          }
        });
  }

  private void watchFiles(FileWatchService watchService, List<Path> files, Runnable runnable)
      throws IOException {
    FileWatchService.FileWatcher watcher =
        watchService.watch(
            new ThreadFactory() {
              @Override
              public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setName("filewatcher-" + System.currentTimeMillis());
                return t;
              }
            },
            files,
            this::reportChange);
    reset();
    try {
      runnable.run();
    } finally {
      watcher.stop();
    }
  }

  private void reportChange(FileWatchEvent event) {
    System.out.println("event = " + event);
    endTime = System.nanoTime();
    changed = true;
  }

  private void reset() {
    try {
      Thread.sleep(200);
    } catch (InterruptedException e) {
      // e.printStackTrace();
    }
    changed = false;
    startTime = System.nanoTime();
  }

  private void assertChanged() {
    Instant deadline = Instant.now().plus(5, ChronoUnit.SECONDS);
    while (!changed) {
      if (deadline.isBefore(Instant.now())) {
        fail("Changed did not become true within 5 seconds");
      }
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        // e.printStackTrace();
      }
    }
    final float l = (endTime - startTime) / 1000000;
    System.out.format("%s: %7.1f ms\n", getClass().getSimpleName(), l);
  }
}
