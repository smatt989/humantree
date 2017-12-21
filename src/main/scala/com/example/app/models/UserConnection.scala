package com.example.app.models

import com.example.app.SlickDbObject
import com.example.app.AppGlobals
import AppGlobals.dbConfig.driver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.example.app.db.Tables._

case class UserConnection(id: Int = 0, senderUserId: Int, receiverUserId: Int)

case class ConnectionRequestJson(addUserId: Int) {
  def newConnection(senderUserId: Int) = {
    UserConnectionsRow(
      userConnectionId = 0,
      senderUserId = senderUserId,
      receiverUserId = addUserId
    )
  }
}

case class ConnectionDeleteJson(removeUserId: Int)


object UserConnection extends SlickDbObject[UserConnectionsRow, UserConnections]{

  lazy val table = UserConnections

  def safeSave(connection: UserConnectionsRow) = {
    findConnection(connection.senderUserId, connection.receiverUserId).flatMap(optionalConnection => {
      if (optionalConnection.isEmpty) {
        create(connection)
      } else {
        Future.apply(optionalConnection.get)
      }
    })
  }

  def findConnection(senderUserId: Int, receiverUserId: Int) =
    db.run(table.filter(a => a.senderUserId === senderUserId && a.receiverUserId === receiverUserId).result).map(_.headOption)

  def getBySenderId(senderId: Int) =
    db.run(table.filter(_.senderUserId === senderId).result)

  def getReceiversBySenderId(senderId: Int) =
    db.run(
      (for {
        (cs, us) <- table.filter(_.senderUserId === senderId) join User.table on (_.receiverUserId === _.userAccountId)
      } yield us).result
    )

  def getByReceiverId(receiverId: Int) =
    db.run(table.filter(_.receiverUserId === receiverId).result)

  def getSendersByReceiverId(receiverId: Int) =
    db.run(
      (for {
        (cs, us) <- table.filter(_.receiverUserId === receiverId) join User.table on (_.senderUserId === _.userAccountId)
      } yield us).result)

  def removeBySenderReceiverPair(senderUserId: Int, receiverUserId: Int) =
    db.run(table.filter(a => a.senderUserId === senderUserId && a.receiverUserId === receiverUserId).delete)

  def idFromRow(a: _root_.com.example.app.db.Tables.UserConnectionsRow) =
    a.userConnectionId

  def updateId(a: _root_.com.example.app.db.Tables.UserConnectionsRow, id: Int) =
    a.copy(userConnectionId = id)

  def idColumnFromTable(a: _root_.com.example.app.db.Tables.UserConnections) =
    a.userConnectionId
}