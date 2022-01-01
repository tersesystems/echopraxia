# https://marketplace.visualstudio.com/items?itemName=twineworks.tweakflow
# https://twineworks.github.io/tweakflow/reference.html
# https://twineworks.github.io/tweakflow/modules/std.html#std

import * as std from "std";

# local alias for imported library
alias std.strings as str;

library echopraxia {

  # level: the logging level
  # fields: the dictionary of fields
  #
  function evaluate: (string level, dict fields) ->
    str.lower_case(fields[:person][:name]) == "will";
    
}
