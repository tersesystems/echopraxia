package echopraxia.jul;

public class TestEchopraxiaService extends JULEchopraxiaService {

  public TestEchopraxiaService() {
    super();
    this.exceptionHandler = new StaticExceptionHandler();
  }
}
