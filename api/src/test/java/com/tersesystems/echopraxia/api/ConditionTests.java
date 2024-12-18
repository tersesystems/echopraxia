package com.tersesystems.echopraxia.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.tersesystems.echopraxia.fake.FakeCoreLogger;
import com.tersesystems.echopraxia.fake.FakeLoggingContext;
import com.tersesystems.echopraxia.spi.CoreLogger;
import org.junit.jupiter.api.Test;

public class ConditionTests {

  final Condition always = Condition.always();

  final Condition never = Condition.never();

  final CoreLogger core = new FakeCoreLogger("");
  final LoggingContext emptyContext = FakeLoggingContext.empty(core);

  @Test
  public void testAndAlways() {
    Condition c = always.and(always);
    assertThat(c.test(Level.INFO, emptyContext)).isTrue();
  }

  @Test
  public void testAndNever() {
    Condition c = always.and(never);
    assertThat(c.test(Level.INFO, emptyContext)).isFalse();
  }

  @Test
  public void testOrAlways() {
    Condition c = always.or(always);
    assertThat(c.test(Level.INFO, emptyContext)).isTrue();
  }

  @Test
  public void testOrNever() {
    Condition c = always.or(never);
    assertThat(c.test(Level.INFO, emptyContext)).isTrue();
  }

  @Test
  public void testXorAlways() {
    Condition c = always.xor(always);
    assertThat(c.test(Level.INFO, emptyContext)).isFalse();
  }

  @Test
  public void testXorNever() {
    Condition c = always.xor(never);
    assertThat(c.test(Level.INFO, emptyContext)).isTrue();
  }

  @Test
  public void testThresholdGreater() {
    Condition c = Condition.threshold(Level.DEBUG);
    assertThat(c.test(Level.INFO, emptyContext)).isTrue();
  }

  @Test
  public void testThresholdSame() {
    Condition c = Condition.threshold(Level.DEBUG);
    assertThat(c.test(Level.DEBUG, emptyContext)).isTrue();
  }

  @Test
  public void testThresholdLess() {
    Condition c = Condition.threshold(Level.DEBUG);
    assertThat(c.test(Level.TRACE, emptyContext)).isFalse();
  }

  @Test
  public void testExactlyMatch() {
    Condition c = Condition.exactly(Level.DEBUG);
    assertThat(c.test(Level.DEBUG, emptyContext)).isTrue();
  }

  @Test
  public void testExactlyNoMatch() {
    Condition c = Condition.exactly(Level.DEBUG);
    assertThat(c.test(Level.INFO, emptyContext)).isFalse();
  }

  @Test
  public void testOperationalMatch() {
    Condition c = Condition.operational();
    assertThat(c.test(Level.INFO, emptyContext)).isTrue();
  }

  @Test
  public void testOperationalNoMatch() {
    Condition c = Condition.operational();
    assertThat(c.test(Level.DEBUG, emptyContext)).isFalse();
  }

  @Test
  public void testDiagnosticMatch() {
    Condition c = Condition.diagnostic();
    assertThat(c.test(Level.DEBUG, emptyContext)).isTrue();
  }

  @Test
  public void testDiagnosticNoMatch() {
    Condition c = Condition.diagnostic();
    assertThat(c.test(Level.ERROR, emptyContext)).isFalse();
  }

  @Test
  public void testAnyMatch() {
    Condition c = Condition.anyMatch(f -> f.name().equals("foo"));
    Field field = FieldBuilder.instance().bool("foo", true);
    assertThat(c.test(Level.ERROR, FakeLoggingContext.single(core, field))).isTrue();
  }

  @Test
  public void testNoneMatch() {
    Condition c = Condition.noneMatch(f -> f.name().equals("foo"));
    Field field = FieldBuilder.instance().bool("foo", true);
    assertThat(c.test(Level.ERROR, FakeLoggingContext.single(core, field))).isFalse();
  }

  @Test
  public void testValueMatch() {
    Condition c =
        Condition.valueMatch(
            "foo",
            v -> {
              return ((Boolean) v.raw());
            });
    Field field = FieldBuilder.instance().bool("foo", true);
    assertThat(c.test(Level.ERROR, FakeLoggingContext.single(core, field))).isTrue();
  }
}
