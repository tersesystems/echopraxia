# https://marketplace.visualstudio.com/items?itemName=twineworks.tweakflow
# https://twineworks.github.io/tweakflow/reference.html

library echopraxia {

  # level: the logging level
  # fields: the dictionary of fields
  #
  doc 'Evaluates if exception matches'
  function evaluate: (string level, dict fields) ->
     fields[:exception][:message] == "testing";
}
