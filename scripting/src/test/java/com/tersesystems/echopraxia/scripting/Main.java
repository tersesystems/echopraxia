package com.tersesystems.echopraxia.scripting;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.Logger;
import com.tersesystems.echopraxia.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Main {

  private static final Logger<?> logger =
      LoggerFactory.getLogger().withFields(fb -> fb.onlyString("name", "Will"));

  public static void main(String[] args) throws IOException, InterruptedException {
    final Path dir = Files.createTempDirectory("echopraxia");
    try (ScriptWatchService watchService = new ScriptWatchService(dir)) {
      Path filePath = dir.resolve("testfile");
      Files.write(filePath, lines("will"));

      final ScriptHandle handle =
          watchService.create(filePath, e -> logger.error(e.getMessage(), e));
      final Condition condition = ScriptCondition.create(false, handle);
      logger.info(condition, "logging first message ");

      for (int i = 0; i < 10; i++) {
        // Write a change and then sleep
        Files.write(filePath, lines("steve"));
        Thread.sleep(300);

        // and then we should be able to see the changed condition evaluate.
        logger.info(condition, "this should never log because the condition doesn't pass!");

        // Write the condition again and give some time for the watcher
        Files.write(filePath, lines("will"));
        Thread.sleep(300);

        // Now the first time it sees, it'll re-evaluate and process again!
        logger.info(condition, "this logs because we changed the script back.");
      }
    }
  }

  private static List<String> lines(String name) {
    return Arrays.asList(
        "import * as std from \"std\";",
        "alias std.strings as str;",
        "library echopraxia {",
        "  function evaluate: (string level, dict fields) ->",
        String.format("    str.lower_case(fields[:name]) == \"%s\";", name),
        "}");
  }
}
