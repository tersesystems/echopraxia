package com.tersesystems.echopraxia.logback;

import com.tersesystems.echopraxia.api.Condition;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;

public class ConditionMarker extends BaseMarker {
  private final Condition condition;

  public ConditionMarker(@NotNull Condition condition) {
    super(condition.toString());
    this.condition = condition;
  }

  @NotNull
  public static Marker apply(@NotNull Condition condition) {
    return new ConditionMarker(condition);
  }

  public Condition getCondition() {
    return condition;
  }
}
