package com.example.app.migrations

import com.example.app.AppGlobals
import AppGlobals.dbConfig.driver.api._
import com.example.app.db.Tables
object Migration3 extends Migration {

  val id = 3

  class GmailAccessTokens(tag: Tag) extends Table[(String, Int, String, String)](tag, Some(InitDB.SCHEMA_NAME), "GMAIL_ACCESS_TOKENS") {
    def id = column[String]("GMAIL_ACCES_TOKEN_ID", O.PrimaryKey)
    def userId = column[Int]("USER_ID")
    def email = column[String]("EMAIL")
    def accessToken = column[String]("ACCESS_TOKEN")

    def * = (id, userId, email, accessToken)

    def user = foreignKey("GMAIL_ACCES_TOKEN_TO_USERS_FK", userId, Tables.UserAccounts)(_.userAccountId)
  }

  val gmailAccessTokens = TableQuery[GmailAccessTokens]

  def query = (gmailAccessTokens.schema).create
}