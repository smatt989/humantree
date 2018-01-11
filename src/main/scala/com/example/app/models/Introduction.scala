package com.example.app.models

import com.example.app.{AppGlobals, SlickUUIDObject}
import com.example.app.db.Tables.IntroductionsRow
import com.example.app.db.Tables._
import AppGlobals.dbConfig.driver.api._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by matt on 12/29/17.
  */
object Introduction extends SlickUUIDObject[IntroductionsRow, Introductions]{
  lazy val table = Introductions

  def idFromRow(a: _root_.com.example.app.db.Tables.IntroductionsRow) =
    a.introductionId

  def updateId(a: _root_.com.example.app.db.Tables.IntroductionsRow, id: String) =
    a.copy(introductionId = id)

  def idColumnFromTable(a: Introductions) =
    a.introductionId

  def introductionsByReceiver(receiver: String) =
    db.run(table.filter(_.receiverPersonEmail === receiver).result)

  def getBranches(roots: Seq[String], contextOf: String) = {
    db.run(
      table.filter(a => a.receiverPersonEmail === contextOf && a.senderPersonEmail.inSet(roots)).result
    )
  }

  def getAllForReceiverEmails(emails: Seq[String]) = {
    db.run(table.filter(a => a.receiverPersonEmail inSet emails).result)
  }

  def removeAllForReceiverEmail(email: String) = {
    db.run(table.filter(_.receiverPersonEmail === email).delete)
  }

}
