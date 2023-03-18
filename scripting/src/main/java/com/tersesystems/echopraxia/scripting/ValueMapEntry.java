package com.tersesystems.echopraxia.scripting;

import java.util.Map;
import java.util.Objects;

public final class ValueMapEntry
    implements Map.Entry<String, com.twineworks.tweakflow.lang.values.Value> {
  final String key;
  final com.twineworks.tweakflow.lang.values.Value value;

  ValueMapEntry(String k, com.twineworks.tweakflow.lang.values.Value v) {
    key = Objects.requireNonNull(k);
    value = Objects.requireNonNull(v);
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public com.twineworks.tweakflow.lang.values.Value getValue() {
    return value;
  }

  @Override
  public com.twineworks.tweakflow.lang.values.Value setValue(
      com.twineworks.tweakflow.lang.values.Value value) {
    throw new UnsupportedOperationException("not supported");
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Map.Entry)) return false;
    Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
    return key.equals(e.getKey()) && value.equals(e.getValue());
  }

  @Override
  public int hashCode() {
    return key.hashCode() ^ value.hashCode();
  }

  @Override
  public String toString() {
    return key + "=" + value;
  }
}
