package example;

import com.tersesystems.echopraxia.AsyncLogger;
import com.tersesystems.echopraxia.AsyncLoggerFactory;
import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.LoggerFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Async {

  // An executor that has a single thread.  This is nice for ordering, but please
  // see the README on how to configure executors for CPU & IO bound operations.
  private static final ExecutorService loggingExecutor =
      Executors.newSingleThreadExecutor(
          r -> {
            Thread t = new Thread(r);
            t.setDaemon(true); // daemon means the thread won't stop JVM from exiting
            t.setName("logging-thread");
            return t;
          });

  // Simulate an expensive condition that will tie up the thread for an unreasonable
  // amount of time.
  private static final Condition expensiveCondition =
      (level, context) -> {
        try {
          Thread.sleep(1000L);
          return true;
        } catch (InterruptedException e) {
          return false;
        }
      };

  private static final AsyncLogger<?> logger =
      AsyncLoggerFactory.getLogger().withExecutor(loggingExecutor).withCondition(expensiveCondition);

  public static void main(String[] args) throws InterruptedException {
    System.out.println("BEFORE logging block");
    for (int i = 0; i < 10; i++) {
      // This should take no time on the rendering thread :-)
      logger.info("Prints out after expensive condition");
    }
    System.out.println("AFTER logging block");
    System.out.println("Sleeping so that the JVM stays up");
    Thread.sleep(1001L * 10L);
  }
}
