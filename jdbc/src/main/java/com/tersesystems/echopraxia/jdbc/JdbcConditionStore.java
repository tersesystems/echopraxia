package com.tersesystems.echopraxia.jdbc;

import com.tersesystems.echopraxia.Condition;
import com.tersesystems.echopraxia.scripting.ScriptCondition;
import com.tersesystems.echopraxia.scripting.ScriptException;
import com.tersesystems.echopraxia.scripting.ScriptHandle;
import java.sql.*;
import java.time.Instant;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;

/** Store conditions in JDBC, organized by class name. */
public class JdbcConditionStore implements AutoCloseable {

  private static final LongAdder threadCount = new LongAdder();

  private final Connection connection;
  private final BiConsumer<String, Throwable> exceptionConsumer;

  private final PreparedStatement touchedStatement;
  private final PreparedStatement selectStatement;
  private final PreparedStatement insertStatement;

  private final ExecutorService executor =
      Executors.newSingleThreadExecutor(
          r -> {
            Thread t = new Thread(r);
            threadCount.increment();
            t.setName("jdbc-condition-store-" + threadCount.intValue());
            return t;
          });

  public JdbcConditionStore(
      String jdbcUrl, ResourceBundle resources, BiConsumer<String, Throwable> exceptionConsumer)
      throws SQLException {
    this.exceptionConsumer = exceptionConsumer;
    connection = DriverManager.getConnection(jdbcUrl);
    connection.setAutoCommit(true);

    initialize(resources);

    touchedStatement = connection.prepareStatement(resources.getString("touched"));
    selectStatement = connection.prepareStatement(resources.getString("select"));
    insertStatement = connection.prepareStatement(resources.getString("insert"));
  }

  private void initialize(ResourceBundle resources) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate(resources.getString("create"));
      statement.execute(resources.getString("index"));
      statement.execute(resources.getString("trigger"));
    }
  }

  private String defaultScript() {
    return "library echopraxia {\n  function evaluate: (string level, dict fields) -> true;\n}";
  }

  void insert(String name, String description, String script) throws SQLException {
    insertStatement.setString(1, name);
    insertStatement.setString(2, description);
    insertStatement.setString(3, script);
    insertStatement.execute();
  }

  public Condition create(String name) {
    ScriptHandle handle = new CallbackHandle(name);
    return ScriptCondition.create(false, handle);
  }

  private class CallbackHandle implements ScriptHandle {
    private final String name;

    private final AtomicBoolean invalid = new AtomicBoolean(true);

    private volatile Instant instant;
    private volatile String script;

    public CallbackHandle(String name) {
      this.name = name;
      this.instant = refreshTouched();
      refreshScript();
    }

    @Override
    public boolean isInvalid() {
      CompletableFuture.supplyAsync(this::refreshTouched, executor)
          .thenAccept(
              touched -> {
                boolean isInvalid = instant.isBefore(touched);
                if (isInvalid) {
                  invalid.set(true);
                }
              });
      return invalid.get();
    }

    @Override
    public String script() {
      // Script is called in two situations:
      //   if the handle is null (never been loaded)
      //   if isInvalid is true and the script manager needs to refresh.
      // We are willing to block for this method.
      refreshScript();
      instant = Instant.now();
      invalid.set(false);
      return script;
    }

    @Override
    public String path() {
      return name;
    }

    @Override
    public void report(Throwable e) {
      exceptionConsumer.accept(name, e);
    }

    @Override
    public void close() throws Exception {
      try {
        insertStatement.close();
      } catch (SQLException e) {
        // do nothing
      }

      try {
        touchedStatement.close();
      } catch (SQLException e) {
        // do nothing
      }

      try {
        selectStatement.close();
      } catch (SQLException e) {
        // do nothing
      }
    }

    private Instant refreshTouched() {
      try {
        touchedStatement.setString(1, name);
        try (ResultSet rs = touchedStatement.executeQuery()) {
          if (rs.next()) {
            Timestamp ts = rs.getTimestamp(1);
            return ts.toInstant();
          } else {
            return Instant.now();
          }
        }
      } catch (SQLException e) {
        throw new ScriptException(e);
      }
    }

    private void refreshScript() {
      try {
        selectStatement.setString(1, name);
        try (ResultSet rs = selectStatement.executeQuery()) {
          if (rs.next()) {
            this.script = rs.getString(1);
          } else {
            this.script = defaultScript();
            insert(name, "example description", script);
          }
        }
      } catch (SQLException e) {
        throw new ScriptException(e);
      }
    }
  }

  @Override
  public void close() throws Exception {
    try {
      executor.shutdownNow();
    } finally {
      connection.close();
    }
  }
}
