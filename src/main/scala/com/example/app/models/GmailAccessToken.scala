package com.example.app.models


import com.example.app.{AppGlobals, SlickUUIDObject, UpdatableUUIDObject}
import com.example.app.db.Tables.IntroductionsRow
import com.example.app.db.Tables._
import AppGlobals.dbConfig.driver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object GmailAccessToken extends UpdatableUUIDObject[GmailAccessTokensRow, GmailAccessTokens]{

  def updateQuery(a: _root_.com.example.app.db.Tables.GmailAccessTokensRow) = table.filter(t => idColumnFromTable(t) === idFromRow(a))
    .map(x => x.accessToken)
    .update(a.accessToken)

  lazy val table = GmailAccessTokens

  def idFromRow(a: _root_.com.example.app.db.Tables.GmailAccessTokensRow) =
    a.gmailAccesTokenId

  def updateId(a: _root_.com.example.app.db.Tables.GmailAccessTokensRow, id: String) =
    a.copy(gmailAccesTokenId = id)

  def idColumnFromTable(a: _root_.com.example.app.db.Tables.GmailAccessTokens) =
    a.gmailAccesTokenId

  def updateUserGmailAccessToken(accessToken: String, email: String, userId: Int) = {
    val previous = Await.result(fetchUserGmailAccessToken(userId, email), Duration.Inf)

    if(previous.isDefined)
      updateOne(previous.get.copy(accessToken = accessToken))
    else
      create(GmailAccessTokensRow(null, userId, email, accessToken))
  }

  def fetchUserGmailAccessToken(userId: Int, email: String) = {
    db.run(table.filter(a => a.userId === userId && a.email === email).result).map(_.headOption)
  }

  def fetchAllForUser(userId: Int) = {
    db.run(table.filter(_.userId === userId).result)
  }

  def statusByEmailAndUserId(userId: Int, email: String) = {
    db.run(
      (for {
        account <- table.filter(a => a.userId === userId && a.email === email)
        progress <- GmailScrapeProgress.table if progress.gmailAccessTokenId === account.gmailAccesTokenId
      } yield (progress)).result
    ).map(_.headOption)
  }

  def allStatusesByUserId(userId: Int) = {
    db.run(
      (for {
        accounts <- table.filter(_.userId === userId)
        progresses <- GmailScrapeProgress.table if progresses.gmailAccessTokenId === accounts.gmailAccesTokenId
      } yield (accounts, progresses)).result
    )
  }

}

case class ConnectedGmailAccount(email: String, status: String, totalThreads: Option[Int], threadsProcessed: Int, lastPullMillis: Long)