package com.example.app.models

import com.example.app.UpdatableDBObject
import org.mindrot.jbcrypt.BCrypt
import com.example.app.AppGlobals
import AppGlobals.dbConfig.driver.api._
import com.example.app.db.Tables._
import com.example.app.db.{Tables => T}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.Await

case class UserCreate(email: String, password: String) {
  lazy val makeUser =
    UserAccountsRow(0, email, User.makeHash(password))
}

case class UpdateUser(email: String, password: String, newEmail: Option[String], newPassword: Option[String]){
  lazy val userLogin =
    UserLogin(email, password)
}

case class UserLogin(email: String, password: String)

case class UserJson(id: Int, email: String)


object User extends UpdatableDBObject[UserAccountsRow, UserAccounts]{

  def makeJson(a: UserAccountsRow) =
    UserJson(a.userAccountId, a.email)

  lazy val table = T.UserAccounts

  def idFromRow(a: _root_.com.example.app.db.Tables.UserAccountsRow) =
    a.userAccountId

  def updateId(a: _root_.com.example.app.db.Tables.UserAccountsRow, id: Int) =
    a.copy(userAccountId = id)

  def idColumnFromTable(a: _root_.com.example.app.db.Tables.UserAccounts) =
    a.userAccountId

  def updateQuery(a: UserAccountsRow) = table.filter(t => idColumnFromTable(t) === idFromRow(a))
    .map(x => (x.email, x.hashedPassword))
    .update((a.email, a.hashedPassword))

  def makeHash(password: String) =
    BCrypt.hashpw(password, BCrypt.gensalt())

  private[this] def checkPassword(password: String, hashedPassword: String) =
    BCrypt.checkpw(password, hashedPassword)

  def authenticate(user: UserAccountsRow, password: String) = {
    checkPassword(password, user.hashedPassword)
  }

  def searchUserName(query: String) = {
    val queryString = "%"+query.toLowerCase()+"%"
    db.run(table.filter(_.email.toLowerCase like queryString).result)//.map(_.map(reifyJson))
  }

  private[this] def unauthenticatedUserFromUserLogin(userLogin: UserLogin) = {

    Await.result(
      db.run(table.filter(_.email.toLowerCase === userLogin.email.toLowerCase()).result).map(_.headOption.getOrElse{
        throw new Exception("No user with that email")
      }), Duration.Inf)
  }

  def authenticatedUser(userLogin: UserLogin) = {
    val user = unauthenticatedUserFromUserLogin(userLogin)

    if(authenticate(user, userLogin.password))
      Some(user)
    else
      None
  }

  def uniqueEmail(email: String) =
    db.run(table.filter(_.email.toLowerCase === email.toLowerCase).result).map(_.isEmpty)

  def createNewUser(userCreate: UserCreate) = {
    val emailIsUnique = Await.result(uniqueEmail(userCreate.email), Duration.Inf)

    if(emailIsUnique)
      create(userCreate.makeUser)
    else
      throw new Exception("Must provide unique email")
  }

  def updateUser(updateUser: UpdateUser) = {
    val user = authenticatedUser(updateUser.userLogin)
    if(user.isDefined){
      val newUser = (updateUser.newEmail, updateUser.newPassword) match {
        case (Some(ne), Some(np)) => user.get.copy(email = ne, hashedPassword = makeHash(np))
        case (Some(ne), None) => user.get.copy(email = ne)
        case (None, Some(np)) => user.get.copy(hashedPassword = makeHash(np))
        case _ => user.get
      }
      save(newUser)
    } else {
      throw new Exception("Unable to authenticate")
    }
  }
}