package com.example.app.migrations

import com.example.app.AppGlobals
import AppGlobals.dbConfig.driver.api._
import com.example.app.db.Tables
object Migration8 extends Migration {

  val id = 8

  class Interactions(tag: Tag) extends Table[(String, String, String, Long)](tag, Some(InitDB.SCHEMA_NAME), "INTERACTIONS") {
    def id = column[String]("INTERACTION_ID", O.PrimaryKey)
    def userEmail = column[String]("USER_EMAIL")
    def interactedWithEmail = column[String]("INTERACTED_WITH_EMAIL")
    def interactionDate = column[Long]("INTERACTION_DATE")

    def * = (id, userEmail, interactedWithEmail, interactionDate)
  }

  val interactions = TableQuery[Interactions]

  def query = (interactions.schema).create
}