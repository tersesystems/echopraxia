package com.tersesystems.echopraxia.scripting;

import static com.tersesystems.echopraxia.api.Level.INFO;

import com.tersesystems.echopraxia.api.Condition;
import com.tersesystems.echopraxia.api.LoggingContext;
import com.tersesystems.echopraxia.model.Field;
import com.tersesystems.echopraxia.model.Value;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Main {

  public static void main(String[] args) throws IOException, InterruptedException {
    final Path dir = Files.createTempDirectory("echopraxia");
    try (ScriptWatchService watchService = new ScriptWatchService(dir)) {
      Path filePath = dir.resolve("testfile");
      Files.write(filePath, lines("INFO"));

      final ScriptHandle handle = watchService.watchScript(filePath, Throwable::printStackTrace);
      final Condition condition = ScriptCondition.create(handle);
      LoggingContext context =
          new FakeLoggingContext(Field.keyValue("correlation_id", Value.string("match")));
      if (condition.test(INFO, context)) {
        System.out.println("First test should eval but take a while...");
      }

      for (int i = 0; i < 100; i++) {
        // Write a change and then sleep
        Files.write(filePath, lines("DEBUG"));
        Thread.sleep(300);

        // and then we should be able to see the changed condition evaluate.
        if (condition.test(INFO, context)) {
          System.out.println(
              "this doesn't log, because even though we pass in INFO the script changed to DEBUG");
        }

        // Write the condition again and give some time for the watcher
        Files.write(filePath, lines("INFO"));
        Thread.sleep(300);

        // Now the first time it sees, it'll re-evaluate and process again!
        if (condition.test(INFO, context)) {
          System.out.println("this logs because we have changed the script back to INFO.");
        }

        Files.deleteIfExists(filePath);
        Thread.sleep(300);
        if (condition.test(INFO, context)) {
          System.out.println("see what happens when script is deleted");
        }
      }
    }
  }

  private static List<String> lines(String name) {
    return Arrays.asList(
        "library echopraxia {",
        "  function evaluate: (string level, dict ctx) ->",
        "    level == \"" + name + "\";",
        "}");
  }
}
