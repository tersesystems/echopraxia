package com.tersesystems.echopraxia.filewatch;

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

public class FileWatchEvent {
  private final Path path;
  private final EventType eventType;

  public enum EventType {

    /* A new file was created */
    CREATE(StandardWatchEventKinds.ENTRY_CREATE),

    /* An existing file was modified */
    MODIFY(StandardWatchEventKinds.ENTRY_MODIFY),

    /* A file was deleted */
    DELETE(StandardWatchEventKinds.ENTRY_DELETE),

    /* An overflow occurred; some events were lost */
    OVERFLOW(StandardWatchEventKinds.OVERFLOW);

    private WatchEvent.Kind<?> kind;

    EventType(WatchEvent.Kind<?> kind) {
      this.kind = kind;
    }

    public WatchEvent.Kind<?> getWatchEventKind() {
      return kind;
    }
  }

  public FileWatchEvent(Path path, EventType eventType) {
    this.path = path;
    this.eventType = eventType;
  }

  public Path path() {
    return path;
  }

  public EventType eventType() {
    return eventType;
  }
}
