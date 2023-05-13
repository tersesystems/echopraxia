package com.tersesystems.echopraxia.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.pcollections.Empty;
import org.pcollections.PMap;

/**
 * An immutable map containing typed values.
 *
 * <p>The elements inside Attributes cannot be enumerated -- It's not possible to access a value in
 * a TypedMap without holding the corresponding key.
 */
public interface Attributes {

  <A> A get(AttributeKey<A> key);

  <A> Optional<A> getOptional(AttributeKey<A> key);

  boolean containsKey(AttributeKey<?> key);

  <A> Attributes plus(AttributeKey<A> key, A value);

  <A> Attributes plus(Attribute<A> attr);

  Attributes plusAll(Attributes attrs);

  Attributes plusAll(Attribute<?> a1, Attribute<?> a2);

  Attributes plusAll(Collection<Attribute<?>> attrs);

  Attributes plusAll(Map<AttributeKey<?>, ?> attrs);

  Attributes minus(AttributeKey<?> k1);

  Attributes minusAll(Collection<AttributeKey<?>> keys);

  static Attributes empty() {
    return AttributesImpl.EMPTY;
  }

  static <A> Attributes create(Attribute<A> attribute) {
    return empty().plus(attribute.key(), attribute.value());
  }

  static Attributes create(Attribute<?> a1, Attribute<?> a2) {
    return empty().plusAll(a1, a2);
  }

  static Attributes create(Collection<Attribute<?>> attrs) {
    return empty().plusAll(attrs);
  }
}

final class AttributesImpl implements Attributes {
  static final Attributes EMPTY = new AttributesImpl(Empty.map());

  private final PMap<AttributeKey<?>, Object> _map;

  AttributesImpl(PMap<AttributeKey<?>, Object> map) {
    this._map = map;
  }

  @Override
  public <A> A get(AttributeKey<A> key) {
    return (A) _map.get(key);
  }

  @Override
  public <A> Optional<A> getOptional(AttributeKey<A> key) {
    return (Optional<A>) Optional.ofNullable(_map.get(key));
  }

  @Override
  public boolean containsKey(AttributeKey<?> key) {
    return _map.containsKey(key);
  }

  @Override
  public <A> Attributes plus(AttributeKey<A> key, A value) {
    return new AttributesImpl(_map.plus(key, value));
  }

  @Override
  public <A> Attributes plus(Attribute<A> attr) {
    return plus(attr.key(), attr.value());
  }

  @Override
  public Attributes plusAll(Attributes attrs) {
    return new AttributesImpl(_map.plusAll(((AttributesImpl) attrs)._map));
  }

  @Override
  public Attributes plusAll(Attribute<?> a1, Attribute<?> a2) {
    return plus(a1).plus(a2);
  }

  @Override
  public Attributes plusAll(Map<AttributeKey<?>, ?> attrs) {
    return new AttributesImpl(_map.plusAll(attrs));
  }

  @Override
  public Attributes plusAll(Collection<Attribute<?>> attrs) {
    Map<AttributeKey<?>, Object> attrsMap = new HashMap<>();
    for (Attribute<?> attr : attrs) {
      attrsMap.put(attr.key(), attr.value());
    }
    return plusAll(attrsMap);
  }

  @Override
  public Attributes minus(AttributeKey<?> key) {
    return new AttributesImpl(_map.minus(key));
  }

  @Override
  public Attributes minusAll(Collection<AttributeKey<?>> keys) {
    return new AttributesImpl(_map.minusAll(keys));
  }

  @Override
  public String toString() {
    return _map.toString();
  }
}
