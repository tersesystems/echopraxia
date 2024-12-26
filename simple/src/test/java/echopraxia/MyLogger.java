package echopraxia;

import echopraxia.api.FieldBuilder;
import echopraxia.logging.api.*;
import echopraxia.logging.spi.*;
import echopraxia.simple.Logger;

class MyLoggerFactory {
  public static class MyFieldBuilder implements FieldBuilder {
    // Add your own field builder methods in here
  }

  public static final MyFieldBuilder FIELD_BUILDER = new MyFieldBuilder();

  public static MyLogger getLogger(Class<?> clazz) {
    final CoreLogger core = CoreLoggerFactory.getLogger(Logger.class.getName(), clazz);
    return new MyLogger(core);
  }

  public static final class MyLogger extends Logger {
    public static final String FQCN = MyLogger.class.getName();

    public MyLogger(CoreLogger logger) {
      super(logger);
    }

    public void notice(String message) {
      // the caller is MyLogger specifically, so we need to let the logging framework know how to
      // address it.
      core().withFQCN(FQCN)
            .withFields(fb -> fb.bool("notice", true), FIELD_BUILDER)
            .log(Level.INFO, message);
    }
  }
}

class Main {
  private static final MyLoggerFactory.MyLogger logger = MyLoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    logger.notice("this has a notice field added");
  }
}
