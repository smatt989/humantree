package com.example.app.Routes

import com.example.app.{AuthenticationSupport, SlickRoutes}
import com.example.app.models._
import org.apache.http.auth.AuthenticationException
import org.scalatra.Ok

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait GmailAuthRoutes extends SlickRoutes with AuthenticationSupport{

  get("/connectedemails") {
    contentType = formats("json")
    authenticate()

    val userId = user.userAccountId

    //val myEmails = Await.result(GmailAccessToken.fetchAllForUser(userId), Duration.Inf)
    //myEmails.map(e => ConnectedGmailAccount(e.email))

    val connectedAccounts = Await.result(GmailAccessToken.allStatusesByUserId(userId), Duration.Inf)

    connectedAccounts.map{case (account, progress) =>
      ConnectedGmailAccount(account.email, progress.status, progress.totalThreads, progress.threadsProcessed, progress.lastPulledMillis)}
  }

  get("/scrape/:email") {
    authenticate()

    val email = {params("email")}

    val userId = user.userAccountId

    val authorized = GmailAuthorization.authenticateEmailAccessForUser(email, userId)

    authorized match {
      case false => throw new AuthenticationException()
      case true =>
        val refreshToken = Await.result(GmailAccessToken.fetchUserGmailAccessToken(userId, email), Duration.Inf)
        if(refreshToken.isEmpty) {
          redirect("/auth?email="+email)
        }
        contentType = formats("json")
        EmailScraper.startAnActor(email, userId)

        Ok{"200"}
    }
  }

  get("/rescrape/:email") {
    authenticate()

    val email = {params("email")}

    val userId = user.userAccountId

    val authorized = GmailAuthorization.authenticateEmailAccessForUser(email, userId)

    authorized match {
      case false => throw new AuthenticationException()
      case true =>
        val refreshToken = Await.result(GmailAccessToken.fetchUserGmailAccessToken(userId, email), Duration.Inf)
        if(refreshToken.isEmpty) {
          redirect("/auth?email="+email)
        }
        contentType = formats("json")

        val progress = Await.result(GmailScrapeProgress.getByGmailConnectionId(refreshToken.get.gmailAccesTokenId), Duration.Inf)

        progress.map(p => {
          Await.result(GmailScrapeProgress.delete(p.gmailScrapeProgressId), Duration.Inf)
        })

        Await.result(Introduction.removeAllForReceiverEmail(email), Duration.Inf)

        EmailScraper.startAnActor(email, userId, Some(0))

        Ok{"200"}
    }
  }

  get("/auth") {

    authenticate()

    val userId = user.userAccountId

    val redirectTo = GmailAuthorization.webAuthorize(userId)

    redirect(redirectTo)
  }

  get("/gmailcallback") {

    authenticate()

    val userId = user.userAccountId

    val code = {params("code")}

    GmailAuthorization.codeToAuthorize(code, userId)

    redirect("/#/emails")
  }

}
