package com.example.app

import com.example.app.Routes._
import org.scalatra.{FutureSupport, ScalatraServlet}


class SlickApp() extends ScalatraServlet with FutureSupport
  with UserRoutes
  with SessionRoutes
  with AppRoutes
  with GmailAuthRoutes
  with TreeRoutes
  with IdentityRoutes
  with AnnotationRoutes {

  def db = AppGlobals.db

  lazy val realm = "rekki"

  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global

}