# https://twineworks.github.io/tweakflow/reference.html

library echopraxia {

  # level: the logging level
  # fields: the dictionary of fields
  #
  doc 'Evaluates if person age is less than 13'
  function evaluate: (string level, dict fields) ->
     (fields[:person][:age] <= "13");
}
