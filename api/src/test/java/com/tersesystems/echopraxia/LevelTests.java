package com.tersesystems.echopraxia;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LevelTests {

  @Test
  public void testGreaterThan() {
    assertThat(Level.INFO.isGreater(Level.DEBUG)).isTrue();
  }

  @Test
  public void testIsNotGreaterThan() {
    assertThat(Level.INFO.isGreater(Level.ERROR)).isFalse();
  }
  
  @Test
  public void testIsGreaterThanOrEqual() {
    assertThat(Level.INFO.isGreaterOrEqual(Level.DEBUG)).isTrue();
  }

  @Test
  public void testIsGreaterThanOrEqualEquality() {
    assertThat(Level.ERROR.isGreaterOrEqual(Level.ERROR)).isTrue();
  }

  @Test
  public void testIsLess() {
    assertThat(Level.DEBUG.isLess(Level.INFO)).isTrue();
  }

  @Test
  public void testIsNotLess() {
    assertThat(Level.INFO.isLess(Level.DEBUG)).isFalse();
  }

  @Test
  public void testIsLessOrEqual() {
    assertThat(Level.TRACE.isLessOrEqual(Level.DEBUG)).isTrue();
  }

  @Test
  public void testIsLessOrEqualEquality() {
    assertThat(Level.TRACE.isLessOrEqual(Level.TRACE)).isTrue();
  }

  @Test
  public void testIsEqual() {
    assertThat(Level.TRACE.isEqual(Level.TRACE)).isTrue();
  }
}
