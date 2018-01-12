package com.example.app.models

import com.example.app.db.Tables.{IdentityLinksRow, IntroductionsRow}
import org.joda.time.DateTime
import org.json4s.JsonAST.{JArray, JObject, JString}

import scala.annotation.tailrec
import scala.concurrent.Await
import scala.concurrent.duration.Duration

case class IntroductionTree(name: String, children: Seq[IntroductionTree] = Nil)


object IntroductionTree {

  //TODO: HOW DOES CONTEXT WORK?  EMAIL?  USER ID?  USER ID --> EMAILS?
  def introsByEmails(emails: Seq[String]) = {
    val allIntros = Await.result(Introduction.getAllForReceiverEmails(emails), Duration.Inf)

    val distinct = allIntros.groupBy(_.introPersonEmail).mapValues(_.sortBy(_.introTimeMillis)).values.toSeq.map(_.head)

    //val removeSelf = distinct.filterNot(a => emails.contains(a.introPersonEmail))

    distinct.map(d => {
      if(emails.contains(d.senderPersonEmail))
        d.copy(senderPersonEmail = emails.head)
      else
        d
    })
  }

  def treeByRootAndContext(root: String, emailContexts: Seq[String], links: Seq[IdentityLinksRow] = Nil) = {
    val intros = introsByEmails(emailContexts)

    val renameMap = IdentityLink.nameMapFromLinks(links)

    val renamedIntros = intros.map(intro => intro.copy(senderPersonEmail = renameMap.getOrElse(intro.senderPersonEmail, intro.senderPersonEmail), introPersonEmail = renameMap.getOrElse(intro.introPersonEmail, intro.introPersonEmail)))
      .filter(a => a.introPersonEmail != a.senderPersonEmail && a.introPersonEmail != root)

    val senderToIntros = renamedIntros.groupBy(_.senderPersonEmail).mapValues(_.map(_.introPersonEmail))

    Seq(tryingThisWay(senderToIntros, IntroductionTree(root)))
  }




  //REPLACE AN ELEMENT AT AN INDEX IN A LIST
  def replaceElementInList[A](list: Seq[A], elementToSwapIn: A, atIndex: Int) = {
    (list.take(atIndex) :+ elementToSwapIn) ++ list.drop(atIndex + 1)
  }

  //CAN PROBABLY DO THIS EVEN FASTER BY TRACKING CHILDREN INSTEAD OF "SEEN" AND USING THE .FIND ON CURRENT CHILDREN?
  @tailrec
  def tryingThisWay(mapsOfIntros: Map[String, Seq[String]], currentTree: IntroductionTree, lineage: Seq[IntroductionTree] = Nil, seen: Set[String] = Set()): IntroductionTree = {
      if (seen.contains(currentTree.name)) {
        val children = currentTree.children
        //BET WE CAN DO BETTER THAN THIS:
        val unseenChild = children.find(c => !seen.contains(c.name))
        if (unseenChild.isDefined) {
          val nextNode = unseenChild.get

          tryingThisWay(mapsOfIntros, nextNode, lineage :+ currentTree, seen)
        } else {
          if (lineage.size > 0) {
            val lastLineage = lineage.last

            val childIndex = lastLineage.children.indexWhere(_.name == currentTree.name)

            val newChildren = replaceElementInList(lastLineage.children, currentTree, childIndex)

            //val updatedLastLineage = lastLineage.copy(children = lastLineage.children.filterNot(_.name == currentTree.name) :+ currentTree)

            val updatedLastLineage = lastLineage.copy(children = newChildren)

            tryingThisWay(mapsOfIntros, updatedLastLineage, lineage.dropRight(1), seen)
          } else {
            currentTree
          }
        }
      } else {

        val children = mapsOfIntros.get(currentTree.name).getOrElse(Nil).filterNot(a => seen.contains(a)).map(c => IntroductionTree(c))
        val newSeen = seen ++ Set(currentTree.name)
        tryingThisWay(mapsOfIntros, currentTree.copy(children = children), lineage, newSeen)
      }

  }

}
