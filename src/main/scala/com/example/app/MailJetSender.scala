package com.example.app

import com.mailjet.client.{MailjetClient, MailjetRequest}
import com.mailjet.client.resource.Email
import org.joda.time.DateTime
import org.json.{JSONArray, JSONObject}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by matt on 1/26/18.
  */
object MailJetSender {

  val mailjetClient = new MailjetClient(System.getenv("MAIL_JET_PUBLIC_KEY"), System.getenv("MAIL_JET_SECRET_KEY"))

  val fromEmail = sys.env.get("SENDER_EMAIL").getOrElse("ScheduledQ@gmail.com")
  val fromName = sys.env.get("SENDER_NAME").getOrElse("Treople")

  val DOMAIN = sys.env.get("DOMAIN").getOrElse(s"http://localhost:8080/")

  val CONFIRMATION_EMAIL = sys.env.get("CONFIRMATION_EMAIL").getOrElse("matthew.slotkin@gmail.com")

  def sendEmail(subject: String, body: String, to: String) = {
    val request = new MailjetRequest(Email.resource)
      //.property(Email.RECIPIENTS, "matthew.slotkin@gmail.com")
      .property(Email.SUBJECT, subject)
      .property(Email.HTMLPART, body)
      .property(Email.FROMEMAIL, fromEmail)
      .property(Email.FROMNAME, fromName)
      .property(Email.RECIPIENTS, new JSONArray()
        .put(new JSONObject()
          .put("Email", to)));

    println("sending")
    val response = mailjetClient.post(request)
    System.out.println(response.getData());
  }

}
