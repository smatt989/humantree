package com.example.app.models

import com.example.app.db.Tables.IntroductionsRow

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by matt on 12/31/17.
  */
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

  def treeByRootAndContext(root: String, emailContexts: Seq[String]) = {
    val intros = introsByEmails(emailContexts)

    val introsBySender = intros.groupBy(_.senderPersonEmail)

    treeFromIntroMap(Seq(root), introsBySender)
  }

  def treeFromIntroMap(roots: Seq[String], introMap: Map[String, Seq[IntroductionsRow]]): Seq[IntroductionTree] = {
    val leaves = roots.flatMap(r => introMap.get(r).getOrElse(Nil))

    val descendants = if(leaves.size > 0){
      treeFromIntroMap(leaves.map(_.introPersonEmail), introMap)
    } else {
      Nil
    }

    val leavesBySender = leaves.groupBy(_.senderPersonEmail)
    val descendantsByRoot = descendants.groupBy(_.name)

    roots.map(root => {
      val rootLeaves = leavesBySender.get(root).getOrElse(Nil)
      val rootDescendants = rootLeaves.flatMap(r => descendantsByRoot.get(r.introPersonEmail).getOrElse(Nil))

      IntroductionTree(root, rootDescendants)
    })
  }

  def allDescendants(tree: IntroductionTree): Seq[String] = {
    tree.children.flatMap(allDescendants) :+ tree.name
  }

  def isADescendant(tree: IntroductionTree, name: String) = {
     val descendants = allDescendants(tree)
     descendants.contains(name)
  }

}
