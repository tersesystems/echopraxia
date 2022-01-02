package com.tersesystems.echopraxia.core;

/**
 * The CoreLoggerProvider is a service provider interface used by LoggerFactory.
 *
 * <p>You probably won't use this directly, unless you're writing a provider implementation.
 */
public interface CoreLoggerProvider {

  CoreLogger getLogger(Class<?> clazz);

  CoreLogger getLogger(String name);
}
