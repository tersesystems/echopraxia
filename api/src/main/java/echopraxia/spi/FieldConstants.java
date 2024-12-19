package echopraxia.spi;

import java.util.ResourceBundle;

public final class FieldConstants {

  private static final ResourceBundle bundle = ResourceBundle.getBundle("echopraxia/fields");

  public static final String EXCEPTION = bundle.getString("exception");
  public static final String CLASS_NAME = bundle.getString("className");
  public static final String MESSAGE = bundle.getString("message");
  public static final String CAUSE = bundle.getString("cause");
  public static final String STACK_TRACE = bundle.getString("stackTrace");
  public static final String FILE_NAME = bundle.getString("fileName");
  public static final String LINE_NUMBER = bundle.getString("lineNumber");
  public static final String METHOD_NAME = bundle.getString("methodName");
}
