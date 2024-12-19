package echopraxia.logback;

import echopraxia.logging.api.Condition;
import org.jetbrains.annotations.NotNull;

public class ConditionMarker extends BaseMarker {
  private final Condition condition;

  ConditionMarker(@NotNull Condition condition) {
    super(condition.toString());
    this.condition = condition;
  }

  @NotNull
  public static ConditionMarker apply(@NotNull Condition condition) {
    return new ConditionMarker(condition);
  }

  public Condition getCondition() {
    return condition;
  }
}
