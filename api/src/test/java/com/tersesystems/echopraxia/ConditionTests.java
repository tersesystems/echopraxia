package com.tersesystems.echopraxia;

import static org.assertj.core.api.Assertions.assertThat;

import com.tersesystems.echopraxia.fake.FakeLoggingContext;
import java.time.*;
import org.junit.jupiter.api.Test;

public class ConditionTests {

  final Condition always = Condition.always();

  final Condition never = Condition.never();

  @Test
  public void testAndAlways() {
    Condition c = always.and(always);
    assertThat(c.test(Level.INFO, FakeLoggingContext.empty())).isTrue();
  }

  @Test
  public void testAndNever() {
    Condition c = always.and(never);
    assertThat(c.test(Level.INFO, FakeLoggingContext.empty())).isFalse();
  }

  @Test
  public void testOrAlways() {
    Condition c = always.or(always);
    assertThat(c.test(Level.INFO, FakeLoggingContext.empty())).isTrue();
  }

  @Test
  public void testOrNever() {
    Condition c = always.or(never);
    assertThat(c.test(Level.INFO, FakeLoggingContext.empty())).isTrue();
  }

  @Test
  public void testXorAlways() {
    Condition c = always.xor(always);
    assertThat(c.test(Level.INFO, FakeLoggingContext.empty())).isFalse();
  }

  @Test
  public void testXorNever() {
    Condition c = always.xor(never);
    assertThat(c.test(Level.INFO, FakeLoggingContext.empty())).isTrue();
  }

  @Test
  public void testThresholdGreater() {
    Condition c = Condition.threshold(Level.DEBUG);
    assertThat(c.test(Level.INFO, FakeLoggingContext.empty())).isTrue();
  }

  @Test
  public void testThresholdSame() {
    Condition c = Condition.threshold(Level.DEBUG);
    assertThat(c.test(Level.DEBUG, FakeLoggingContext.empty())).isTrue();
  }

  @Test
  public void testThresholdLess() {
    Condition c = Condition.threshold(Level.DEBUG);
    assertThat(c.test(Level.TRACE, FakeLoggingContext.empty())).isFalse();
  }

  @Test
  public void testExactlyMatch() {
    Condition c = Condition.exactly(Level.DEBUG);
    assertThat(c.test(Level.DEBUG, FakeLoggingContext.empty())).isTrue();
  }

  @Test
  public void testExactlyNoMatch() {
    Condition c = Condition.exactly(Level.DEBUG);
    assertThat(c.test(Level.INFO, FakeLoggingContext.empty())).isFalse();
  }

  @Test
  public void testOperationalMatch() {
    Condition c = Condition.operational();
    assertThat(c.test(Level.INFO, FakeLoggingContext.empty())).isTrue();
  }

  @Test
  public void testOperationalNoMatch() {
    Condition c = Condition.operational();
    assertThat(c.test(Level.DEBUG, FakeLoggingContext.empty())).isFalse();
  }

  @Test
  public void testDiagnosticMatch() {
    Condition c = Condition.diagnostic();
    assertThat(c.test(Level.DEBUG, FakeLoggingContext.empty())).isTrue();
  }

  @Test
  public void testDiagnosticNoMatch() {
    Condition c = Condition.diagnostic();
    assertThat(c.test(Level.ERROR, FakeLoggingContext.empty())).isFalse();
  }

  static final Clock officeClock = Clock.system(ZoneId.of("America/Los_Angeles"));

  public Condition testWeekday() {
    return (level, context) -> {
      LocalTime now = LocalTime.now(officeClock);
      final int hour = now.getHour() + 1; // hour is zero based, so adjust for readability
      return (hour >= 8) && (hour <= 17); // 8 am to 5 pm
    };
  }
}
