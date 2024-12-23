package echopraxia;

import echopraxia.api.FieldBuilder;
import echopraxia.logging.api.*;
import echopraxia.logging.spi.*;
import echopraxia.simple.Logger;

class MyLoggerFactory {
  public static class MyFieldBuilder implements FieldBuilder {
    // Add your own field builder methods in here
  }

  private static final MyFieldBuilder fieldBuilder = new MyFieldBuilder();

  public static MyLogger getLogger(Class<?> clazz) {
    final CoreLogger core = CoreLoggerFactory.getLogger(Logger.class.getName(), clazz);
    return new MyLogger(core);
  }

  public static final class MyLogger extends Logger {
    public static final String FQCN = MyLogger.class.getName();

    public MyLogger(CoreLogger logger) {
      super(logger);
    }

    @Override
    public FieldBuilder fieldBuilder() {
      return fieldBuilder;
    }

    public void notice(String message) {
      // the caller is MyLogger specifically, so we need to let the logging framework know how to
      // address it.
      core().withFQCN(FQCN).log(Level.INFO, message, fb -> fb.bool("notice", true), fieldBuilder());
    }
  }
}

class Main {
  private static final MyLoggerFactory.MyLogger logger = MyLoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    logger.notice("this has a notice field added");
  }
}
