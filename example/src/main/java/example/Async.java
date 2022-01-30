package example;

import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import java.util.concurrent.*;
import java.util.function.Function;
import org.slf4j.MDC;

public class Async {

  private static final Logger<?> logger = LoggerFactory.getLogger();

  public static void main(String[] args) {
    ExecutorService executor =
        Executors.newSingleThreadExecutor(
            r -> {
              Thread t = new Thread(r);
              t.setDaemon(true);
              t.setName("logging-thread");
              return t;
            });

    logger
        .withExecutor(executor)
        .withThreadContext()
        .asyncInfo(
            h -> {
              MDC.put("herp", "derp");
              h.log("This logs in the main flow, but does so asynchronously");
            });

    final CompletableFuture<Long> future =
        CompletableFuture.supplyAsync(
                () -> {
                  String mdcvalue = MDC.get("herp");
                  logger.info("supplyAsync: {}", fb -> fb.onlyString("mdcvalue", mdcvalue));
                  return System.currentTimeMillis();
                })
            .thenApplyAsync(wireTap(), executor);
  }

  static <T> Function<T, T> wireTap() {
    return element -> {
      logger.info("wireTap: {}", fb -> fb.onlyString("element", element.toString()));
      return element;
    };
  }
}
