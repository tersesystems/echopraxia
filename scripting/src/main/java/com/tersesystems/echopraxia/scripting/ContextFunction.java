package com.tersesystems.echopraxia.scripting;

import com.tersesystems.echopraxia.LoggingContext;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class ContextFunction implements Arity1UserFunction {
  private final LoggingContext ctx;

  public ContextFunction(LoggingContext ctx) {
    this.ctx = ctx;
  }

  @Override
  public Value call(UserCallContext context, Value functionName) {
    final String string = functionName.string();
    // XXX Strip the "find" bits?
    if ("findNumber".equals(string)) {
      return userFunctionValue(optionalFunction(ctx::findNumber));
    }
    if ("findString".equals(string)) {
      return userFunctionValue(optionalFunction(ctx::findString));
    }
    if ("findBoolean".equals(string)) {
      return userFunctionValue(optionalFunction(ctx::findBoolean));
    }
    if ("findObject".equals(string)) {
      return userFunctionValue(optionalFunction(ctx::findObject));
    }
    if ("findList".equals(string)) {
      return userFunctionValue(listFunction(ctx::findList));
    }
    if ("findNull".equals(string)) {
      return userFunctionValue(nullFunction(ctx::findNull));
    }

    throw new LangException(LangError.ILLEGAL_ARGUMENT, "Unknown function name");
  }

  @NotNull
  private Value userFunctionValue(Arity1UserFunction userFunction) {
    return Values.make(
        new UserFunctionValue(
            new FunctionSignature(
                Collections.singletonList(
                    new FunctionParameter(0, "jsonType", Types.STRING, Values.NIL)),
                Types.ANY),
            userFunction));
  }

  Arity1UserFunction optionalFunction(Function<String, Optional<?>> contextFunction) {
    return (context, pathValue) -> {
      final String path = pathValue.string();
      final Optional<?> opt = contextFunction.apply(path);
      if (opt.isPresent()) {
        return Values.make(opt.get());
      }
      return Values.NIL;
    };
  }

  private Arity1UserFunction listFunction(Function<String, List<?>> listFunction) {
    return (context, pathValue) -> {
      final String path = pathValue.string();
      final List<?> list = listFunction.apply(path);
      return Values.make(list);
    };
  }

  private Arity1UserFunction nullFunction(Function<String, Boolean> nullFunction) {
    return (context, pathValue) -> {
      final String path = pathValue.string();
      final Boolean result = nullFunction.apply(path);
      return Values.make(result);
    };
  }
}
