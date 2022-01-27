package com.tersesystems.echopraxia;

/**
 * Marker interface for key values.
 *
 * <p>Indicates that the plain value should be rendered in message template.
 *
 * <p>This marker interface is used internally, but you typically won't need to use it directly.
 * You can call `Field.Builder.value` to get a instance of a field with this.
 */
public interface ValueField extends Field {}
