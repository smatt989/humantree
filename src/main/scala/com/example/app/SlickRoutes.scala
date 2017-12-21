package com.example.app

import org.scalatra._
import AppGlobals.dbConfig.driver.api._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import org.scalatra.scalate.ScalateSupport

trait SlickRoutes extends ScalatraBase with FutureSupport with JacksonJsonSupport with ScalateSupport with CorsSupport{

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  def db: Database

  methodNotAllowed { _ =>
    if (routes.matchingMethodsExcept(Options, requestPath).isEmpty)
      doNotFound() // correct for options("*") CORS behaviour
    else
      MethodNotAllowed()
  }

  options("/*"){
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"))
  }

  before() {
    response.setHeader("Access-Control-Expose-Headers", SessionTokenStrategy.HeaderKey)
  }

}