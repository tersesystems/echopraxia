package com.tersesystems.echopraxia;

public abstract class AbstractSelf<SELF extends AbstractSelf<SELF>> {
  protected final SELF myself;

  // we prefer not to use Class<? extends S> selfType because it would force inherited
  // constructor to cast with a compiler warning
  // let's keep compiler warning internal (when we can) and not expose them to our end users.
  @SuppressWarnings("unchecked")
  protected AbstractSelf(Class<?> selfType) {
    myself = (SELF) selfType.cast(this);
  }

  protected final SELF self() {
    return myself;
  }
}
