package com.example.app.Routes

import com.example.app.models.{User, UserSession}
import com.example.app.{AuthenticationSupport, SlickRoutes}

trait SessionRoutes extends SlickRoutes with AuthenticationSupport{

  get("/sessions") {
    contentType = formats("json")
    UserSession.getAll
  }

  get("/sessions/new"){
    contentType = formats("json")
    authenticate()
    User.makeJson(user)
  }

  post("/sessions/logout"){
    authenticate()
    val id = user.userAccountId
    scentry.logout()
    scentry.store.invalidate()
    val session = UserSession.fromUser(id)
    session.map(s => UserSession.delete(s.userSessionId))
    "200"
  }

}