package com.example.app.Routes

import com.example.app.db.Tables.NodeAnnotationsRow
import com.example.app.{AuthenticationSupport, SlickRoutes}
import com.example.app.models._
import org.apache.http.auth.AuthenticationException
import org.joda.time.DateTime
import org.scalatra.Ok

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait AnnotationRoutes extends SlickRoutes with AuthenticationSupport{

  get("/annotations/:annotation") {
    contentType = formats("json")
    authenticate()

    val annotation = {params("annotation")}

    val userId = user.userAccountId

    val annotations = Await.result(NodeAnnotation.byAnnotation(userId, annotation), Duration.Inf)

    annotations.map(a => NodeAnnotationRequest(a.nodeName, a.annotation))
  }

  get("/shared/:key/annotations/:annotation") {
    contentType = formats("json")

    val key = {params("key")}.toLowerCase
    val annotation = {params("annotation")}

    val sharedContext = Await.result(ShareableContext.byId(key), Duration.Inf)

    if(DateTime.now().getMillis - sharedContext.createdAtMillis < ShareableContext.weekMillis){

      val annotations = Await.result(NodeAnnotation.byAnnotation(sharedContext.userId, annotation), Duration.Inf)

      annotations.map(a => NodeAnnotationRequest(a.nodeName, a.annotation))
    } else {
      throw new AuthenticationException("Invalid link")
    }
  }

  post("/annotations/create") {
    contentType = formats("json")
    authenticate()

    val requestObject = parsedBody.extract[NodeAnnotationRequest]

    val userId = user.userAccountId

    val newAnnotation = NodeAnnotationsRow(null, userId, requestObject.name, requestObject.annotation)

    val saved = Await.result(NodeAnnotation.create(newAnnotation), Duration.Inf)

    NodeAnnotationRequest(saved.nodeName, saved.annotation)
  }

  post("/annotations/delete") {
    contentType = formats("json")
    authenticate()

    val requestObject = parsedBody.extract[NodeAnnotationRequest]

    val userId = user.userAccountId

    val deleted = Await.result(NodeAnnotation.deleteBy(userId, requestObject.name, requestObject.annotation), Duration.Inf)

    requestObject
  }
}
