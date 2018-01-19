package com.example.app.models

import com.example.app.db.Tables.{IdentityLinksRow, IntroductionsRow}
import org.joda.time.DateTime
import org.json4s.JsonAST.{JArray, JObject, JString}

import scala.annotation.tailrec
import scala.concurrent.Await
import scala.concurrent.duration.Duration

case class IntroductionTree(name: String, dateMillis: Long, children: Seq[IntroductionTree] = Nil)


object IntroductionTree {

  def introsByEmails(emails: Seq[String]) = {
    val allIntros = Await.result(Introduction.getAllForReceiverEmails(emails), Duration.Inf)

    allIntros.groupBy(_.introPersonEmail).mapValues(_.sortBy(_.introTimeMillis)).values.toSeq.map(_.head)

  }

  def treeByRootAndContext(root: String, emailContexts: Seq[String], links: Seq[IdentityLinksRow] = Nil) = {

    val intros = Await.result(Introduction.getAllForReceiverEmails(emailContexts), Duration.Inf)

    treeFromIntroductions(root, emailContexts, intros, links)
  }

  def treeFromIntroductions(root: String, emailContexts: Seq[String], intros: Seq[IntroductionsRow], links: Seq[IdentityLinksRow] = Nil) = {

    val renamedIntros = renameIntros(intros, emailContexts, links)

    val uniqueIntros = distinctIntros(renamedIntros)

    val senderToIntros = uniqueIntros.groupBy(_.senderPersonEmail)

    Seq(tryingThisWay(senderToIntros, IntroductionTree(root, DateTime.now().getMillis)))
  }

  def renameMapFromLinksAndContext(context: Seq[String], links: Seq[IdentityLinksRow]) = {
    val tempLinks = if(context.size > 0)
      context.tail.map(e => IdentityLinksRow(null, 0, context.head, e))
    else
      Nil

    IdentityLink.nameMapFromLinks(tempLinks ++ links)
  }

  def renameIntros(intros: Seq[IntroductionsRow], emails: Seq[String], links: Seq[IdentityLinksRow]) = {
    val renameMap = renameMapFromLinksAndContext(emails, links)

    intros.map(intro => intro.copy(senderPersonEmail = renameMap.getOrElse(intro.senderPersonEmail, intro.senderPersonEmail), introPersonEmail = renameMap.getOrElse(intro.introPersonEmail, intro.introPersonEmail)))
      .filter(a => a.introPersonEmail != a.senderPersonEmail && !emails.contains(a.introPersonEmail))
  }

  def distinctIntros(intros: Seq[IntroductionsRow]) = {
    intros.groupBy(_.introPersonEmail).mapValues(_.sortBy(_.introTimeMillis)).values.toSeq.map(_.head)
  }

  //REPLACE AN ELEMENT AT AN INDEX IN A LIST
  def replaceElementInList[A](list: Seq[A], elementToSwapIn: A, atIndex: Int) = {
    (list.take(atIndex) :+ elementToSwapIn) ++ list.drop(atIndex + 1)
  }

  //CAN PROBABLY DO THIS EVEN FASTER BY TRACKING CHILDREN INSTEAD OF "SEEN" AND USING THE .FIND ON CURRENT CHILDREN?
  @tailrec
  def tryingThisWay(mapsOfIntros: Map[String, Seq[IntroductionsRow]], currentTree: IntroductionTree, lineage: Seq[IntroductionTree] = Nil, seen: Set[String] = Set()): IntroductionTree = {
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

        val children = mapsOfIntros.get(currentTree.name).getOrElse(Nil).filterNot(a => seen.contains(a.introPersonEmail)).map(c => IntroductionTree(c.introPersonEmail, c.introTimeMillis))
        val newSeen = seen ++ Set(currentTree.name)
        tryingThisWay(mapsOfIntros, currentTree.copy(children = children), lineage, newSeen)
      }

  }

  @tailrec
  def bloop(tree: IntroductionTree, since: Long, lineage: Seq[IntroductionTree] = Nil, seen: Set[String] = Set()): IntroductionTree = {
    if(seen.contains(tree.name)) {
      val unseenChild = tree.children.find(c => !seen.contains(c.name))
      if(unseenChild.isDefined){
        val nextNode = unseenChild.get
        bloop(nextNode, since, lineage :+ tree, seen)
      } else {
        val newChildren = tree.children.filter(child => child.children.size > 0 || child.dateMillis > since)
        val newTree = tree.copy(children = newChildren)

        if(lineage.size > 0){
          val lastLineage = lineage.last
          val treeIndex = lastLineage.children.indexWhere(_.name == newTree.name)
          val lastLineageNewChildren = replaceElementInList(lastLineage.children, newTree, treeIndex)

          val updatedLastedLineage = lastLineage.copy(children = lastLineageNewChildren)

          bloop(updatedLastedLineage, since, lineage.dropRight(1), seen)
        } else {
          newTree
        }
      }
    } else {
      bloop(tree, since, lineage, seen ++ Set(tree.name))
    }
  }

  @tailrec
  def treeDescendants(trees: Seq[IntroductionTree], seenNames: Seq[String] = Nil): Seq[String] = {
    val allChildrenNodes = trees.flatMap(t => t.children)
    val rootNodeNames = trees.map(t => t.name)
    val newSeen = seenNames ++ rootNodeNames
    if(allChildrenNodes.nonEmpty){
      treeDescendants(allChildrenNodes, newSeen)
    } else {
      newSeen
    }
  }

}
