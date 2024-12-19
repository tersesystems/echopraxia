package echopraxia.log4j.layout;

import echopraxia.api.Field;
import java.util.List;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolverContext;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolverFactory;
import org.apache.logging.log4j.layout.template.json.resolver.TemplateResolverConfig;
import org.apache.logging.log4j.layout.template.json.resolver.TemplateResolverFactory;

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

  static final class EchopraxiaFieldsResolver extends AbstractEchopraxiaResolver {

    private static final EchopraxiaFieldsResolver INSTANCE = new EchopraxiaFieldsResolver();

    static EchopraxiaFieldsResolver getInstance() {
      return INSTANCE;
    }

    static String getName() {
      return "echopraxiaFields";
    }

    @Override
    protected List<Field> resolveFields(EchopraxiaFieldsMessage message) {
      return message.getFields();
    }
  }
}
