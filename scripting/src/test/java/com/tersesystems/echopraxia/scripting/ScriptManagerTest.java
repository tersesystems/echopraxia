package com.tersesystems.echopraxia.scripting;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.tersesystems.echopraxia.api.Level;
import com.tersesystems.echopraxia.api.LoggingContext;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;
import org.junit.jupiter.api.Test;

public class ScriptManagerTest {

  public String buildScript() {
    StringBuilder b = new StringBuilder("library echopraxia {");
    b.append("  function evaluate: (string level, dict ctx) ->");
    b.append("    true;");
    b.append("}");
    return b.toString();
  }

  @Test
  public void testConcurrency() {
    String script = buildScript();
    final ScriptHandle handle =
        new ScriptHandle() {
          @Override
          public boolean isInvalid() {
            return false;
          }

          @Override
          public String script() {
            return script;
          }

          @Override
          public String path() {
            return "<memory>";
          }

          @Override
          public void report(Throwable e) {
            e.printStackTrace();
          }

          @Override
          public void close() throws Exception {}
        };
    final ScriptManager scriptManager = new ScriptManager(handle);

    LoggingContext empty = new FakeLoggingContext();
    int parallel = 4;
    final ExecutorService executorService = Executors.newWorkStealingPool(parallel);
    LongAdder count = new LongAdder();
    int limit = 3000000; // about 27 seconds on my laptop

    try {
      for (int j = 0; j < parallel; j++) {
        CompletableFuture.runAsync(
            () -> {
              for (int i = 0; i < limit; i++) {
                try {
                  if (scriptManager.execute(false, Level.INFO, empty)) {
                    count.increment();
                  }
                } catch (Exception e) {
                  e.printStackTrace();
                }
              }
            },
            executorService);
      }

      await().atMost(30, SECONDS).until(() -> count.intValue() >= limit * parallel);
      assertThat(count.intValue()).isEqualTo(limit * parallel);
    } finally {
      executorService.shutdown();
    }
  }
}
