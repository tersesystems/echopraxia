package com.tersesystems.echopraxia.filewatch;

/** The SPI for FileWatchService. */
public interface FileWatchServiceProvider {

  FileWatchService fileWatchService(boolean disableFileHashCheck);
}
