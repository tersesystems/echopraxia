package com.tersesystems.echopraxia.api;

import java.util.List;

class ValueFormatter {

  static void formatToBuffer(StringBuilder b, Value<?> v) {
    final Object raw = v.raw();
    if (raw == null) { // if null value or a raw value was set to null, keep going.
      b.append("null");
    } else if (v.type() == Value.Type.OBJECT) {
      // render an object with curly braces to distinguish from array.
      final List<Field> fieldList = ((Value.ObjectValue) v).raw();
      b.append("{");
      for (int i = 0; i < fieldList.size(); i++) {
        DefaultField field = (DefaultField) fieldList.get(i);
        field.formatToBuffer(b);
        if (i < fieldList.size() - 1) {
          b.append(", ");
        }
      }
      b.append("}");
    } else {
      b.append(raw);
    }
  }
}
