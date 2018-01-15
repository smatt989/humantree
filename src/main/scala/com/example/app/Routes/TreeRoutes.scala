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

    val links = Await.result(IdentityLink.byUserId(userId), Duration.Inf)

    IntroductionTree.treeByRootAndContext(root, context, links)


    //val tree = IntroductionTree.treeByRootAndContext(root, context, links)

    //Seq(IntroductionTree.bloop(tree.head, DateTime.now().minusDays(14).getMillis))
  }

  post("/share") {
    contentType = formats("json")
    authenticate()

    val userId = user.userAccountId

    val treeShareObject = parsedBody.extract[TreeShareObject]

    val saved = Await.result(ShareableContext.createShareableContext(userId, treeShareObject.root), Duration.Inf)

    SharedContextRequestObject(saved.shareableContextId)
  }

  post("/shared/:key") {
    contentType = formats("json")

    val key = {params("key")}.toLowerCase

    val sharedContext = Await.result(ShareableContext.byId(key), Duration.Inf)

    if(DateTime.now().getMillis - sharedContext.createdAtMillis < ShareableContext.weekMillis){
      val contextEmails = Await.result(GmailAccessToken.allStatusesByUserId(sharedContext.userId), Duration.Inf).map(_._1.email)

      val links = Await.result(IdentityLink.byUserId(sharedContext.userId), Duration.Inf)

      IntroductionTree.treeByRootAndContext(sharedContext.treeRoot, contextEmails, links)
    } else {
      throw new AuthenticationException("Invalid link")
    }
  }

}
