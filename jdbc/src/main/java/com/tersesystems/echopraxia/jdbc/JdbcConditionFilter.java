package com.tersesystems.echopraxia.jdbc;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerFilter;
import com.tersesystems.echopraxia.scripting.ScriptException;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class JdbcConditionFilter implements CoreLoggerFilter {
  private final JdbcConditionStore conditionManager;

  public JdbcConditionFilter() {
    try {
      // https://github.com/xerial/sqlite-jdbc#how-to-specify-database-files
      // connection = DriverManager.getConnection("jdbc:sqlite::memory:");
      // still need to report this better...
      // Need to specify files as sqlite / postgresql etc
      final ResourceBundle bundle = ResourceBundle.getBundle("echopraxia.sqlite");
      final String jdbcUrl = "jdbc:sqlite:conditions.db";

      conditionManager =
          new JdbcConditionStore(
              jdbcUrl,
              bundle,
              (name, e) -> {
                e.printStackTrace();
              });
    } catch (SQLException e) {
      throw new ScriptException(e);
    }
  }

  @Override
  public CoreLogger apply(CoreLogger coreLogger) {
    final Condition condition = conditionManager.create(coreLogger.getName());
    return coreLogger.withCondition(condition);
  }
}
