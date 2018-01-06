package com.example.app.Routes

import com.example.app.{AuthenticationSupport, SlickRoutes}
import com.example.app.models._
import org.apache.http.auth.AuthenticationException
import org.joda.time.DateTime

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait TreeRoutes extends SlickRoutes with AuthenticationSupport{

  post("/tree") {
    contentType = formats("json")
    authenticate()

    val userId = user.userAccountId

    val requestObject = parsedBody.extract[TreeRequestObject]

    val myEmails = Await.result(GmailAccessToken.allStatusesByUserId(userId), Duration.Inf).map(_._1.email)

    val root = requestObject.root.getOrElse(myEmails.head)
    val context = requestObject.emails.getOrElse(myEmails)

    IntroductionTree.treeByRootAndContext(root, context)
    //println("no problemo!")
    //org.json4s.JObject()
    //cat
  }

  post("/share") {
    contentType = formats("json")
    authenticate()

    val userId = user.userAccountId

    val treeShareObject = parsedBody.extract[TreeShareObject]

    val saved = Await.result(ShareableContext.createShareableContext(userId, treeShareObject.root), Duration.Inf)

    SharedContextRequestObject(saved.shareableContextId)
  }

  val minuteMillis = 1000 * 60
  val dayMillis = minuteMillis * 60 * 24
  val weekMillis = dayMillis * 7

  post("/shared/:key") {
    contentType = formats("json")

    val key = {params("key")}.toLowerCase

    val sharedContext = Await.result(ShareableContext.byId(key), Duration.Inf)

    if(DateTime.now().getMillis - sharedContext.createdAtMillis < weekMillis){
      val contextEmails = Await.result(GmailAccessToken.allStatusesByUserId(sharedContext.userId), Duration.Inf).map(_._1.email)

      IntroductionTree.treeByRootAndContext(sharedContext.treeRoot, contextEmails)
    } else {
      throw new AuthenticationException("Invalid link")
    }
  }

}
