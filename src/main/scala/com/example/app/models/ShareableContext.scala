package com.example.app.models

import com.example.app.SlickUUIDObject
import com.example.app.db.Tables.{ShareableContexts, ShareableContextsRow}
import org.joda.time.DateTime

/**
  * Created by matt on 1/4/18.
  */
object ShareableContext extends SlickUUIDObject[ShareableContextsRow, ShareableContexts] {
  lazy val table = ShareableContexts

  def idFromRow(a: _root_.com.example.app.db.Tables.ShareableContextsRow) =
    a.shareableContextId

  def updateId(a: _root_.com.example.app.db.Tables.ShareableContextsRow, id: String) =
    a.copy(shareableContextId = id)

  def idColumnFromTable(a: _root_.com.example.app.db.Tables.ShareableContexts) =
    a.shareableContextId

  def createShareableContext(userId: Int, root: String) = {
    val toCreate = ShareableContextsRow(null, userId, root, DateTime.now().getMillis)
    create(toCreate)
  }
}
