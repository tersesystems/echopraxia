package  com.tersesystems.echopraxia.diff;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.JsonDiff;
import com.tersesystems.echopraxia.api.FieldBuilder;
import com.tersesystems.echopraxia.api.FieldBuilderResult;
import com.tersesystems.echopraxia.api.Value;

public interface DiffFieldBuilder extends FieldBuilder {

  default FieldBuilderResult diff(String fieldName, Value<?> before, Value<?> after) {
    JsonNode beforeNode = DiffObjectMapper.mapper.valueToTree(before);
    JsonNode afterNode = DiffObjectMapper.mapper.valueToTree(after);

    JsonNode patch = JsonDiff.asJson(beforeNode, afterNode);
    return keyValue(fieldName, Value.string(patch.toString()));
  }
}

