package com.tersesystems.echopraxia;

import com.tersesystems.echopraxia.core.CoreLogger;
import org.jetbrains.annotations.NotNull;

public class CustomLogger<FB extends Field.Builder> extends AbstractLogger<CustomLogger<FB>, FB> {

  protected CustomLogger(@NotNull CoreLogger core, @NotNull FB fieldBuilder) {
    super(CustomLogger.class, core, fieldBuilder);
  }

  /**
   * Creates a new logger with the given field builder.
   *
   * @param newBuilder the given field builder.
   * @param <T> the type of the field builder.
   * @return a new logger using the given field builder.
   */
  @NotNull
  public <T extends Field.Builder> CustomLogger<T> withFieldBuilder(@NotNull T newBuilder) {
    if (this.fieldBuilder == newBuilder) {
      //noinspection unchecked
      return (CustomLogger<T>) this;
    }
    return new CustomLogger<>(core(), newBuilder);
  }

  @Override
  protected CustomLogger<FB> newLogger(CoreLogger coreLogger) {
    return new CustomLogger<>(coreLogger, fieldBuilder);
  }

  @Override
  protected CustomLogger<FB> neverLogger(CoreLogger coreLogger) {
    return new CustomLogger<>(coreLogger, fieldBuilder);
  }
}
