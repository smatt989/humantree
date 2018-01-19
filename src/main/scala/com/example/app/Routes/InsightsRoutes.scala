package com.example.app.Routes

import com.example.app.{AuthenticationSupport, SlickRoutes}
import com.example.app.models._
import org.apache.http.auth.AuthenticationException
import org.joda.time.DateTime

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait InsightsRoutes extends SlickRoutes with AuthenticationSupport{

  get("/introductions") {
    contentType = formats("json")
    authenticate()

    val userId = user.userAccountId

    val sinceMillis = {params("since")}.toLong

    val connectedAccounts = Await.result(GmailAccessToken.allStatusesByUserId(userId), Duration.Inf)

    val emails = connectedAccounts.map(_._1.email)
    val introductions = Await.result(Introduction.introductionsSince(emails, sinceMillis), Duration.Inf)
    val links = Await.result(IdentityLink.byUserId(userId), Duration.Inf)

    val renamedIntros = IntroductionTree.distinctIntros(IntroductionTree.renameIntros(introductions, emails, links))

    renamedIntros.map(i => {
      val sender = if(emails.contains(i.senderPersonEmail)){
        None
      } else {
        Some(i.senderPersonEmail)
      }
      IntroductionJson(sender, i.receiverPersonEmail, i.introPersonEmail, i.introTimeMillis)
    })
  }

  get("/connectors") {
    contentType = formats("json")
    authenticate()

    val userId = user.userAccountId

    val sinceMillis = {params("since")}.toLong

    val connectedAccounts = Await.result(GmailAccessToken.allStatusesByUserId(userId), Duration.Inf)

    val emails = connectedAccounts.map(_._1.email)
    val introductions = Await.result(Introduction.introductionsSince(emails, sinceMillis), Duration.Inf)
    val links = Await.result(IdentityLink.byUserId(userId), Duration.Inf)

    val renamedIntros = IntroductionTree.renameIntros(introductions, emails, links).filterNot(a => emails.contains(a.senderPersonEmail))

    val introducers = renamedIntros.map(_.senderPersonEmail)

    val introductionTrees = introducers.flatMap(i => IntroductionTree.treeFromIntroductions(i, emails, renamedIntros, links))

    introductionTrees.map(i => ConnectorSummary(i.name, IntroductionTree.treeDescendants(Seq(i)).size - 1))
  }

  get("/coolingoff") {
    contentType = formats("json")
    authenticate()

    val userId = user.userAccountId

    val connectedAccounts = Await.result(GmailAccessToken.allStatusesByUserId(userId), Duration.Inf)

    val emails = connectedAccounts.map(_._1.email)

    val links = Await.result(IdentityLink.byUserId(userId), Duration.Inf)

    Interaction.coolingOffInteractions(emails, links).sortBy(_.interactions).reverse
  }

}
