package com.tersesystems.echopraxia.jdbc;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerFilter;
import com.tersesystems.echopraxia.scripting.ScriptException;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SqliteConditionFilter implements CoreLoggerFilter {
  private final JdbcConditionStore conditionStore;

  public SqliteConditionFilter() {
    try {
      // https://github.com/xerial/sqlite-jdbc#how-to-specify-database-files
      // connection = DriverManager.getConnection("jdbc:sqlite::memory:");
      // still need to report this better...
      // Need to specify files as sqlite / postgresql etc
      final ResourceBundle bundle = ResourceBundle.getBundle("echopraxia.sqlite");
      final String jdbcUrl = "jdbc:sqlite:conditions.db";

      conditionStore =
          new JdbcConditionStore(
              true,
              jdbcUrl,
              bundle,
              (name, e) -> {
                e.printStackTrace();
              });

      conditionStore.runDDL(bundle);
    } catch (SQLException e) {
      throw new ScriptException(e);
    }
  }

  @Override
  public CoreLogger apply(CoreLogger coreLogger) {
    final Condition condition = conditionStore.create(coreLogger.getName());
    return coreLogger.withCondition(condition);
  }
}
