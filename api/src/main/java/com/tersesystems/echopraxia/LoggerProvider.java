package com.tersesystems.echopraxia;

/**
 * The LoggerProvider is a service provider interface used by LoggerFactory.
 *
 * <p>You probably won't use this directly, unless you're writing a provider implementation.
 */
public interface LoggerProvider {

  Logger<Field.Builder> getLogger(Class<?> clazz);

  Logger<Field.Builder> getLogger(String name);
}
