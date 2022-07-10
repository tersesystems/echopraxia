package  com.tersesystems.echopraxia.difflogger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import com.tersesystems.echopraxia.api.FieldBuilder;
import com.tersesystems.echopraxia.api.FieldBuilderResult;
import com.tersesystems.echopraxia.api.Value;
import com.tersesystems.echopraxia.logstash.jackson.EchopraxiaModule;

public interface DiffFieldBuilder extends FieldBuilder {

  default FieldBuilderResult diff(String fieldName, Value<?> before, Value<?> after) {
    JsonNode beforeNode = Constants.mapper.valueToTree(before);
    JsonNode afterNode = Constants.mapper.valueToTree(after);

    JsonNode patch = JsonDiff.asJson(beforeNode, afterNode);
    return keyValue(fieldName, Value.string(patch.toString()));
  }
}

class Constants {

  static ObjectMapper mapper = new ObjectMapper();

  static {
    EchopraxiaModule testModule = new EchopraxiaModule();
    mapper.registerModule(testModule);
  }

}