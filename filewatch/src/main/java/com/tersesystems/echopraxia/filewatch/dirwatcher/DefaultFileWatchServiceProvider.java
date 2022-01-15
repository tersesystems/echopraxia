package com.tersesystems.echopraxia.filewatch.dirwatcher;

import com.tersesystems.echopraxia.filewatch.FileWatchService;
import com.tersesystems.echopraxia.filewatch.FileWatchServiceProvider;

/** The provider for default filewatch service. */
public class DefaultFileWatchServiceProvider implements FileWatchServiceProvider {

  @Override
  public FileWatchService fileWatchService(boolean disableHashCheck) {
    return new DefaultFileWatchService(disableHashCheck);
  }
}
