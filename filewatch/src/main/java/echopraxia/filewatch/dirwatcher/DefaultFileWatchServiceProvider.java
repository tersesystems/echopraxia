package echopraxia.filewatch.dirwatcher;

import echopraxia.filewatch.FileWatchService;
import echopraxia.filewatch.FileWatchServiceProvider;

/** The provider for default filewatch service. */
public class DefaultFileWatchServiceProvider implements FileWatchServiceProvider {

  @Override
  public FileWatchService fileWatchService(boolean disableHashCheck) {
    return new DefaultFileWatchService(disableHashCheck);
  }
}
