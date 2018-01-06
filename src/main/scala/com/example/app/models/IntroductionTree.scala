package com.example.app.models

import com.example.app.db.Tables.IntroductionsRow
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

  def treeByRootAndContext(root: String, emailContexts: Seq[String]) = {
    val intros = introsByEmails(emailContexts).filter(a => a.introPersonEmail != a.senderPersonEmail)
        .filter(_.introPersonEmail != root)

    val senderToIntros = intros.groupBy(_.senderPersonEmail).mapValues(_.map(_.introPersonEmail))

    //val firstTime = DateTime.now().getMillis

    //val fullTree = edgesIntoTree(intros.map(a => a.senderPersonEmail -> a.introPersonEmail), Nil, Set())

    //val x = fullTree.find(_.name == root).toSeq

    //val secondTime = DateTime.now().getMillis

    val y = Seq(tryingThisWay(senderToIntros, IntroductionTree(root)))

    //val thirdTime = DateTime.now().getMillis

    //println("old way: "+(secondTime - firstTime))
    //println("new way: "+(thirdTime - secondTime))

    y
  }

  @tailrec
  def edgesIntoTree(edges: Seq[(String, String)], assembledNodes: Seq[IntroductionTree] = Nil, seenNodeNames: Set[String] = Set()): Seq[IntroductionTree] = {
    if(edges.size > 0){

      val currentEdge = edges.head
      val tailEdges = edges.tail

      val parentName = currentEdge._1
      val childName = currentEdge._2

      val newSeenNodeNames = seenNodeNames ++ Set(parentName, childName)

      val parentHasBeenSeen = seenNodeNames.contains(parentName)
      val childHasBeenSeen = seenNodeNames.contains(childName)

      /*
      POSSIBILITIES
       - BOTH PARENT AND CHILD HAVE NOT BEEN SEEN BEFORE
          -> CREATE NEW PARENT NODE OF PARENT WITH ONE CHILD
          -> ADD NEW NODE TO ASSEMBLED NODES
       - THE PARENT HAS NOT BEEN SEEN BEFORE
          -> FIND THE CHILD NODE IN ASSEMBLED NODES (MUST BE A ROOT GIVEN NO PARENT YET) AND ADD IT AS A CHILD OF NEW PARENT NODE
          -> REPLACE ORIGINAL CHILD NODE IN ASSEMBLED NODES WITH PARENT NODE CONTAINING CHILD
       - THE CHILD HAS NOT BEEN SEEN BEFORE
          -> FIND THE PARENT NODE IN ASSEMBLED NODES (COULD BE ANYWHERE IN A PREEXISTING NODE) AND ADD CHILD TO IT
          -> REPLACE TREE CONTAINING PARENT NODE IN ASSEMBLED NODES WITH NEW NODE CONTAINING PARENT W NEW CHILD
       - THE CHILD AND PARENT HAVE BOTH BEEN SEEN BEFORE
          -> FIND THE CHILD NODE IN ASSEMBLED NODES (MUST BE A ROOT), FIND THE PARENT NODE IN ASSEMBLED NODES (COULD BE ANYWHERE), AND ADD CHILD
          -> REPLACE THE TREE CONTAINING PARENT NODE IN ASSEMBLED NODES WITH NEW NODE
          -> REMOVE CHILD NODE FROM ASSEMBLED NODES

     */

      (parentHasBeenSeen, childHasBeenSeen) match {
        case (false, false) =>
          val newTree = IntroductionTree(parentName, Seq(IntroductionTree(childName, Nil)))
          edgesIntoTree(tailEdges, assembledNodes :+ newTree, newSeenNodeNames)
        case (false, true) =>
          val newChildIndex = assembledNodes.indexWhere(_.name == childName)
          val newChild = assembledNodes(newChildIndex)
          val updatedTree = IntroductionTree(parentName, Seq(newChild))

          val newTreeList = replaceElementInList(assembledNodes, updatedTree, newChildIndex)
          edgesIntoTree(tailEdges, newTreeList, newSeenNodeNames)
        case (true, false) =>
          val childTree = IntroductionTree(childName, Nil)

          val updatedTree = appendNodeToParent(assembledNodes, childTree, parentName)

          val lookingFor = updatedTree.name
          val nameIndex = assembledNodes.indexWhere(_.name == lookingFor)

          val newTreeList = replaceElementInList(assembledNodes, updatedTree, nameIndex)
          edgesIntoTree(tailEdges, newTreeList, newSeenNodeNames)
        case (true, true) =>
          val childTree = assembledNodes.find(_.name == childName).get
          val withoutChild = assembledNodes.filter(_.name != childName)

          val updatedTree = appendNodeToParent(withoutChild, childTree, parentName)

          val lookingFor = updatedTree.name
          val nameIndex = withoutChild.indexWhere(_.name == lookingFor)

          val newTreeList = replaceElementInList(withoutChild, updatedTree, nameIndex)
          edgesIntoTree(tailEdges, newTreeList, newSeenNodeNames)
      }

    } else {
      assembledNodes
    }
  }




  //RETURNS ALL DESCENDANT NODE NAMES THAT APPEAR IN A LIST OF NODES
  @tailrec
  def nodeNamesInNode(nodes: Seq[IntroductionTree], seenNodeNames: Seq[String] = Nil): Seq[String] = {
    val allChildrenNodes = nodes.flatMap(_.children)
    val rootNodeNames = nodes.map(_.name)
    if(allChildrenNodes.size > 0){
      nodeNamesInNode(allChildrenNodes, seenNodeNames ++ rootNodeNames)
    } else {
      seenNodeNames ++ rootNodeNames
    }
  }

  //IN A LIST OF NODES, FIND WHICH NODE HAS A GIVEN NODE NAME AS A DESCENDANT
  def whichNodeContainsNodeNameDescendant(nodes: Seq[IntroductionTree], nodeNameToFind: String): IntroductionTree = {
    val nodeToDescendants = nodes.map(t => t -> nodeNamesInNode(Seq(t)))
    nodeToDescendants.find(_._2.contains(nodeNameToFind)).get._1
  }

  //REPLACE AN ELEMENT AT AN INDEX IN A LIST
  def replaceElementInList[A](list: Seq[A], elementToSwapIn: A, atIndex: Int) = {
    (list.take(atIndex) :+ elementToSwapIn) ++ list.drop(atIndex + 1)
  }

  //GIVEN A LIST OF ANCESTORS IN ORDER FROM FURTHEST TO CLOSEST, ADD NEW DESCENDANT OR REPLACE OLD VERSION OF A DESCENDANT WITH A NEW ONE UNTIL THE WHOLE LINEAGE IS FIXED
  @tailrec
  def repairLineage(lineageOldestFirst: Seq[IntroductionTree], fixedNode: IntroductionTree): IntroductionTree = {
    if(lineageOldestFirst.size > 0){
      val last = lineageOldestFirst.last

      val optionalExistingChild = last.children.find(_.name == fixedNode.name)

      //IF CHILD ALREADY EXISTS, REPLACE IT WITH NEW ONE, OTHERWISE ADD NEW ONE TO PARENT
      val newChildren = if(optionalExistingChild.isDefined){
        val childIndex = last.children.indexWhere(_.name == fixedNode.name)
        replaceElementInList(last.children, fixedNode, childIndex)
      } else {
        last.children :+ fixedNode
      }

      repairLineage(lineageOldestFirst.dropRight(1), last.copy(children = newChildren))
    } else {
      fixedNode
    }
  }

  //APPEND A NODE TO ITS PARENT BY FINDING THE PARENT, AND THEN FIXING THE LINEAGE SO THAT THE OLDEST ANCESTOR CONTAINS THE NEW NODE AS A DESCENDANT
  @tailrec
  def appendNodeToParent(nodes: Seq[IntroductionTree], childToAppend: IntroductionTree, parentName: String, lineage: Seq[IntroductionTree] = Nil): IntroductionTree = {

    val nodeWithParent = whichNodeContainsNodeNameDescendant(nodes, parentName)

    if(nodeWithParent.name == parentName){
      val currentTree = nodeWithParent.copy(children = nodeWithParent.children :+childToAppend)
      repairLineage(lineage, currentTree)
    } else {
      appendNodeToParent(nodeWithParent.children, childToAppend, parentName, lineage :+ nodeWithParent)
    }
  }


  @tailrec
  def tryingThisWay(mapsOfIntros: Map[String, Seq[String]], currentTree: IntroductionTree, lineage: Seq[IntroductionTree] = Nil, seen: Set[String] = Set()): IntroductionTree = {
      if (seen.contains(currentTree.name)) {
        val children = currentTree.children
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

        val children = mapsOfIntros.get(currentTree.name).getOrElse(Nil).map(c => IntroductionTree(c))
        val newSeen = seen ++ Set(currentTree.name)
        tryingThisWay(mapsOfIntros, currentTree.copy(children = children), lineage, newSeen)
      }

  }

}
