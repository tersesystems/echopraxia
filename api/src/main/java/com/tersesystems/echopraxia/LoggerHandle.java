package com.tersesystems.echopraxia;

/** The LoggerHandle class is used as a handle to a logger at a specific level. */
public interface LoggerHandle<FB extends Field.Builder> {

  /**
   * Logs using a message.
   *
   * @param message the message.
   */
  void log(String message);

  /**
   * Logs using a message template with a field builder function.
   *
   * @param message the message template.
   * @param f the field builder function.
   */
  void log(String message, Field.BuilderFunction<FB> f);

  /**
   * Logs using a message and an exception.
   *
   * @param message the message.
   * @param e the exception.
   */
  void log(String message, Throwable e);
}
