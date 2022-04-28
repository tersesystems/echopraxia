package com.tersesystems.echopraxia.logstash.jackson;

import static com.tersesystems.echopraxia.api.Field.Value;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.tersesystems.echopraxia.api.Field;

/** A Jackson module that is loaded in automatically by logstash-logback-encoder. */
public class EchopraxiaModule extends Module {
  //
  // https://github.com/FasterXML/jackson-docs/wiki/JacksonHowToCustomSerializers

  public EchopraxiaModule() {
    super();
  }

  @Override
  public String getModuleName() {
    return EchopraxiaModule.class.getSimpleName();
  }

  @Override
  @SuppressWarnings("deprecation")
  public Version version() {
    final ClassLoader loader = EchopraxiaModule.class.getClassLoader();
    return VersionUtil.mavenVersionFor(loader, "com.tersesystems.echopraxia", "logstash");
  }

  @Override
  public void setupModule(final SetupContext context) {
    final SimpleSerializers serializers = new SimpleSerializers();
    serializers.addSerializer(Field.class, FieldSerializer.INSTANCE);
    serializers.addSerializer(Value.class, ValueSerializer.INSTANCE);
    context.addSerializers(serializers);
  }
}
