# https://marketplace.visualstudio.com/items?itemName=twineworks.tweakflow
# https://twineworks.github.io/tweakflow/reference.html

library echopraxia {

  # level: the logging level
  # fields: the dictionary of fields
  #
  doc 'Evaluates if correlation_id matches given value (you can change this at runtime)'
  function evaluate: (string level, dict fields) ->
     fields[:correlation_id] == "match";
}
