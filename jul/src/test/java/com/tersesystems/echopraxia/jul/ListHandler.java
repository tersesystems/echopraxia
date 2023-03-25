package com.tersesystems.echopraxia.jul;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ListHandler extends Handler {

  private static final List<LogRecord> records = new ArrayList<>();

  public ListHandler() {
    super();
  }

  public static List<LogRecord> list() {
    return records;
  }

  @Override
  public void publish(LogRecord record) {
    records.add(record);
  }

  @Override
  public void flush() {

  }

  @Override
  public void close() throws SecurityException {
    records.clear();
  }
}
