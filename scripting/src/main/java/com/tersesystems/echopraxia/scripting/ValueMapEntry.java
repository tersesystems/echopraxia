package com.tersesystems.echopraxia.scripting;

import com.twineworks.tweakflow.lang.values.UserFunctionValue;
import com.twineworks.tweakflow.lang.values.Values;
import java.util.Map;
import java.util.Objects;

/** A value map entry that maps a string to a valeu. */
public final class ValueMapEntry
    implements Map.Entry<String, com.twineworks.tweakflow.lang.values.Value> {
  final String key;
  final com.twineworks.tweakflow.lang.values.Value value;

  private ValueMapEntry(String k, com.twineworks.tweakflow.lang.values.Value v) {
    key = Objects.requireNonNull(k);
    value = Objects.requireNonNull(v);
  }

  /**
   * @param name the name of the entry
   * @param userFunction the user function to apply
   * @return the value map entry
   */
  public static ValueMapEntry make(String name, UserFunctionValue userFunction) {
    return new ValueMapEntry(name, Values.make(userFunction));
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
