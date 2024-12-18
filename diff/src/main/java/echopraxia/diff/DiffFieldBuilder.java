package echopraxia.diff;

import com.fasterxml.jackson.databind.ObjectMapper;
import echopraxia.api.Field;
import echopraxia.api.Value;
import echopraxia.jackson.ObjectMapperProvider;

/**
 * This field builder uses the stable structured representation of objects to diff them against each
 * other, returning an RFC 6902 set of values for the difference.
 */
public interface DiffFieldBuilder extends ObjectMapperProvider {

  /**
   * Diffs two values against each other.
   *
   * @param fieldName the field name to give the diff
   * @param before object before (aka `base`)
   * @param after object after (aka `working`)
   * @param <F> field type
   * @param fieldClass the field class
   * @return the field representing the diff between the two values, in RFC 6902.
   */
  default <F extends Field> F diff(
      String fieldName, Value<?> before, Value<?> after, Class<F> fieldClass) {
    ObjectMapper om = _objectMapper();
    Value<?> value = Diff.diff(om, before, after);
    return Field.keyValue(fieldName, value, fieldClass);
  }
}
