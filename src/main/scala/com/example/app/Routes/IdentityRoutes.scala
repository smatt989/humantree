package com.example.app.Routes

import com.example.app.db.Tables.IdentityLinksRow
import com.example.app.{AuthenticationSupport, SlickRoutes}
import com.example.app.models._
import org.apache.http.auth.AuthenticationException
import org.joda.time.DateTime
import org.scalatra.Ok

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait IdentityRoutes extends SlickRoutes with AuthenticationSupport{

  post("/links/create") {
    contentType = formats("json")
    authenticate()

    val requestObject = parsedBody.extract[IdentityLinkRequest]

    val userId = user.userAccountId

    if(requestObject.left != requestObject.right) {

      val newLink = IdentityLinksRow(null, userId, requestObject.left, requestObject.right)

      val saved = Await.result(IdentityLink.create(newLink), Duration.Inf)

      IdentityLinkRequest(saved.leftSide, saved.rightSide)
    } else {
      Ok{"405"}
    }
  }

  get("/links") {
    contentType = formats("json")
    authenticate()

    val userId = user.userAccountId

    val links = Await.result(IdentityLink.byUserId(userId), Duration.Inf)

    links.map(link => IdentityLinkRequest(link.leftSide, link.rightSide))
  }

  post("/links/delete") {
    contentType = formats("json")
    authenticate()

    val userId = user.userAccountId

    val requestObject = parsedBody.extract[IdentityLinkRequest]

    val deleted = Await.result(IdentityLink.deleteBy(userId, requestObject.left, requestObject.right), Duration.Inf)

    Ok{"200"}
  }
}
