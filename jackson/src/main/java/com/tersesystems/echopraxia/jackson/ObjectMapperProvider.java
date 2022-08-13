package com.tersesystems.echopraxia.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This interface provides an object mapper, with a default pointing to a static final instance.
 *
 * <p>You can override this method in your own field builder if you need a different object mapper.
 */
public interface ObjectMapperProvider {
  default ObjectMapper _objectMapper() {
    return DefaultObjectMapper.OBJECT_MAPPER;
  }
}

final class DefaultObjectMapper {
  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    // if this fails for any reason, we'll get a "NoClassDefFoundError"
    // which can be very unintuitive.
    OBJECT_MAPPER.findAndRegisterModules();
  }
}
