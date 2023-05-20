package com.tersesystems.echopraxia.jul;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class EncodedListHandler extends Handler {
  final Formatter jsonFormatter = new JULJSONFormatter(true);
  final Formatter textFormatter = new SimpleFormatter();

  private static final List<String> jsonList = new ArrayList<>();
  private static final List<String> linesList = new ArrayList<>();
  private static final List<LogRecord> records = new ArrayList<>();


  public static List<LogRecord> records() {
    return records;
  }

  public static List<String> ndjson() {
    return jsonList;
  }

  public static List<String> lines() {
    return linesList;
  }

  @Override
  public void publish(LogRecord record) {
    jsonList.add(jsonFormatter.format(record)); // you can never use formatMessage here
    linesList.add(textFormatter.formatMessage(record));
    records.add(record);
  }

  @Override
  public void flush() {

  }

  @Override
  public void close() throws SecurityException {

  }


  public static void clear() throws SecurityException {
    records.clear();
    jsonList.clear();
    linesList.clear();
  }
}
