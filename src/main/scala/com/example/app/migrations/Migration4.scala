package com.example.app.migrations

import com.example.app.AppGlobals
import AppGlobals.dbConfig.driver.api._
import com.example.app.db.Tables
object Migration4 extends Migration {

  val id = 4

  class GmailScrapeProgresses(tag: Tag) extends Table[(String, String, Option[Int], Int, String, Long)](tag, Some(InitDB.SCHEMA_NAME), "GMAIL_SCRAPE_PROGRESSES") {
    def id = column[String]("GMAIL_SCRAPE_PROGRESS_ID", O.PrimaryKey)
    def gmailAccessTokenId = column[String]("GMAIL_ACCESS_TOKEN_ID")
    def totalThreads = column[Option[Int]]("TOTAL_THREADS")
    def threadsProcessed = column[Int]("THREADS_PROCESSED")
    def status = column[String]("STATUS")
    def lastPulledMillis = column[Long]("LAST_PULLED_MILLIS")

    def * = (id, gmailAccessTokenId, totalThreads, threadsProcessed, status, lastPulledMillis)

    def gmailAccessToken = foreignKey("GMAIL_SCRAPE_PROGRESS_TO_GMAIL_ACCESS_TOKEN_FK", gmailAccessTokenId, Tables.GmailAccessTokens)(_.gmailAccesTokenId)
  }


  val gmailScrapeProgresses = TableQuery[GmailScrapeProgresses]

  def query = (gmailScrapeProgresses.schema).create
}