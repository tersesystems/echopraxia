package echopraxia.filewatch;

import java.util.ServiceLoader;

/**
 * The FileWatchServiceFactory creates `FileWatchService` instances from using a service provider
 * interface internally.
 *
 * <p>A default implementation is provided if no external provider is available.
 */
public class FileWatchServiceFactory {

  // lazy singleton holder for the SPI provider.
  // this might be overkill, but could be useful for unit testing / shims / etc
  // and it's self contained otherwise.
  private static class LazyHolder {
    private static FileWatchServiceProvider init() {
      ServiceLoader<FileWatchServiceProvider> loader =
          ServiceLoader.load(FileWatchServiceProvider.class);

      // Look to see if the end user provided another implementation we should use
      for (FileWatchServiceProvider provider : loader) {
        final String name = provider.getClass().getName();
        if (!name.endsWith(".DefaultFileWatchService")) {
          return provider;
        }
      }
      // Otherwise fall back to DefaultFileWatchService.
      return loader.iterator().next();
    }

    static final FileWatchServiceProvider INSTANCE = init();
  }

  /**
   * Returns a file watch service with the file hash check enabled.
   *
   * @return the file watch service singleton.
   */
  public static FileWatchService fileWatchService() {
    return fileWatchService(false);
  }

  /**
   * Returns a file watch service.
   *
   * @param disableFileHashCheck true if the file hash check should be disabled.
   * @return the file watch service singleton.
   */
  public static FileWatchService fileWatchService(boolean disableFileHashCheck) {
    return LazyHolder.INSTANCE.fileWatchService(disableFileHashCheck);
  }
}
