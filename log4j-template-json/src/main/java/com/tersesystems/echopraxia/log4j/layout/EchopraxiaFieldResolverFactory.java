package com.tersesystems.echopraxia.log4j.layout;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.layout.template.json.resolver.*;

@Plugin(name = "FieldResolverFactory", category = TemplateResolverFactory.CATEGORY)
public class EchopraxiaFieldResolverFactory implements EventResolverFactory {

  private static final EchopraxiaFieldResolverFactory INSTANCE =
      new EchopraxiaFieldResolverFactory();

  private EchopraxiaFieldResolverFactory() {}

  @PluginFactory
  public static EchopraxiaFieldResolverFactory getInstance() {
    return INSTANCE;
  }

  @Override
  public String getName() {
    return EchopraxiaFieldsResolver.getName();
  }

  @Override
  public EchopraxiaFieldsResolver create(
      final EventResolverContext context, final TemplateResolverConfig config) {
    return EchopraxiaFieldsResolver.getInstance();
  }
}
