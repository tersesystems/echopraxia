package com.tersesystems.echopraxia.logstash;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EncodingListAppender<E> extends UnsynchronizedAppenderBase<E> {

  public List<String> list = new ArrayList<>();

  protected Encoder<E> encoder;

  public Encoder<E> getEncoder() {
    return encoder;
  }

  public void setEncoder(Encoder<E> encoder) {
    this.encoder = encoder;
  }

  protected void append(E e) {
    final byte[] encode = encoder.encode(e);
    final String s = new String(encode, StandardCharsets.UTF_8);
    System.out.println("appender hash code = " + Integer.toHexString(hashCode()));
    System.out.println("s = " + s);
    list.add(s);
  }
}
