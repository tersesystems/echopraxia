package com.tersesystems.echopraxia.jul;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class EncodedListHandler extends Handler {

  final Formatter formatter = new JULJSONFormatter();

  private static final List<String> records = new ArrayList<>();

  public static List<String> list() {
    return records;
  }

  @Override
  public void publish(LogRecord record) {
    final String encoded = formatter.format(record);
    records.add(encoded);
  }

  @Override
  public void flush() {}

  @Override
  public void close() throws SecurityException {
    records.clear();
  }
}
