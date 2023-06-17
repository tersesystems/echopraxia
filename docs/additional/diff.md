# Diff Field Builder

The "diff" field builder is useful for debugging a change in state in complex objects because it can compare "before" and "after" objects and only render the changes between the two values, using [RFC 6902](https://datatracker.ietf.org/doc/html/rfc6902) format with [zjsonpatch](https://github.com/flipkart-incubator/zjsonpatch/).

To add the diff field builder, add the `diff` module:

```gradle
implementation "com.tersesystems.echopraxia:diff:<VERSION>"
```

And implement `DiffFieldBuilder`:

```java
import com.tersesystems.echopraxia.diff.DiffFieldBuilder;

class PersonFieldBuilder implements DiffFieldBuilder {
  // ...
  public FieldBuilderResult diff(String name, Person before, Person after) {
    return diff(name, personValue(before), personValue(after), Field.class);
  }
}
```

You can then compare a change in an object by rendering the diff:

```java
Logger<PersonFieldBuilder> logger = LoggerFactory.getLogger().withFieldBuilder(PersonFieldBuilder.instance);

Person before = new Person("Jim", 1);
Person after = before.withName("Will");

logger.info("{}", fb -> fb.diff("personDiff", before, after));
```

The diff field builder depends on Jackson 2.13, and will use a static object mapper by default, which you can override using the `_objectMapper` method.
