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

    Insights.introductions(sinceMillis, userId)
  }

  get("/connectors") {
    contentType = formats("json")
    authenticate()

    val userId = user.userAccountId

    val sinceMillis = {params("since")}.toLong

    Insights.connectors(sinceMillis, userId)
  }

  get("/coolingoff") {
    contentType = formats("json")
    authenticate()

    val userId = user.userAccountId

    Insights.coolingOff(userId)
  }

}
