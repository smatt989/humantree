package com.example.app.models

import java.util.UUID

import com.example.app.SlickDbObject
import com.example.app.AppGlobals
import AppGlobals.dbConfig.driver.api._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import com.example.app.db.Tables._

object UserSession extends SlickDbObject[UserSessionsRow, UserSessions]{

  lazy val table = UserSessions

  def fromUser(userId: Int) =
    Await.result(db.run(table.filter(_.userId === userId).result).map(_.headOption), Duration.Inf)

  def findFromUserOrCreate(userId: Int) = {
    fromUser(userId).getOrElse(
      Await.result(create(UserSessionsRow(0, userId, UUID.randomUUID().toString)), Duration.Inf)
    )
  }

  def user(userSession: UserSessionsRow) =
    Await.result(User.byId(userSession.userId), Duration.Inf)

  def byHashString(hashString: String) =
    Await.result(db.run(table.filter(_.hashString === hashString).result).map(_.headOption), Duration.Inf)

  def idFromRow(a: _root_.com.example.app.db.Tables.UserSessionsRow) =
    a.userSessionId

  def updateId(a: _root_.com.example.app.db.Tables.UserSessionsRow, id: Int) =
    a.copy(userSessionId = id)

  def idColumnFromTable(a: _root_.com.example.app.db.Tables.UserSessions) =
    a.userSessionId
}