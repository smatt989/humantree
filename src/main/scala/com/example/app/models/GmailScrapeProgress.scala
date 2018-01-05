package com.example.app.models

import com.example.app.UpdatableUUIDObject
import com.example.app.db.Tables.{GmailScrapeProgresses, GmailScrapeProgressesRow}
import com.example.app.AppGlobals
import AppGlobals.dbConfig.driver.api._
import org.joda.time.DateTime

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

object GmailScrapeProgress extends UpdatableUUIDObject[GmailScrapeProgressesRow, GmailScrapeProgresses]{

  lazy val table = GmailScrapeProgresses

  def updateQuery(a: _root_.com.example.app.db.Tables.GmailScrapeProgressesRow) = table.filter(t => idColumnFromTable(t) === idFromRow(a))
    .map(x => (x.totalThreads, x.threadsProcessed, x.status, x.lastPulledMillis))
    .update(a.totalThreads, a.threadsProcessed, a.status, a.lastPulledMillis)

  def idFromRow(a: _root_.com.example.app.db.Tables.GmailScrapeProgressesRow) =
    a.gmailScrapeProgressId

  def updateId(a: _root_.com.example.app.db.Tables.GmailScrapeProgressesRow, id: String) =
    a.copy(gmailScrapeProgressId = id)

  def idColumnFromTable(a: _root_.com.example.app.db.Tables.GmailScrapeProgresses) =
    a.gmailScrapeProgressId

  val STATUS_SCRAPING = "running"
  val STATUS_STOPPED = "stopped"

  def initializeOrDoNothing(gmailConnectionId: String) = {
    val preexisting = Await.result(getByGmailConnectionId(gmailConnectionId), Duration.Inf)
    if(preexisting.isEmpty)
      Await.result(create(GmailScrapeProgressesRow(null, gmailConnectionId, None, 0, STATUS_STOPPED, DateTime.now().getMillis)), Duration.Inf)
    else
      preexisting.get
  }

  //TODO: MAYBE INCLUDE AN ACTOR TO DOUBLE CHECK THIS STUFF

  def updateStatus(progressId: String, status: String) = {
    val preexisting = Await.result(byId(progressId), Duration.Inf)

    updateOne(preexisting.copy(status = status))
  }

  def updateTotalThreads(progressId: String, totalThreads: Option[Int] = None) = {
    val preexisting = Await.result(byId(progressId), Duration.Inf)

    updateOne(preexisting.copy(totalThreads = totalThreads, lastPulledMillis = DateTime.now().getMillis))
  }

  def updateThreadCount(progressId: String, threads: Int) = {
    val preexisting = Await.result(byId(progressId), Duration.Inf)

    updateOne(preexisting.copy(threadsProcessed = threads))
  }

  def getByGmailConnectionId(id: String) =
    db.run(table.filter(_.gmailAccessTokenId === id).result).map(_.headOption)

}
