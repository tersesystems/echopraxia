package com.tersesystems.echopraxia;

import com.tersesystems.echopraxia.api.*;
import com.tersesystems.echopraxia.support.*;
import org.jetbrains.annotations.NotNull;

public class MyLogger extends AbstractLoggerSupport<MyLogger, MyFieldBuilder>
    implements DefaultLoggerMethods<MyFieldBuilder> {

  public MyLogger(CoreLogger core, MyFieldBuilder fieldBuilder) {
    super(core, fieldBuilder, MyLogger.class);
  }

  public void notice(String message) {
    // the caller is MyLogger specifically, so we need to let the logging framework know how to
    // address it.
    core.withFQCN(getClass().getName())
        .log(Level.INFO, message, fb -> fb.onlyBool("notice", true), fieldBuilder());
  }

  @Override
  protected @NotNull MyLogger newLogger(CoreLogger core) {
    return new MyLogger(core, fieldBuilder());
  }

  protected @NotNull MyLogger neverLogger() {
    return new MyLogger(core.withCondition(Condition.never()), fieldBuilder);
  }
}

class MyFieldBuilder implements FieldBuilder {}

class MyLoggerFactory {

  private static final MyFieldBuilder fieldBuilder = new MyFieldBuilder();

  public static MyLogger getLogger(Class<?> clazz) {
    final CoreLogger core = CoreLoggerFactory.getLogger(LoggerMethods.class.getName(), clazz);
    return new MyLogger(core, fieldBuilder);
  }
}

class Main {
  private static final MyLogger logger = MyLoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    logger.notice("this has a notice field added");
  }
}
