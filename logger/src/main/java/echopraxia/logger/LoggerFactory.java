package echopraxia.logger;

import echopraxia.api.FieldBuilder;
import echopraxia.logging.spi.Caller;
import echopraxia.logging.spi.CoreLogger;
import echopraxia.logging.spi.CoreLoggerFactory;
import org.jetbrains.annotations.NotNull;

/**
 * The LoggerFactory class. This is used to create the appropriate `Logger`.
 *
 * <p>{@code private static final Logger<FieldBuilder> logger = LoggerFactory.getLogger(); }
 */
public class LoggerFactory {

  /**
   * Creates a logger using the given class name.
   *
   * @param clazz the logger class to use
   * @return the logger.
   */
  @NotNull
  public static Logger<FieldBuilder> getLogger(Class<?> clazz) {
    final CoreLogger core = CoreLoggerFactory.getLogger(echopraxia.logger.Logger.FQCN, clazz);
    return getLogger(core, FieldBuilder.instance());
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
  public static <FB> Logger<FB> getLogger(@NotNull Class<?> clazz, @NotNull FB builder) {
    CoreLogger coreLogger = LoggerFactory.getLogger(clazz).core();
    return getLogger(coreLogger, builder);
  }

  /**
   * Creates a logger using the given name.
   *
   * @param name the logger name to use
   * @return the logger.
   */
  @NotNull
  public static Logger<FieldBuilder> getLogger(@NotNull String name) {
    final CoreLogger core = CoreLoggerFactory.getLogger(Logger.FQCN, name);
    return getLogger(core, FieldBuilder.instance());
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
  public static <FB> Logger<FB> getLogger(@NotNull String name, @NotNull FB builder) {
    CoreLogger coreLogger = LoggerFactory.getLogger(name).core();
    return getLogger(coreLogger, builder);
  }

  /**
   * Creates a logger using the caller's class name.
   *
   * @return the logger.
   */
  @NotNull
  public static Logger<FieldBuilder> getLogger() {
    CoreLogger core = CoreLoggerFactory.getLogger(Logger.FQCN, Caller.resolveClassName());
    return getLogger(core, FieldBuilder.instance());
  }

  /**
   * Creates a logger from a core logger.
   *
   * @return the logger.
   */
  public static Logger<FieldBuilder> getLogger(CoreLogger core) {
    return getLogger(core, FieldBuilder.instance());
  }

  /**
   * Creates a logger using the caller's class name and an explicit field builder.
   *
   * @param fieldBuilder the field builder.
   * @return the logger.
   * @param <FB> the type of field builder.
   */
  @NotNull
  public static <FB extends FieldBuilder> Logger<FB> getLogger(@NotNull FB fieldBuilder) {
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
  public static <FB> Logger<FB> getLogger(@NotNull CoreLogger core, @NotNull FB fieldBuilder) {
    return new Logger<>(core, fieldBuilder);
  }
}
