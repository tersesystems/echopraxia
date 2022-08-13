package com.tersesystems.echopraxia.diff;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import com.tersesystems.echopraxia.api.FieldBuilder;
import com.tersesystems.echopraxia.api.FieldBuilderResult;
import com.tersesystems.echopraxia.api.Value;
import com.tersesystems.echopraxia.jackson.ObjectMapperProvider;

/**
 * This field builder uses the stable structured representation of objects to diff them against each
 * other, returning an RFC 6902 set of values for the difference.
 */
public interface DiffFieldBuilder extends FieldBuilder, ObjectMapperProvider {

  /**
   * Diffs two values against each other.
   *
   * @param fieldName the field name to give the diff
   * @param before object before (aka `base`)
   * @param after object after (aka `working`)
   * @return the field representing the diff between the two values, in RFC 6902.
   */
  default FieldBuilderResult diff(String fieldName, Value<?> before, Value<?> after) {
    ObjectMapper m = _objectMapper();
    JsonNode beforeNode = m.valueToTree(before);
    JsonNode afterNode = m.valueToTree(after);

    JsonNode patch = JsonDiff.asJson(beforeNode, afterNode);
    Value<?> value = m.convertValue(patch, Value.class);

    return keyValue(fieldName, value);
  }
}
