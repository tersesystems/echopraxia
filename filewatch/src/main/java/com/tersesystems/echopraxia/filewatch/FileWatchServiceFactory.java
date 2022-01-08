package com.tersesystems.echopraxia.filewatch;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public class FileWatchServiceFactory {

  private static class LazyHolder {
    private static FileWatchServiceProvider init() {
      ServiceLoader<FileWatchServiceProvider> loader =
          ServiceLoader.load(FileWatchServiceProvider.class);
      Iterator<FileWatchServiceProvider> iterator = loader.iterator();
      // XXX Run through the iterations, if there's no 3rd party service then load up this one?
      if (iterator.hasNext()) {
        return iterator.next();
      } else {
        String msg = "No FileWatchServiceProvider implementation found in classpath!";
        throw new ServiceConfigurationError(msg);
      }
    }

    static final FileWatchServiceProvider INSTANCE = init();
  }

  public static FileWatchService fileWatchService() {
    return fileWatchService(false);
  }

  public static FileWatchService fileWatchService(boolean disableFileHashCheck) {
    return LazyHolder.INSTANCE.fileWatchService(disableFileHashCheck);
  }
}
