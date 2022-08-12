package com.tersesystems.echopraxia.diff;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DiffObjectMapper {
  private DiffObjectMapper() {}

  public static final ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.findAndRegisterModules();
  }
}
