package com.tersesystems.echopraxia.log4j.layout;

import com.tersesystems.echopraxia.model.Field;
import java.util.List;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolverContext;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolverFactory;
import org.apache.logging.log4j.layout.template.json.resolver.TemplateResolverConfig;
import org.apache.logging.log4j.layout.template.json.resolver.TemplateResolverFactory;

@Plugin(name = "ContextFieldResolverFactory", category = TemplateResolverFactory.CATEGORY)
public class EchopraxiaContextFieldsResolverFactory implements EventResolverFactory {

  private static final EchopraxiaContextFieldsResolverFactory INSTANCE =
      new EchopraxiaContextFieldsResolverFactory();

  private EchopraxiaContextFieldsResolverFactory() {}

  @PluginFactory
  public static EchopraxiaContextFieldsResolverFactory getInstance() {
    return INSTANCE;
  }

  @Override
  public String getName() {
    return EchopraxiaContextFieldsResolver.getName();
  }

  @Override
  public EchopraxiaContextFieldsResolver create(
      final EventResolverContext context, final TemplateResolverConfig config) {
    return EchopraxiaContextFieldsResolver.getInstance();
  }

  static final class EchopraxiaContextFieldsResolver extends AbstractEchopraxiaResolver {

    private static final EchopraxiaContextFieldsResolver INSTANCE =
        new EchopraxiaContextFieldsResolver();

    static EchopraxiaContextFieldsResolver getInstance() {
      return INSTANCE;
    }

    static String getName() {
      return "echopraxiaContextFields";
    }

    @Override
    protected List<Field> resolveFields(EchopraxiaFieldsMessage message) {
      return message.getLoggerFields();
    }
  }
}
