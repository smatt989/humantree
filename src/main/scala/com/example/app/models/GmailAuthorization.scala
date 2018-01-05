package com.example.app.models

import java.io.{File, FileInputStream, InputStreamReader}

import com.amazonaws.services.applicationdiscovery.model.AuthorizationErrorException
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2._
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.gmail.{Gmail, GmailScopes}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import collection.JavaConversions._

object GmailAuthorization {

  val JSON_FACTORY = JacksonFactory.getDefaultInstance()
  val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
  val SCOPES = Seq(GmailScopes.GMAIL_READONLY, "profile")

  val APPLICATION_NAME = System.getenv("HUMAN_TREE_APPLICATION_NAME")
  val CLIENT_ID = System.getenv("HUMAN_TREE_CLIENT_ID")
  val CLIENT_SECRET = System.getenv("HUMAN_TREE_CLIENT_SECRET")

  val LOCAL_DOMAIN = System.getenv("LOCAL_DOMAIN")

  val REDIRECT_URI = LOCAL_DOMAIN+"/gmailcallback"

  def authorize(userId: Int, email: String): Credential = {

    val savedAccessToken = Await.result(GmailAccessToken.fetchUserGmailAccessToken(userId, email), Duration.Inf)

    try {
      val token = savedAccessToken.get

      val response = new GoogleRefreshTokenRequest(
        HTTP_TRANSPORT,
        JSON_FACTORY,
        token.accessToken,
        CLIENT_ID,
        CLIENT_SECRET).execute()

      saveCredentials(response, userId)

    } catch {

      case _ => throw new AuthorizationErrorException("could not log into the googs")
    }
  }

  def webAuthorize(userId: Int) = {

    val flow = makeFlow()

    val authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI)

    println("GMAIL AUTHORIZATION: "+authorizationUrl.toString)

    authorizationUrl.build()
  }

  def codeToAuthorize(code: String, appUserId: Int) = {
    val request = getToken(code)

    saveCredentials(request, appUserId)
  }

  def makeService(credential: Credential) = {
    new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
      .setApplicationName(APPLICATION_NAME)
      .build()
  }

  def getToken(code: String) = {
    val flow = makeFlow()

    flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute()
  }

  def credentialFromToken(response: GoogleTokenResponse) = {
    val c = new GoogleCredential.Builder()
      .setJsonFactory(JSON_FACTORY)
      .setTransport(HTTP_TRANSPORT)
      .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
      .build()
      .setAccessToken(response.getAccessToken)
      .setRefreshToken(response.getRefreshToken)

    c.refreshToken()

    c

  }

  def saveCredentials(tokenRequest: GoogleTokenResponse, appUserId: Int) = {
    val idToken = tokenRequest.parseIdToken()
    val userId = idToken.getPayload.getUserId

    val credential = credentialFromToken(tokenRequest)

    val s = makeService(credential)

    val response = s.users().getProfile(userId).execute()

    val email = response.getEmailAddress

    if(credential.refreshToken()) {
      val saved = Await.result(GmailAccessToken.updateUserGmailAccessToken(credential.getRefreshToken, email, appUserId), Duration.Inf)
      val saveProgress = GmailScrapeProgress.initializeOrDoNothing(saved.gmailAccesTokenId)
    }

    credential
  }


  def makeFlow() = {

    new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, SCOPES)
      //.setDataStoreFactory(DATA_STORE_FACTORY)
      .setAccessType("offline")
      .setApprovalPrompt("force")
      .build()
  }

}
