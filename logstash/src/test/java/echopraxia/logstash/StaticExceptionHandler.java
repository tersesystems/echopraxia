package echopraxia.logstash;

import echopraxia.logging.spi.ExceptionHandler;
import java.util.ArrayList;
import java.util.List;

public class StaticExceptionHandler implements ExceptionHandler {

  private static final List<Throwable> exceptions = new ArrayList<>();

  public static Throwable head() {
    return exceptions.get(0);
  }

  public static void clear() {
    exceptions.clear();
  }

  @Override
  public void handleException(Throwable e) {
    exceptions.add(e);
  }
}
