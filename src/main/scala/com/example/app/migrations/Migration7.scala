package com.example.app.migrations

import com.example.app.AppGlobals
import AppGlobals.dbConfig.driver.api._
import com.example.app.db.Tables
object Migration7 extends Migration {

  val id = 7

  class ScraperActors(tag: Tag) extends Table[(String, Int, String, Long, Option[Long], Long, Option[Long])](tag, Some(InitDB.SCHEMA_NAME), "SCRAPER_ACTORS") {
    def id = column[String]("SCRAPER_ACTOR_ID", O.PrimaryKey)
    def userId = column[Int]("USER_ID")
    def email = column[String]("EMAIL")
    def startedMillis = column[Long]("STARTED_MILLIS")
    def finishedMillis = column[Option[Long]]("FINISHED_MILLIS")
    def updatedMillis = column[Long]("UPDATED_MILLIS")
    def terminatedMillis = column[Option[Long]]("TERMINATED_MILLIS")

    def * = (id, userId, email, startedMillis, finishedMillis, updatedMillis, terminatedMillis)

    def user = foreignKey("SCRAPER_ACTOR_TO_USERS_FK", userId, Tables.UserAccounts)(_.userAccountId)
  }

  val scraperActors = TableQuery[ScraperActors]

  def query = (scraperActors.schema).create
}