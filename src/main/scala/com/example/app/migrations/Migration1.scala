package com.example.app.migrations

import com.example.app.AppGlobals
import AppGlobals.dbConfig.driver.api._
object Migration1 extends Migration {

  val id = 1

  class Users(tag: Tag) extends Table[(Int, String, String)](tag, Some(InitDB.SCHEMA_NAME), "USER_ACCOUNTS") {
    def id = column[Int]("USER_ACCOUNT_ID", O.PrimaryKey, O.AutoInc)
    def email = column[String]("EMAIL")
    def hashedPassword = column[String]("HASHED_PASSWORD")

    def * = (id, email, hashedPassword)
  }

  class DeviceTokens(tag: Tag) extends Table[(Int, Int, Option[String])](tag, Some(InitDB.SCHEMA_NAME), "DEVICE_TOKENS") {
    def id = column[Int]("DEVICE_TOKEN_ID", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("USER_ID")
    def deviceToken = column[Option[String]]("DEVICE_TOKEN")

    def * = (id, userId, deviceToken)

    def user = foreignKey("DEVICE_TOKENS_TO_USER_FK", userId, users)(_.id)
  }

  class UserSessions(tag: Tag) extends Table[(Int, Int, String)](tag, Some(InitDB.SCHEMA_NAME), "USER_SESSIONS") {
    def id = column[Int]("USER_SESSION_ID", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("USER_ID")
    def hashString = column[String]("HASH_STRING")

    def * = (id, userId, hashString)

    def user = foreignKey("USER_SESSIONS_TO_USER_FK", userId, users)(_.id)
  }

  class UserConnections(tag: Tag) extends Table[(Int, Int, Int)](tag, Some(InitDB.SCHEMA_NAME), "USER_CONNECTIONS") {
    def id = column[Int]("USER_CONNECTION_ID", O.PrimaryKey, O.AutoInc)
    def senderUserId = column[Int]("SENDER_USER_ID")
    def receiverUserId = column[Int]("RECEIVER_USER_ID")

    def * = (id, senderUserId, receiverUserId)

    def sender = foreignKey("USER_CONNECTIONS_SENDER_TO_USERS_FK", senderUserId, users)(_.id)
    def receiver = foreignKey("USER_CONNECTIONS_RECEIVER_TO_USERS_FK", receiverUserId, users)(_.id)
  }

  val users = TableQuery[Users]
  val deviceTokens = TableQuery[DeviceTokens]
  val userSessions = TableQuery[UserSessions]

  val userConnections = TableQuery[UserConnections]

  def query = (users.schema ++ deviceTokens.schema ++ userSessions.schema ++ userConnections.schema).create
}