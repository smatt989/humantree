package com.example.app.models

import com.example.app.db.Tables.{NodeAnnotationsRow, NodeAnnotations}
import com.example.app.{AppGlobals, SlickUUIDObject}
import AppGlobals.dbConfig.driver.api._
/**
  * Created by matt on 1/12/18.
  */
object NodeAnnotation extends SlickUUIDObject[NodeAnnotationsRow, NodeAnnotations]{
  lazy val table = NodeAnnotations

  def idFromRow(a: _root_.com.example.app.db.Tables.NodeAnnotationsRow) =
    a.nodeAnnotationId

  def updateId(a: _root_.com.example.app.db.Tables.NodeAnnotationsRow, id: String) =
    a.copy(nodeAnnotationId = id)

  def idColumnFromTable(a: _root_.com.example.app.db.Tables.NodeAnnotations) =
    a.nodeAnnotationId

  def byAnnotation(userId: Int, annotation: String) = {
    db.run(table.filter(a => a.userId === userId && a.annotation === annotation).result)
  }

  def deleteBy(userId: Int, node: String, annotation: String) = {
    db.run(table.filter(a => a.userId === userId && a.nodeName === node && a.annotation === annotation).delete)
  }
}
