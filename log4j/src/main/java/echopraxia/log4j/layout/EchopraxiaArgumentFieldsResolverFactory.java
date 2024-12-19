package echopraxia.log4j.layout;

import echopraxia.api.Field;
import java.util.List;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolverContext;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolverFactory;
import org.apache.logging.log4j.layout.template.json.resolver.TemplateResolverConfig;
import org.apache.logging.log4j.layout.template.json.resolver.TemplateResolverFactory;

@Plugin(name = "ArgumentFieldResolverFactory", category = TemplateResolverFactory.CATEGORY)
public class EchopraxiaArgumentFieldsResolverFactory implements EventResolverFactory {

  private static final EchopraxiaArgumentFieldsResolverFactory INSTANCE =
      new EchopraxiaArgumentFieldsResolverFactory();

  private EchopraxiaArgumentFieldsResolverFactory() {}

  @PluginFactory
  public static EchopraxiaArgumentFieldsResolverFactory getInstance() {
    return INSTANCE;
  }

  @Override
  public String getName() {
    return EchopraxiaArgumentFieldsResolver.getName();
  }

  @Override
  public EchopraxiaArgumentFieldsResolver create(
      final EventResolverContext context, final TemplateResolverConfig config) {
    return EchopraxiaArgumentFieldsResolver.getInstance();
  }

  static final class EchopraxiaArgumentFieldsResolver extends AbstractEchopraxiaResolver {

    private static final EchopraxiaArgumentFieldsResolver INSTANCE =
        new EchopraxiaArgumentFieldsResolver();

    static EchopraxiaArgumentFieldsResolver getInstance() {
      return INSTANCE;
    }

    static String getName() {
      return "echopraxiaArgumentFields";
    }

    @Override
    protected List<Field> resolveFields(EchopraxiaFieldsMessage message) {
      return message.getArgumentFields();
    }
  }
}
