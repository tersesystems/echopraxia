package com.tersesystems.echopraxia.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcollections.Empty;
import org.pcollections.PMap;

/**
 * An immutable map containing typed values.
 *
 * <p>The elements inside Attributes cannot be enumerated -- It's not possible to access a value in
 * Attributes without holding the corresponding key.
 */
public interface Attributes {

  @Nullable
  <A> A get(@NotNull AttributeKey<A> key);

  @NotNull
  <A> Optional<A> getOptional(@NotNull AttributeKey<A> key);

  boolean containsKey(@NotNull AttributeKey<?> key);

  @NotNull
  <A> Attributes plus(@NotNull AttributeKey<A> key, A value);

  @NotNull
  <A> Attributes plus(@NotNull Attribute<A> attr);

  @NotNull
  Attributes plusAll(@NotNull Attributes attrs);

  @NotNull
  Attributes plusAll(@NotNull Attribute<?> a1, @NotNull Attribute<?> a2);

  @NotNull
  Attributes plusAll(@NotNull Collection<Attribute<?>> attrs);

  @NotNull
  Attributes plusAll(@NotNull Map<AttributeKey<?>, ?> attrs);

  @NotNull
  Attributes minus(@NotNull AttributeKey<?> k1);

  @NotNull
  Attributes minusAll(@NotNull Collection<AttributeKey<?>> keys);

  @NotNull
  static Attributes empty() {
    return AttributesImpl.EMPTY;
  }

  @NotNull
  static <A> Attributes create(@NotNull Attribute<A> attribute) {
    return empty().plus(attribute.key(), attribute.value());
  }

  @NotNull
  static Attributes create(@NotNull Attribute<?> a1, Attribute<?> a2) {
    return empty().plusAll(a1, a2);
  }

  @NotNull
  static Attributes create(@NotNull Collection<Attribute<?>> attrs) {
    return empty().plusAll(attrs);
  }
}

final class AttributesImpl implements Attributes {
  static final Attributes EMPTY = new AttributesImpl(Empty.map());

  private final PMap<AttributeKey<?>, Object> _map;

  AttributesImpl(PMap<AttributeKey<?>, Object> map) {
    this._map = map;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <A> A get(@NotNull AttributeKey<A> key) {
    return (A) _map.get(key);
  }

  @SuppressWarnings("unchecked")
  @Override
  public @NotNull <A> Optional<A> getOptional(@NotNull AttributeKey<A> key) {
    return (Optional<A>) Optional.ofNullable(_map.get(key));
  }

  @Override
  public boolean containsKey(@NotNull AttributeKey<?> key) {
    return _map.containsKey(key);
  }

  @Override
  public <A> Attributes plus(@NotNull AttributeKey<A> key, A value) {
    return new AttributesImpl(_map.plus(key, value));
  }

  @Override
  public <A> Attributes plus(@NotNull Attribute<A> attr) {
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