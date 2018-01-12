package com.example.app.models

import com.example.app.{AppGlobals, SlickUUIDObject}
import com.example.app.db.Tables.{IdentityLinks, IdentityLinksRow}
import AppGlobals.dbConfig.driver.api._

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by matt on 1/11/18.
  */
object IdentityLink extends SlickUUIDObject[IdentityLinksRow, IdentityLinks] {
  lazy val table = IdentityLinks

  def idFromRow(a: _root_.com.example.app.db.Tables.IdentityLinksRow) =
    a.identityLinkId

  def updateId(a: _root_.com.example.app.db.Tables.IdentityLinksRow, id: String) =
    a.copy(identityLinkId = id)

  def idColumnFromTable(a: _root_.com.example.app.db.Tables.IdentityLinks) =
    a.identityLinkId

  def byUserId(userId: Int) = {
    db.run(table.filter(_.userId === userId).result)
  }

  def deleteBy(userId: Int, left: String, right: String) = {
    db.run(table.filter(a => a.userId === userId && ((a.leftSide === left && a.rightSide === right) || (a.leftSide === right && a.rightSide === left))).delete)
  }

  def nameMapFromLinks(links: Seq[IdentityLinksRow]) = {
    val families = replacementFamilies(links)
    families.flatMap(family => {
      val target = family.head.leftSide
      family.map(a => a.leftSide -> target).toMap ++ family.map(a => a.rightSide -> target)
    }).toMap
  }

  @tailrec
  def findAFamily(searchingLinks: Seq[IdentityLinksRow], remainingLinks: Seq[IdentityLinksRow]): Seq[IdentityLinksRow] = {
    val toFind = (searchingLinks.map(_.leftSide) ++ searchingLinks.map(_.rightSide)).distinct

    val (moreLinks, remainders) = remainingLinks.partition(a => toFind.contains(a.leftSide) || toFind.contains(a.rightSide))

    if(moreLinks.size > 0){
      findAFamily(moreLinks ++ searchingLinks, remainders)
    } else {
      searchingLinks
    }
  }

  @tailrec
  def replacementFamilies(links: Seq[IdentityLinksRow], families: Seq[Seq[IdentityLinksRow]] = Nil): Seq[Seq[IdentityLinksRow]] = {
    if(links.size > 0){
      val thisLink = links.head

      val aFamily = findAFamily(Seq(thisLink), links.tail)

      val familyIds = aFamily.map(_.identityLinkId)

      val nextLinks = links.filterNot(a => familyIds.contains(a.identityLinkId))

      replacementFamilies(nextLinks, families :+ aFamily)

    } else {
      families
    }
  }


}
