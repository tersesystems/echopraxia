module echopraxia.logstash.main {
  requires com.tersesystems.echopraxia.api;
  requires com.tersesystems.echopraxia.logback;
  requires com.tersesystems.echopraxia.jackson;

  exports com.tersesystems.echopraxia.logstash;
}