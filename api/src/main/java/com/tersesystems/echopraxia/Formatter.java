package com.tersesystems.echopraxia;

// Internal formatter... at some point might be worth exposing this through SPI.
// This may be important for key=value vs just plain value and logfmt line oriented data.
// For now, don't expose it anywhere.
interface Formatter {
  String fieldToString(Field field);
}
