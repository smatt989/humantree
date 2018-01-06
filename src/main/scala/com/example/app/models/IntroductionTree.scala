package com.example.app.models

import com.example.app.db.Tables.IntroductionsRow
import org.json4s.JsonAST.{JArray, JObject, JString}

import scala.annotation.tailrec
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by matt on 12/31/17.
  */
class IntroductionTree(val name: String, var children: Seq[IntroductionTree] = Nil) {
  def serialize: TreeOutput = TreeOutput(name, children.map(_.serialize))

  def hoop: JObject = JObject(
    ("name", JString(name)),
    ("children", JArray(children.map(_.hoop).toList))
  )
}

case class TreeOutput(name: String, children: Seq[TreeOutput] = Nil)


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
    val intros = introsByEmails(emailContexts).filter(a => a.introPersonEmail != a.senderPersonEmail)
        .filter(_.introPersonEmail != root)

    val fullTree = edgesIntoTree(intros.map(a => a.senderPersonEmail -> a.introPersonEmail), Nil, Set())

    fullTree.find(_.name == root).toSeq
    //hereWeGo(root, nodeToChildren)

    //treeFromIntroductions(intros)

    //treeFromIntroMap(Seq(root), introsBySender)
  }


  //START HERE
  def guysInTree(trees: Seq[TreeOutput], seen: Seq[String] = Nil): Seq[String] = {
    val nextLevel = trees.flatMap(_.children)
    val thisLevel = trees.map(_.name)
    if(nextLevel.size > 0){
      guysInTree(nextLevel, seen ++ thisLevel)
    } else {
      seen ++ thisLevel
    }
  }

  def whichTreeContainsParent(trees: Seq[TreeOutput], parentName: String): TreeOutput = {
    val treeToContents = trees.map(t => t -> guysInTree(Seq(t)))
    treeToContents.find(_._2.contains(parentName)).get._1
  }

  def amendTreeList(trees: Seq[TreeOutput], fixedTree: TreeOutput, atIndex: Int) = {

    (trees.take(atIndex) :+ fixedTree) ++ trees.drop(atIndex + 1)
  }

  def repairLineage(lineageOldestFirst: Seq[TreeOutput], currentTree: TreeOutput): TreeOutput = {
    if(lineageOldestFirst.size > 0){
      val last = lineageOldestFirst.last

      val optionalExistingChild = last.children.find(_.name == currentTree.name)
      val newChildren = if(optionalExistingChild.isDefined){
        val childIndex = last.children.indexWhere(_.name == currentTree.name)
        amendTreeList(last.children, currentTree, childIndex)
      } else {
        last.children :+ currentTree
      }

      repairLineage(lineageOldestFirst.dropRight(1), last.copy(children = newChildren))
    } else {
      currentTree
    }
  }

  def appendTreeToParent(trees: Seq[TreeOutput], tree: TreeOutput, parentName: String, searchParents: Seq[TreeOutput] = Nil): TreeOutput = {

    val treeWithParent = whichTreeContainsParent(trees, parentName)

    if(treeWithParent.name == parentName){
      val currentTree = treeWithParent.copy(children = treeWithParent.children :+tree)
      repairLineage(searchParents, currentTree)
    } else {
      appendTreeToParent(treeWithParent.children, tree, parentName, searchParents :+ treeWithParent)
    }
  }

  def edgesIntoTree(introductions: Seq[(String, String)], trees: Seq[TreeOutput], seen: Set[String]): Seq[TreeOutput] = {
    if(introductions.size > 0){
      val nextIntros = introductions.tail

      val intro = introductions.head

      val parent = intro._1
      val child = intro._2

      val newSeen = seen ++ Set(parent, child)
      //trees.foreach(println)
      //println("\na round")
      //println("parent: "+parent)
      //println("child: "+child)

      if(!seen.contains(parent) && !seen.contains(child)) {
        val newTree = TreeOutput(parent, Seq(TreeOutput(child, Nil)))
        edgesIntoTree(nextIntros, trees :+ newTree, newSeen)

      } else if (!seen.contains(parent)){
        val newChildIndex = trees.indexWhere(_.name == child)
        val newChild = trees(newChildIndex)
        val updatedTree = TreeOutput(parent, Seq(newChild))

        val newTreeList = amendTreeList(trees, updatedTree, newChildIndex)
        edgesIntoTree(nextIntros, newTreeList, newSeen)

      } else if (!seen.contains(child)) {
        val childTree = TreeOutput(child, Nil)

        val updatedTree = appendTreeToParent(trees, childTree, parent)

        val lookingFor = updatedTree.name
        val nameIndex = trees.indexWhere(_.name == lookingFor)

        val newTreeList = amendTreeList(trees, updatedTree, nameIndex)
        edgesIntoTree(nextIntros, newTreeList, newSeen)

      } else {
        val childTree = trees.find(_.name == child).get
        val withoutChild = trees.filter(_.name != child)

        val updatedTree = appendTreeToParent(withoutChild, childTree, parent)

        val lookingFor = updatedTree.name
        val nameIndex = withoutChild.indexWhere(_.name == lookingFor)

        val newTreeList = amendTreeList(withoutChild, updatedTree, nameIndex)
        edgesIntoTree(nextIntros, newTreeList, newSeen)

      }

    } else {
      trees
    }
  }

  def allDescendants(tree: IntroductionTree): Seq[String] = {
    tree.children.flatMap(allDescendants) :+ tree.name
  }

  def isADescendant(tree: IntroductionTree, name: String) = {
     val descendants = allDescendants(tree)
     descendants.contains(name)
  }

}
