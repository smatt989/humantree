package com.example.app.migrations

import com.example.app.AppGlobals
import AppGlobals.dbConfig.driver.api._
import com.example.app.db.Tables
object Migration5 extends Migration {

  val id = 5

  class ShareableContexts(tag: Tag) extends Table[(String, Int, String, Long)](tag, Some(InitDB.SCHEMA_NAME), "SHAREABLE_CONTEXTS") {
    def id = column[String]("SHAREABLE_CONTEXT_ID", O.PrimaryKey)
    def userId = column[Int]("USER_ID")
    def root = column[String]("TREE_ROOT")
    def createdAtMillis = column[Long]("CREATED_AT_MILLIS")

    def * = (id, userId, root, createdAtMillis)

    def user = foreignKey("SHAREABLE_CONTEXT_TO_USERS_FK", userId, Tables.UserAccounts)(_.userAccountId)
  }


  val shareableContexts = TableQuery[ShareableContexts]

  def query = (shareableContexts.schema).create
}