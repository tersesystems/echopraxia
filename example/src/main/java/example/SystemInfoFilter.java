package example;

import com.tersesystems.echopraxia.Field;
import com.tersesystems.echopraxia.core.CoreLogger;
import com.tersesystems.echopraxia.core.CoreLoggerFilter;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

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
    return coreLogger.withFields(
        fb -> {
          Field loadField =
              fb.object(
                  "load_average", //
                  fb.number("1min", loadAverage[0]), //
                  fb.number("5min", loadAverage[1]), //
                  fb.number("15min", loadAverage[2]));
          Field memField =
              fb.object(
                  "mem", //
                  fb.number("available", mem.getAvailable()), //
                  fb.number("total", mem.getTotal()));
          Field sysinfoField = fb.object("sysinfo", loadField, memField);
          return fb.only(sysinfoField);
        },
        Field.Builder.instance());
  }
}
