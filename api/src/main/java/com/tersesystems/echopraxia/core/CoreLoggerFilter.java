package com.tersesystems.echopraxia.core;

import java.util.function.Function;

/**
 * A filter which can modify a core logger in some way, typically by adding fields or conditions.
 *
 * <p>This interface is used in "global" situations where you don't want to modify individual
 * loggers, but want something in the pipeline to `getLogger`.
 */
@FunctionalInterface
public interface CoreLoggerFilter extends Function<CoreLogger, CoreLogger> {}
