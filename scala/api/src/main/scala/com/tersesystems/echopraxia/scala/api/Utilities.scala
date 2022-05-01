package com.tersesystems.echopraxia.scala.api

import com.tersesystems.echopraxia.api.{Field, Value}

import java.util
import java.util.function.{Function, Supplier}
import scala.jdk.CollectionConverters._
import scala.util.control.NonFatal

object Utilities {

  def getNewInstance[T](newBuilderClass: Class[T]): T = {
    try {
      newBuilderClass.getDeclaredConstructor().newInstance()
    } catch {
      case NonFatal(e) =>
        throw new IllegalStateException(e)
    }
  }

  val getThreadContextFunction
      : Function[Supplier[util.Map[String, String]], Supplier[util.List[Field]]] =
    new Function[Supplier[util.Map[String, String]], Supplier[util.List[Field]]] {
      override def apply(
          mapSupplier: Supplier[util.Map[String, String]]
      ): Supplier[util.List[Field]] = {
        new Supplier[util.List[Field]]() {
          def buildFields(contextMap: util.Map[String, String]): util.List[Field] = {
            val list = new util.ArrayList[Field]();
            for (entry <- contextMap.entrySet().iterator.asScala) {
              list.add(Field.keyValue(entry.getKey, Value.string(entry.getValue)))
            }
            list
          }

          override def get(): util.List[Field] = buildFields(mapSupplier.get)
        }
      }
    }
}
