package com.tersesystems.echopraxia.fluent;

import com.tersesystems.echopraxia.api.PresentationFieldBuilder;
import com.tersesystems.echopraxia.spi.Caller;
import com.tersesystems.echopraxia.spi.CoreLogger;
import com.tersesystems.echopraxia.spi.CoreLoggerFactory;
import org.jetbrains.annotations.NotNull;

/** The factory for FluentLogger. */
public class FluentLoggerFactory {
  public static final String FQCN = FluentLogger.class.getName();

  /**
   * Creates a logger using the given class name.
   *
   * @param clazz the logger class to use
   * @return the logger.
   */
  @NotNull
  public static FluentLogger<PresentationFieldBuilder> getLogger(Class<?> clazz) {
    return getLogger(clazz, PresentationFieldBuilder.instance());
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
  public static <FB> FluentLogger<FB> getLogger(Class<?> clazz, FB builder) {
    CoreLogger coreLogger = CoreLoggerFactory.getLogger(FQCN, clazz);
    return getLogger(coreLogger, builder);
  }

  /**
   * Creates a logger using the given name.
   *
   * @param name the logger name to use
   * @return the logger.
   */
  @NotNull
  public static FluentLogger<PresentationFieldBuilder> getLogger(String name) {
    return getLogger(name, PresentationFieldBuilder.instance());
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
  public static <FB> FluentLogger<FB> getLogger(String name, FB builder) {
    CoreLogger coreLogger = CoreLoggerFactory.getLogger(FQCN, name);
    return getLogger(coreLogger, builder);
  }

  /**
   * Creates a logger using the caller's class name.
   *
   * @return the logger.
   */
  @NotNull
  public static FluentLogger<PresentationFieldBuilder> getLogger() {
    return getLogger(Caller.resolveClassName());
  }

  /**
   * Creates a logger using the caller's class name and an explicit field builder.
   *
   * @param builder the field builder.
   * @return the logger.
   * @param <FB> the type of field builder.
   */
  @NotNull
  public static <FB> FluentLogger<FB> getLogger(FB builder) {
    return getLogger(Caller.resolveClassName(), builder);
  }

  /**
   * Creates a logger using a core logger and an explicit field builder.
   *
   * @param coreLogger the core logger.
   * @param builder the field builder.
   * @param <FB> the type of field builder.
   * @return the logger.
   */
  @NotNull
  public static <FB> FluentLogger<FB> getLogger(CoreLogger coreLogger, FB builder) {
    return new FluentLogger<>(coreLogger, builder);
  }
}
