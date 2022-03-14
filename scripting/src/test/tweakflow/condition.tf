# https://marketplace.visualstudio.com/items?itemName=twineworks.tweakflow
# https://twineworks.github.io/tweakflow/reference.html

library echopraxia {

  doc 'Evaluates if correlation_id matches given value'
  function evaluate: (string level, function ctx) ->
    let {
      find_string: ctx("find_string");
    }
    find_string("correlation_id") == "match";
}
