package com.example.app.models

import com.example.app.db.Tables.{IdentityLinksRow, Interactions, InteractionsRow}
import com.example.app.{AppGlobals, SlickUUIDObject}
import AppGlobals.dbConfig.driver.api._
import org.joda.time.DateTime

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration


object Interaction extends SlickUUIDObject[InteractionsRow, Interactions]{
  lazy val table = Interactions

  def idFromRow(a: _root_.com.example.app.db.Tables.InteractionsRow) =
    a.interactionId

  def updateId(a: _root_.com.example.app.db.Tables.InteractionsRow, id: String) =
    a.copy(interactionId = id)

  def idColumnFromTable(a: _root_.com.example.app.db.Tables.Interactions) =
    a.interactionId

  val monthMillis = 1000*60*60*24*30

  def coolingOffInteractions(emails: Seq[String], links: Seq[IdentityLinksRow]) = {
    val now = DateTime.now()
    val eighteenMonthsAgo = now.minusMonths(18).getMillis
    val twoMonthsAgo = now.minusMonths(2).getMillis
    val interactions = Await.result(db.run(table.filter(a => a.userEmail.inSet(emails) && a.interactionDate > eighteenMonthsAgo).result), Duration.Inf)

    val nameMap = IntroductionTree.renameMapFromLinksAndContext(emails, links)

    val renamedInteractions = interactions.map(i => i.copy(interactedWithEmail = nameMap.get(i.interactedWithEmail).getOrElse(i.interactedWithEmail)))

    //TODO: HOLY FUCK NEED BETTER USER IDENTITY ACROSS THE BOARD...
    val frequentInteractionsWithoutEmailsInTwoMonths = renamedInteractions.groupBy(a => (a.interactedWithEmail, a.interactionDate)).values.toSeq.map(_.head)
      .groupBy(_.interactedWithEmail)
      .filterNot(_._2.exists(_.interactionDate > twoMonthsAgo)) //no contact in the last 2 months
      .filter(_._2.size > 10) //at least 10 interactions
      .filter(a => a._2.map(_.interactionDate).max - a._2.map(_.interactionDate).min > (monthMillis * 2)) //interactions span at least 2 months

    frequentInteractionsWithoutEmailsInTwoMonths.mapValues(a => (a.map(_.interactionDate).max, a.size)).toSeq
      .map{case (email, (date, size)) => CoolingInteraction(email, size, date)}
  }
}


case class CoolingInteraction(email: String, interactions: Int, lastInteractionMillis: Long)