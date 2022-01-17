package com.tersesystems.echopraxia;

class DefaultFieldBuilder implements Field.Builder {

  private static final Field.Builder instance = new DefaultFieldBuilder();

  private DefaultFieldBuilder() {}

  static Field.Builder singleton() {
    return instance;
  }
}
