# Dynamic Conditions with Scripts

One of the limitations of logging is that it's not that easy to change logging levels in an application at run-time.  In modern applications, you typically have complex inputs and may want to enable logging for some very specific inputs without turning on your logging globally.

Script Conditions lets you tie your conditions to scripts that you can change and re-evaluate at runtime.

The security concerns surrounding Groovy or Javascript make them unsuitable in a logging environment.  Fortunately, Echopraxia provides a [Tweakflow](https://twineworks.github.io/tweakflow) script integration that lets you evaluate logging statements **safely**.  

Tweakflow comes with a [VS Code integration](https://marketplace.visualstudio.com/items?itemName=twineworks.tweakflow), a [reference guide](https://twineworks.github.io/tweakflow/reference.html), and a [standard library](https://twineworks.github.io/tweakflow/modules/std.html) that contains useful regular expression and date manipulation logic.

### Installation

Because Scripting has a dependency on Tweakflow, it is broken out into a distinct library that you must add to your build.

Maven:

```xml
<dependency>
  <groupId>com.tersesystems.echopraxia</groupId>
  <artifactId>scripting</artifactId>
  <version><VERSION></version>
</dependency>
```

Gradle:

```gradle
implementation "com.tersesystems.echopraxia:scripting:<VERSION>" 
```

## Script Syntax

The call site for a script is the function `evaluate` inside a library called `echopraxia`.  The level and context are
passed through as `(string level, dict ctx)`, where `ctx` is a dictionary of functions that connect back to the logging context.

Methods in the context are snake case, separated by underscores. For example, to call the equivalent of `ctx.findString("$.person.name")`, you would call `ctx[:find_string]("$.person.name")`.  

* `ctx[:find_number]` returns a [number](https://twineworks.github.io/tweakflow/reference.html#long) or null
* `ctx[:find_string]` returns a [string](https://twineworks.github.io/tweakflow/reference.html#string) or null
* `ctx[:find_boolean]` returns a [boolean](https://twineworks.github.io/tweakflow/reference.html#boolean) or null
* `ctx[:find_object]` returns a [dict](https://twineworks.github.io/tweakflow/reference.html#dict) or null
* `ctx[:find_list]` returns a [list](https://twineworks.github.io/tweakflow/reference.html#list) or null
* `ctx[:find_null]` returns a [boolean](https://twineworks.github.io/tweakflow/reference.html#boolean)
* `ctx[:fields]` returns a [list](https://twineworks.github.io/tweakflow/reference.html#list) of fields

You can use the `let` construct in Tweakflow to make this clearer:

```tweakflow
library echopraxia {
  function evaluate: (string level, dict ctx) ->
    let {
      find_string: ctx[:find_string];
    }
    find_string("$.person.name") == "testing";
}
```

Using `find_object` or `find_list` returns the appropriate type of `dict` or `list` respectively.  

```tweakflow
library echopraxia {
  function evaluate: (string level, dict ctx) ->
    let {
      find_list: ctx[:find_list];
      interests: find_list("$.obj.interests");
    }
    interests[1] == "drink";
}
```

And you can use the Tweakflow [standard library](https://twineworks.github.io/tweakflow/modules/std.html) to allow for more advanced functionality, i.e.

```tweakflow
import * as std from "std";
alias std.strings as str;
library echopraxia {
  function evaluate: (string level, dict ctx) ->
    let {
      find_string: ctx[:find_string];
    }
    str.lower_case(find_string("$.person.name")) == "will";
}
```

The context method also has functionality to access "impure" methods such as the current instant, using `ctx[:now]`:

```tweakflow
import * as std from "std";
alias std.time as time;
library echopraxia {
  function evaluate: (string level, dict ctx) ->
    let { now: ctx[:now]; }
    time.unix_timestamp(now()) > 0;
}
```

If you want to look at intermediate results, you can use the `debug` function which will print out values to `System.out`:

```tweakflow
library echopraxia {
  function evaluate: (string level, dict ctx) ->
    let { now: ctx[:now]; }
    debug(now()) && true;
}
```

## Creating Script Conditions

The simplest way to handle a script is to pass it in directly as a string:

```java
import com.tersesystems.echopraxia.scripting.*;

StringBuilder b = new StringBuilder("");
b.append("library echopraxia {");
b.append("  function evaluate: (string level, dict ctx) ->");
b.append("    level == \"info\";");
b.append("}");
String scriptString = b.toString();  
Condition c = ScriptCondition.create(false, scriptString, Throwable::printStackTrace);
```

You can also use a `Path` for file based scripts:

```java
import com.tersesystems.echopraxia.scripting.*;

Path path = Paths.get("src/test/tweakflow/condition.tf");
Condition condition = ScriptCondition.create(false, path, Throwable::printStackTrace);

Logger<?> logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
```

Where `condition.tf` contains a tweakflow script, e.g.

```tweakflow
import * as std from "std";
alias std.strings as str;

library echopraxia {
  # level: the logging level
  # ctx: the logging context
  function evaluate: (string level, dict ctx) ->
    let {
      find_string: ctx[:find_string];
    }
    str.lower_case(find_string("$.person.name")) == "will";   
}
```

You also have the option to store scripts in a key-value store or in a database.  See the [sqlite condition store example](https://github.com/tersesystems/echopraxia-examples/tree/main/conditionstore) for details.

## User Defined Functions

You have the option of passing in user defined functions into the script, in addition to the built-in scripts.

```java
import com.tersesystems.echopraxia.scripting.*;
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.*;

class NowFunction {
  private UserFunctionValue nowFunction() {
    return ScriptFunction.builder()
            .supplier(() -> Values.make(Instant.now()))
            .result(Types.DATETIME)
            .build();
  }
  
  public final List<ValueMapEntry> userFunctions = 
      Collections.singletonList(new ValueMapEntry("now", Values.make(nowFunction())));
  
  public void logWithNow() {
    Path path = Paths.get("src/test/tweakflow/condition.tf");
    Condition condition = ScriptCondition.create(ctx -> userFunctions, false, path, Throwable::printStackTrace);
    Logger<?> logger = LoggerFactory.getLogger(getClass()).withCondition(condition);
  }
}
```

This will allow you to access `Instant.now()` whenever you call the function attached to `ctx[:now]`:

```tweakflow
import * as std from "std";
alias std.time as time;
library echopraxia {
  function evaluate: (string level, dict ctx) ->
     let { now: ctx[:now]; }
     time.unix_timestamp(now()) > 0;
  }
```

You can access the core logger and the underlying framework logger through the context:

```java
Function<LoggingContext, List<ValueMapEntry>> userFunctions = ctx -> {
   UserFunctionValue f = ScriptFunction.builder()
        .parameter(new FunctionParameter(0, "property_name", Types.STRING, Values.make("")))
        .function(propertyName -> {
            LogstashCoreLogger core = (LogstashCoreLogger) ctx.getCore();
            LoggerContext loggerContext = core.logger().getLoggerContext();
            String propertyValue = loggerContext.getProperty(propertyName.string());
            return Values.make(propertyValue);
        })
        .result(Types.STRING)
        .build())
   return Collections.singletonList(ValueMapEntry.make("logger_property", f); 
}
```

With this user defined function, you can set a logback property:

```dtd
<configuration>
    <property name="herp" value="derp" scope="context"/>
</configuration>
```

And then access the property using `ctx[:logger_property]("herp")`.

## Watched Scripts

You can change file based scripts while the application is running, if they are in a directory watched by `ScriptWatchService`.

To configure `ScriptWatchService`, pass it the directory that contains your script files:

```java
final Path watchedDir = Paths.get("/your/script/directory");
ScriptWatchService watchService = new ScriptWatchService(watchedDir);

Path filePath = watchedDir.resolve("myscript.tf");

Logger logger = LoggerFactory.getLogger();

final ScriptHandle watchedHandle = watchService.watchScript(filePath, 
        e -> logger.error("Script compilation error", e));
final Condition condition = ScriptCondition.create(watchedHandle);

logger.info(condition, "Statement only logs if condition is met!")
        
// After that, you can edit myscript.tf and the condition will 
// re-evaluate the script as needed automatically!
        
// You can delete the file, but doing so will log a warning from `ScriptWatchService`
// Recreating a deleted file will trigger an evaluation, same as modification.

// Note that the watch service creates a daemon thread to watch the directory.
// To free up the thread and stop watching, you should call close() as appropriate:
watchService.close();
```

Please see the [scripting example](https://github.com/tersesystems/echopraxia-examples/blob/main/script) for more details.
