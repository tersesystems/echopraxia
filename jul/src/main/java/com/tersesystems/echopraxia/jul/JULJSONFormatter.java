package com.tersesystems.echopraxia.jul;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tersesystems.echopraxia.api.Field;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import org.jetbrains.annotations.NotNull;

/**
 * Shamelessly inspired by <a
 * href="https://github.com/devatherock/jul-jsonformatter">jul-jsonformatter</a>
 */
public class JULJSONFormatter extends Formatter {

  private static final ResourceBundle bundle = ResourceBundle.getBundle("echopraxia/jsonformatter");

  public static final String TIMESTAMP_FORMAT = bundle.getString("timestamp_format");
  public static final String TIMESTAMP_ZONEID = bundle.getString("timestamp_zoneid");

  /** Pattern for the logged time which is in ISO 8601 format */
  public static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT).withZone(ZoneId.of(TIMESTAMP_ZONEID));

  /** Maximum number of thread names to cache */
  public static final int THREAD_NAME_CACHE_SIZE = 10000;

  /** JSON key for log time */
  public static final String KEY_TIMESTAMP = bundle.getString("timestamp");

  /** JSON key for logger name */
  public static final String KEY_LOGGER_NAME = bundle.getString("logger_name");

  /** JSON key for log level */
  public static final String KEY_LOG_LEVEL = bundle.getString("level");

  /** JSON key for thread name that issued the log statement */
  public static final String KEY_THREAD_NAME = bundle.getString("thread_name");

  /** JSON key for class name that issued the log statement */
  public static final String KEY_LOGGER_CLASS = bundle.getString("class");

  /** JSON key for method name that issued the log statement */
  public static final String KEY_LOGGER_METHOD = bundle.getString("method");

  /** JSON key for the message being logged */
  public static final String KEY_MESSAGE = bundle.getString("message");

  /** JSON key for the exception being logged */
  public static final String KEY_EXCEPTION = bundle.getString("exception");

  public enum ExceptionKeys {
    exception_class,
    exception_message,
    stack_trace
  }

  private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

  private static final ThreadMXBean THREAD_MX_BEAN = ManagementFactory.getThreadMXBean();

  /** Cache of thread names */
  private static final Map<Integer, String> THREAD_NAME_CACHE =
      new LinkedHashMap<Integer, String>() {

        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
          return (size() > THREAD_NAME_CACHE_SIZE);
        }
      };

  private final boolean useSlf4jLevelNames;

  public JULJSONFormatter() {
    LogManager manager = LogManager.getLogManager();
    String cname = getClass().getName();

    String value = manager.getProperty(cname + ".use_slf4j_level_names");
    useSlf4jLevelNames = Boolean.parseBoolean(value);
  }

  @Override
  public String format(LogRecord record) {
    Map<String, Object> object = new LinkedHashMap<>();
    try {
      object.put(
          KEY_TIMESTAMP, TIMESTAMP_FORMATTER.format(Instant.ofEpochMilli(record.getMillis())));
      object.put(KEY_LOGGER_NAME, record.getLoggerName());
      object.put(KEY_LOG_LEVEL, getLogLevel(record));
      object.put(KEY_THREAD_NAME, getThreadName(record.getThreadID()));

      if (record.getSourceClassName() != null) {
        object.put(KEY_LOGGER_CLASS, record.getSourceClassName());
      }

      if (record.getSourceMethodName() != null) {
        object.put(KEY_LOGGER_METHOD, record.getSourceMethodName());
      }
      object.put(KEY_MESSAGE, formatMessage(record));

      final Throwable thrown = record.getThrown();
      if (thrown != null) {
        object.put(KEY_EXCEPTION, createExceptionInfo(thrown));
      }

      if (record instanceof EchopraxiaLogRecord) {
        final EchopraxiaLogRecord er = (EchopraxiaLogRecord) record;

        // render the logger fields first
        Field[] loggerFields = er.getLoggerFields();
        for (Field field : loggerFields) {
          object.put(field.name(), field.value());
        }

        // render the argument fields after logger fields
        final Field[] fields = (Field[]) record.getParameters();
        for (Field field : fields) {
          object.put(field.name(), field.value());
        }
      }

      return mapper.writeValueAsString(object) + System.lineSeparator();
    } catch (JsonProcessingException e) {
      return object.toString();
    }
  }

  private String getLogLevel(LogRecord record) {
    return useSlf4jLevelNames
        ? renameLogLevel(record.getLevel().getName())
        : record.getLevel().getName();
  }

  @NotNull
  private static Map<ExceptionKeys, Object> createExceptionInfo(Throwable thrown) {
    Map<ExceptionKeys, Object> exceptionInfo = new java.util.EnumMap<>(ExceptionKeys.class);
    exceptionInfo.put(ExceptionKeys.exception_class, thrown.getClass().getName());

    if (thrown.getMessage() != null) {
      exceptionInfo.put(ExceptionKeys.exception_message, thrown.getMessage());
    }

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    thrown.printStackTrace(pw);
    pw.close();
    exceptionInfo.put(ExceptionKeys.stack_trace, sw.toString());
    return exceptionInfo;
  }

  /** Gets the thread name from the threadId present in the logRecord. */
  private static String getThreadName(int logRecordThreadId) {
    String result = THREAD_NAME_CACHE.get(logRecordThreadId);

    if (result != null) {
      return result;
    }

    if (logRecordThreadId > Integer.MAX_VALUE / 2) {
      result = String.valueOf(logRecordThreadId);
    } else {
      ThreadInfo threadInfo = THREAD_MX_BEAN.getThreadInfo(logRecordThreadId);
      if (threadInfo == null) {
        return String.valueOf(logRecordThreadId);
      }
      result = threadInfo.getThreadName();
    }

    synchronized (THREAD_NAME_CACHE) {
      THREAD_NAME_CACHE.put(logRecordThreadId, result);
    }

    return result;
  }

  private String renameLogLevel(String logLevel) {

    switch (logLevel) {
      case "FINEST":
        return "TRACE";

      case "FINER":
      case "FINE":
        return "DEBUG";

      case "INFO":
      case "CONFIG":
        return "INFO";

      case "WARNING":
        return "WARN";

      case "SEVERE":
        return "ERROR";

      default:
        return logLevel;
    }
  }
}
