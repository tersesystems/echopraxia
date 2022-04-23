library echopraxia {
  function evaluate: (string level, dict ctx) ->
    let {
      find_number: ctx[:find_number];
    }
    find_number("$.some_field") == 1;
}
