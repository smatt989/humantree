package com.example.app.migrations

import com.example.app.AppGlobals
import AppGlobals.dbConfig.driver.api._
import com.example.app.db.Tables
object Migration6 extends Migration {

  val id = 6

  class IdentityLinks(tag: Tag) extends Table[(String, Int, String, String, Option[String])](tag, Some(InitDB.SCHEMA_NAME), "IDENTITY_LINKS") {
    def id = column[String]("IDENTITY_LINK_ID", O.PrimaryKey)
    def userId = column[Int]("USER_ID")
    def left = column[String]("LEFT_SIDE")
    def right = column[String]("RIGHT_SIDE")
    def name = column[Option[String]]("LINK_NAME")

    def * = (id, userId, left, right, name)

    def user = foreignKey("IDENTITY_LINK_TO_USERS_FK", userId, Tables.UserAccounts)(_.userAccountId)
  }

  class NodeAnnotations(tag: Tag) extends Table[(String, Int, String, String)](tag, Some(InitDB.SCHEMA_NAME), "NODE_ANNOTATIONS") {
    def id = column[String]("NODE_ANNOTATION_ID", O.PrimaryKey)
    def userId = column[Int]("USER_ID")
    def nodeName = column[String]("NODE_NAME")
    def annotation = column[String]("ANNOTATION")

    def * = (id, userId, nodeName, annotation)

    def user = foreignKey("NODE_ANNOTATION_TO_USERS_FK", userId, Tables.UserAccounts)(_.userAccountId)
  }


  val identityLinks = TableQuery[IdentityLinks]
  val nodeAnnotations = TableQuery[NodeAnnotations]

  def query = (identityLinks.schema ++ nodeAnnotations.schema).create
}