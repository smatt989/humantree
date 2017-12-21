package com.example.app.models

import com.example.app.UpdatableDBObject
import com.example.app.AppGlobals
import AppGlobals.dbConfig.driver.api._
import com.example.app.db.Tables._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object DeviceToken extends UpdatableDBObject[DeviceTokensRow, DeviceTokens] {
  def updateQuery(a: DeviceTokensRow) = table.filter(t => idColumnFromTable(t) === idFromRow(a))
    .map(x => x.deviceToken)
    .update(a.deviceToken)

  lazy val table = DeviceTokens

  def getByUserIds(ids: Seq[Int]): Map[Int, Option[String]] = {
    val tokens = Await.result(db.run(table.filter(_.userId inSet ids).result), Duration.Inf)
    val tokenMap = tokens.groupBy(_.userId).mapValues(_.sortBy(_.deviceTokenId).last.deviceToken)
    ids.map(id => id -> tokenMap.getOrElse(id, None)).toMap
  }

  def idFromRow(a: _root_.com.example.app.db.Tables.DeviceTokensRow) =
    a.deviceTokenId

  def updateId(a: _root_.com.example.app.db.Tables.DeviceTokensRow, id: Int) =
    a.copy(deviceTokenId = id)

  def idColumnFromTable(a: _root_.com.example.app.db.Tables.DeviceTokens) =
    a.deviceTokenId
}