package com.tersesystems.echopraxia;

import com.tersesystems.echopraxia.core.Caller;
import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerFactory;
import org.jetbrains.annotations.NotNull;

/**
 * The AsyncLoggerFactory class. This is used to create the appropriate `Logger`.
 *
 * <p>{@code private static final AsyncLogger<?> logger = AsyncLoggerFactory.getLogger(); }
 */
public class AsyncLoggerFactory {

  /**
   * Creates a logger using the given class name.
   *
   * @param clazz the logger class to use
   * @return the logger.
   */
  @NotNull
  public static AsyncLogger<Field.Builder> getLogger(Class<?> clazz) {
    final CoreLogger core = CoreLoggerFactory.getLogger(Logger.FQCN, clazz);
    return getLogger(core, Field.Builder.instance());
  }

  /**
   * Creates a logger using the given class name and explicit field builder.
   *
   * @param clazz the logger class to use
   * @param builder the field builder.
   * @return the logger.
   * @param <FB> the type of field builder.
   */
  @NotNull
  public static <FB extends Field.Builder> AsyncLogger<FB> getLogger(
      @NotNull Class<?> clazz, @NotNull FB builder) {
    CoreLogger coreLogger = AsyncLoggerFactory.getLogger(clazz).core();
    return getLogger(coreLogger, builder);
  }

  /**
   * Creates a logger using the given name.
   *
   * @param name the logger name to use
   * @return the logger.
   */
  @NotNull
  public static AsyncLogger<Field.Builder> getLogger(@NotNull String name) {
    final CoreLogger core = CoreLoggerFactory.getLogger(Logger.FQCN, name);
    return getLogger(core, Field.Builder.instance());
  }

  /**
   * Creates a logger using the given name and an explicit field builder.
   *
   * @param name the logger name to use
   * @param builder the field builder.
   * @param <FB> the type of field builder.
   * @return the logger.
   */
  @NotNull
  public static <FB extends Field.Builder> AsyncLogger<FB> getLogger(
      @NotNull String name, @NotNull FB builder) {
    CoreLogger coreLogger = AsyncLoggerFactory.getLogger(name).core();
    return getLogger(coreLogger, builder);
  }

  /**
   * Creates a logger using the caller's class name.
   *
   * @return the logger.
   */
  @NotNull
  public static AsyncLogger<Field.Builder> getLogger() {
    CoreLogger core = CoreLoggerFactory.getLogger(Logger.FQCN, Caller.resolveClassName());
    return getLogger(core, Field.Builder.instance());
  }

  /**
   * Creates a logger using the caller's class name and an explicit field builder.
   *
   * @param fieldBuilder the field builder.
   * @return the logger.
   * @param <FB> the type of field builder.
   */
  @NotNull
  public static <FB extends Field.Builder> AsyncLogger<FB> getLogger(@NotNull FB fieldBuilder) {
    CoreLogger core = CoreLoggerFactory.getLogger(Logger.FQCN, Caller.resolveClassName());
    return getLogger(core, fieldBuilder);
  }

  /**
   * Creates a logger from a core logger and a field builder.
   *
   * @param core logger
   * @param fieldBuilder the field builder.
   * @return the logger.
   * @param <FB> the type of field builder.
   */
  @NotNull
  public static <FB extends Field.Builder> AsyncLogger<FB> getLogger(
      @NotNull CoreLogger core, @NotNull FB fieldBuilder) {
    return new AsyncLogger<>(core, fieldBuilder);
  }
}
