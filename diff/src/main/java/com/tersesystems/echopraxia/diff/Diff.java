package com.tersesystems.echopraxia.diff;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import com.tersesystems.echopraxia.model.Value;

public final class Diff {

  /**
   * Diffs two values against each other.
   *
   * @param om the object mapper
   * @param before object before (aka `base`)
   * @param after object after (aka `working`)
   * @return the field representing the diff between the two values, in RFC 6902.
   */
  public static Value<?> diff(ObjectMapper om, Value<?> before, Value<?> after) {
    JsonNode beforeNode = om.valueToTree(before);
    JsonNode afterNode = om.valueToTree(after);

    JsonNode patch = JsonDiff.asJson(beforeNode, afterNode);
    return om.convertValue(patch, Value.class);
  }
}
