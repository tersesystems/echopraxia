package com.tersesystems.echopraxia.jsonpath;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.tersesystems.echopraxia.LoggingContext;
import com.tersesystems.echopraxia.jsonpath.fake.FakeLoggingContext;
import java.util.EnumSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class JsonPathTests {

  @Test
  public void testPath() {
    Configuration.setDefaults(
        new Configuration.Defaults() {
          final JsonProvider jsonProvider = new EchopraxiaJsonProvider();
          final MappingProvider mappingProvider = new EchopraxiaMappingProvider();

          @Override
          public JsonProvider jsonProvider() {
            return jsonProvider;
          }

          @Override
          public MappingProvider mappingProvider() {
            return mappingProvider;
          }

          @Override
          public Set<Option> options() {
            return EnumSet.noneOf(Option.class);
          }
        });

    LoggingContext context = FakeLoggingContext.empty();
    JsonPath.read(context, "$.store.book[*].author");
  }
}
