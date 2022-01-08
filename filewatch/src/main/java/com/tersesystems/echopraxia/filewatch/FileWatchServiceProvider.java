package com.tersesystems.echopraxia.filewatch;

public interface FileWatchServiceProvider {

  FileWatchService fileWatchService(boolean disableFileHashCheck);
}
