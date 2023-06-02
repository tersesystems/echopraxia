
## Filters

There are times when you want to add a field or a condition to all loggers.  Although you can wrap individual loggers or create your own wrapper around `LoggerFactory`, this can be a labor-intensive process that requires lots of code modification, and must be handled for fluent, semantic, async, and regular loggers.

Echopraxia includes filters that wrap around the `CoreLogger` returned by `CoreLoggerFactory` that provides the ability to modify the core logger from a single pipeline in the code.

For example, to add a `uses_filter` field to every Echopraxia logger:

```java
package example;

import com.tersesystems.echopraxia.api.*;

public class ExampleFilter implements CoreLoggerFilter {
  @Override
  public CoreLogger apply(CoreLogger coreLogger) {
    return coreLogger
        .withFields(fb -> fb.bool("uses_filter", true), FieldBuilder.instance());
  }
}
```

Filters must extend the `CoreLoggerFilter` interface, and must have a no-args constructor.

Filters must have a fully qualified class name in the `/echopraxia.properties` file as a resource somewhere in your classpath.  The format is `filter.N` where N is the order in which filters should be loaded.

```properties
filter.0=example.ExampleFilter
```

Filters are particularly helpful when you need to provide "out of context" information for your conditions. 

For example, imagine that you have a situation in which the program uses more CPU or memory than normal in production, but works fine in a staging environment.  Using [OSHI](https://github.com/oshi/oshi) and a filter, you can provide the [machine statistics](https://speakerdeck.com/lyddonb/what-is-happening-attempting-to-understand-our-systems?slide=133) and evaluate with dynamic conditions.

```java
public class SystemInfoFilter implements CoreLoggerFilter {

  private final SystemInfo systemInfo;

  public SystemInfoFilter() {
     systemInfo = new SystemInfo();
  }

  @Override
  public CoreLogger apply(CoreLogger coreLogger) {
    HardwareAbstractionLayer hardware = systemInfo.getHardware();
    GlobalMemory mem = hardware.getMemory();
    CentralProcessor proc = hardware.getProcessor();
    double[] loadAverage = proc.getSystemLoadAverage(3);

    // Now you can add conditions based on these fields, and conditionally
    // enable logging based on your load and memory!
    return coreLogger.withFields(fb -> {
        Field loadField = fb.object("load_average", //
                fb.number("1min", loadAverage[0]), //
                fb.number("5min", loadAverage[1]), //
                fb.number("15min", loadAverage[2]));
        Field memField = fb.object("mem", //
                fb.number("available", mem.getAvailable()), //
                fb.number("total", mem.getTotal()));
        Field sysinfoField = fb.object("sysinfo", loadField, memField);
        return sysinfoField;
      }, FieldBuilder.instance());
  }
}
```

Please see the [system info example](https://github.com/tersesystems/echopraxia-examples/tree/main/system-info) for details.
