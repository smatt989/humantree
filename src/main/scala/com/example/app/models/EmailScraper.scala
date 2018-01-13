package com.example.app.models

import com.example.app.db.Tables.IntroductionsRow
import com.google.api.services.gmail.{Gmail, GmailScopes}
import com.google.api.services.gmail.model.{Message, MessagePartHeader, Thread => GThread}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import akka.actor.{Actor, ActorSystem, Props}
import com.example.app.EmailScrapeRequestObject
import org.json4s.ParserUtil.ParseException

import collection.JavaConversions._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class EmailActor extends Actor {

  def receive = {
    case a: EmailScrapeRequestObject =>
      println("scraping...")

      EmailScraper.basicProcess(a.email, a.appUserId, a.startAt)
      println("donezo")
      context.stop(self)
  }
}

object EmailScraper {
  val system = ActorSystem()

  def startAnActor(email: String, appUserId: Int, startAt: Option[Int] = None) = {
    val request = EmailScrapeRequestObject(email, appUserId, startAt)
    val myActor = system.actorOf(Props[EmailActor])

    myActor ! request
  }

  def threadsByLabels(service: Gmail, userId: String, labels: Seq[String], pageToken: Option[String] = None): Seq[GThread] = {
    val response = if(pageToken.isEmpty)
                    service.users().threads().list(userId).setLabelIds(labels).execute()
                  else
                    service.users().threads().list(userId).setLabelIds(labels).setPageToken(pageToken.get).execute()

    val nextThreads = if(response.getThreads != null && response.getNextPageToken != null)
                        threadsByLabels(service, userId, labels, Some(response.getNextPageToken))
                      else
                        Nil

    val theseThreads: Seq[GThread] = response.getThreads.toList

    theseThreads ++ nextThreads
  }

  def getOneThread(service: Gmail, userId: String, threadId: String) =
    service.users().threads().get(userId, threadId).execute()

  def headerValueByName(headers: Seq[MessagePartHeader], key: String) = {
    try {
      headers.find(a => a.getName == key).map(_.getValue)
    } catch {
      case _ => None
    }
  }


  val emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])".r

  val bracketRegex = "(?<=<).*(?=>)".r

  //now just returns email addresses
  def userHeaderValueByName(headers: Seq[MessagePartHeader], key: String) = {
    val output = headerValueByName(headers, key)
    if(output.isDefined)
      emailRegex.findAllIn(output.get.toLowerCase()).toArray.toSeq.map(_.replaceAll("'", "")).distinct
    else
      Nil
  }

  def countNumbersInString(str: String) = {
    val r = "\\d".r
    r.findAllIn(str).toArray.toSeq.size
  }

  def basicProcess(myEmail: String, appUserId: Int, forceStartAt: Option[Int] = None) = {

    println("getting threads...")

    val credential = GmailAuthorization.authorize(appUserId, myEmail)
    val service = GmailAuthorization.makeService(credential)
    val connectedAccount = Await.result(GmailAccessToken.fetchUserGmailAccessToken(appUserId, myEmail), Duration.Inf).get

    val progress = GmailScrapeProgress.initializeOrDoNothing(connectedAccount.gmailAccesTokenId)
    GmailScrapeProgress.updateStatus(progress.gmailScrapeProgressId, GmailScrapeProgress.STATUS_SCRAPING)

    val toDrop = math.max(forceStartAt.getOrElse(progress.threadsProcessed) - 20, 0)

    val threads = threadsByLabels(service, myEmail, Seq("SENT")).reverse
    val threadSize = threads.size

    GmailScrapeProgress.updateTotalThreads(progress.gmailScrapeProgressId, Some(threadSize))

    println(threadSize + " threads...")

    val introductions: Set[IntroductionsRow] = Await.result(Introduction.introductionsByReceiver(myEmail), Duration.Inf).toSet

    println(introductions.size + " introductions...")

    var knownEmails = scala.collection.mutable.Buffer(introductions.map(_.introPersonEmail).toSeq:_*)

    threads.drop(toDrop).zipWithIndex.map{case (t, i) => {

      val threadIndex = i+toDrop+1

      println("thread "+(threadIndex) +" / "+threadSize)
      val thread = getOneThread(service, myEmail, t.getId)

      val messages = thread.getMessages.toList

      val intros = messages.flatMap(message => {

        val headers = message.getPayload.getHeaders

        val from = userHeaderValueByName(headers, "From")
        val to = userHeaderValueByName(headers, "To")
        val cc = userHeaderValueByName(headers, "cc")

        val newPersons = (from ++ to ++ cc).distinct diff knownEmails.toSeq

        val fromTry = try {

          val userHeaderValue = userHeaderValueByName(headers, "From")

          Some(userHeaderValue.head)
        } catch {
          case _ => {
            println("FROM ERROR")
            None
          }
        }

        val dateString = headerValueByName(message.getPayload().getHeaders(), "Date")
        val date = dateString.flatMap(d => DateParserUtil.dateParse(d, true))

        if(date.isDefined && fromTry.isDefined && newPersons.size <= 10) {
          val from = fromTry.get

          print(newPersons.size + " new persons...")
          val introsToMake = newPersons.flatMap(p => {
            if (p != myEmail && countNumbersInString(p) <= 8) {

              knownEmails +:= p

              if (p != from) {
                Some(IntroductionsRow(null, from, myEmail, p, date.get.getMillis))
              } else {
                Some(IntroductionsRow(null, myEmail, myEmail, p, date.get.getMillis))
              }
            } else {
              None
            }
          })

          introsToMake
        } else {

          println(message)
          println("DATE: "+date.isDefined)
          println("FROM: "+fromTry.isDefined)
          println("SIZE: "+newPersons.size)
          Nil
        }
      })

      val saved = Await.result(Introduction.createMany(intros), Duration.Inf)

      println("saved "+saved.size+" intros...")

      if(threadIndex % 7 == 0 || threadIndex % 19 == 0) {
        Await.result(GmailScrapeProgress.updateThreadCount(progress.gmailScrapeProgressId, threadIndex), Duration.Inf)
      }

      //knownEmails ++= saved.map(_.introPersonEmail)
      saved
    }}

    Await.result(GmailScrapeProgress.updateThreadCount(progress.gmailScrapeProgressId, threadSize), Duration.Inf)
    Await.result(GmailScrapeProgress.updateStatus(progress.gmailScrapeProgressId, GmailScrapeProgress.STATUS_STOPPED), Duration.Inf)
  }

}

object DateParserUtil {
  val df = DateTimeFormat.forPattern("EEE dd MMM yyyy HH:mm:ss Z")
  val df1 = DateTimeFormat.forPattern("EEE dd MMM yyyy HH:mm:ss z")
  val df2 = DateTimeFormat.forPattern("EEE dd MMM yyyy HH:mm:ss Z (z)")
  val df3 = DateTimeFormat.forPattern("EEE dd MMM yyyy HH:mm:ss Z (zZ)")
  val df4 = DateTimeFormat.forPattern("d MMM yyyy HH:mm:ss Z")
  val df5 = DateTimeFormat.forPattern("EEE d MMM yyyy HH:mm:ss Z ...")
  val df6 = DateTimeFormat.forPattern("EEE MMM dd yyyy HH:mm:ss Z")

  "Thu Mar 16 2017 23:45:02 -0400"

  def dateParse(da: String, firstPass: Boolean = true): Option[DateTime] = {
    val d = da.replaceAll(" +", " ").replaceAll(",", "")
    try {
      Some(DateTime.parse(d, df))
    } catch {
      case _ => try {
        Some(DateTime.parse(d, df1))
      } catch {
        case _ => try {
          Some(DateTime.parse(d, df2))
        } catch {
          case _ => try {
            Some(DateTime.parse(d, df3))
          } catch{
            case _ => try {
              Some(DateTime.parse(d, df4))
            } catch {
              case _ => try {
                Some(DateTime.parse(d, df5))
              } catch {
                case _ => try {
                  Some(DateTime.parse(d, df6))
                } catch {
                  case _ => {
                    if(firstPass) {
                      println("trying another pass at date...")
                      val tryWithoutEnd = d.split(" \\(").head
                      dateParse(tryWithoutEnd, false)
                    } else {
                      throw new ParseException("COULD NOT PARSE: "+d, new Exception())
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
