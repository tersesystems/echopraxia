package com.tersesystems.echopraxia.api;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The LoggerHandle class is used as a handle to a logger at a specific level. */
public interface LoggerHandle<FB, RET> {

  /**
   * Logs using a message.
   *
   * @param message the message.
   */
  void log(@Nullable String message);

  /**
   * Logs using a message template with a field builder function.
   *
   * @param message the message template.
   * @param f the field builder function.
   */
  void log(@Nullable String message, @NotNull Function<FB, RET> f);
}
