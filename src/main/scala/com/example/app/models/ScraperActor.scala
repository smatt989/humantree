package com.example.app.models

import com.example.app.{AppGlobals, UpdatableUUIDObject}
import com.example.app.db.Tables.{ScraperActors, _}
import AppGlobals.dbConfig.driver.api._
import org.joda.time.DateTime

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object ScraperActor extends UpdatableUUIDObject[ScraperActorsRow, ScraperActors]{
  def updateQuery(a: _root_.com.example.app.db.Tables.ScraperActorsRow) = table.filter(t => idColumnFromTable(t) === idFromRow(a))
    .map(x => (x.startedMillis, x.finishedMillis, x.updatedMillis, x.terminatedMillis))
    .update(a.startedMillis, a.finishedMillis, a.updatedMillis, a.terminatedMillis)

  lazy val table = ScraperActors

  def idFromRow(a: _root_.com.example.app.db.Tables.ScraperActorsRow) =
    a.scraperActorId

  def updateId(a: _root_.com.example.app.db.Tables.ScraperActorsRow, id: String) =
    a.copy(scraperActorId = id)

  def idColumnFromTable(a: _root_.com.example.app.db.Tables.ScraperActors) =
    a.scraperActorId

  def byEmailAndUserId(email: String, userId: Int) = {
    db.run(table.filter(a => a.email === email && a.userId === userId && a.terminatedMillis.isEmpty).sortBy(_.startedMillis).result).map(_.lastOption)
  }

  def unscraped = {
    db.run(
      (for {
        accounts <- GmailAccessToken.table joinLeft table on((a, b) => a.email === b.email && a.userId === b.userId) if accounts._2.isEmpty
      } yield accounts).result
    ).map(_.map(_._1))
  }

  def unfinished() = {
    db.run(table.filter(_.finishedMillis.isEmpty).result)
  }

  def unfinishedNotUpdatedInOver(millis: Long) = {
    val now = DateTime.now().getMillis
    val cutoff = now - millis
    db.run(table.filter(a => a.finishedMillis.isEmpty && a.updatedMillis < cutoff && a.terminatedMillis.isEmpty).result)
  }

  def finishedLongAgo(millis: Long) = {
    val now = DateTime.now().getMillis
    val cutoff = now - millis
    val finishedRuns = Await.result(db.run(table.filter(a => a.finishedMillis.isDefined && a.terminatedMillis.isEmpty).result), Duration.Inf)
    val lastRuns = finishedRuns.filter(_.finishedMillis.isDefined).groupBy(a => (a.email, a.userId)).mapValues(_.sortBy(_.finishedMillis.get).last).values.toSeq
    lastRuns.filter(_.finishedMillis.get < cutoff)
  }

  def terminateUnfinishedForUserIdEmail(email: String, userId: Int) = {
    val now = DateTime.now().getMillis
    db.run(table.filter(a => a.terminatedMillis.isEmpty && a.email === email && a.userId === userId && a.finishedMillis.isEmpty).map(_.terminatedMillis).update(Some(now)))
  }
}

case class KillActorRequest(email: String, userId: Int)
