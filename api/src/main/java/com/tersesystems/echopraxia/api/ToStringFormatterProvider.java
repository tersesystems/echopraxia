package com.tersesystems.echopraxia.api;

/** An SPI interface for returning a custom formatter. */
public interface ToStringFormatterProvider {

  ToStringFormatter getToStringFormatter();
}
