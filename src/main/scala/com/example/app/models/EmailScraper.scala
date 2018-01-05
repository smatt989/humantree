package com.example.app.models

import com.example.app.db.Tables.IntroductionsRow
import com.google.api.services.gmail.{Gmail, GmailScopes}
import com.google.api.services.gmail.model.{Message, MessagePartHeader, Thread => GThread}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import akka.actor.{Actor, ActorSystem, Props}
import com.example.app.EmailScrapeRequestObject

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

  def emailFromUserString(userString: String) = {
    val regex = "(?<=<).*(?=>)".r
    val trySecondPart = regex.findFirstIn(userString)

    trySecondPart.getOrElse(userString).toLowerCase
  }

  def personFromUserString(userString: String) = {
    val email = emailFromUserString(userString)
    val name = userString.split("<").head.trim
    //PersonsRow(null, email, name)
  }

  //now just returns email addresses
  def userHeaderValueByName(headers: Seq[MessagePartHeader], key: String) = {
    val output = headerValueByName(headers, key)
    if(output.isDefined)
      output.get.split(",").toSeq.flatMap(a => bracketRegex.findFirstIn(a)).map(_.toLowerCase)
      //bracketRegex.findAllIn(output.get).toArray.toSeq
      //emailRegex.findAllIn(output.get).toArray.toSeq
      //output.get.split(",").toSeq.map(_.trim)
    else
      Nil
  }

  def parseUsersFromMessage(message: Message) = {
    val headers = message.getPayload.getHeaders

    println(headers)

    val from = userHeaderValueByName(headers, "From")
    val to = userHeaderValueByName(headers, "To")
    val cc = userHeaderValueByName(headers, "cc")

    (from ++ to ++ cc).toSet
  }

  def parseUsers(thread: GThread) = {
    val messages = thread.getMessages.toList

    messages.flatMap(message => {
      parseUsersFromMessage(message)
    }).toSet
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

    //TODO: OK -- ONLY LOOKS AT CURRENT PARSING EMAIL, SHOULD NOT INTERFERE WITH MULTIPLE ACCOUNTS
    val introductions: Set[IntroductionsRow] = Await.result(Introduction.introductionsByReceiver(myEmail), Duration.Inf).toSet

    println(introductions.size + " introductions...")

    var knownEmails = scala.collection.mutable.Buffer(introductions.map(_.introPersonEmail).toSeq:_*)

    val df = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z")
    val df1 = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss z")
    val df2 = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z (z)")
    val df3 = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z (zZ)")
    val df4 = DateTimeFormat.forPattern("d MMM yyyy HH:mm:ss Z")
    val df5 = DateTimeFormat.forPattern("EEE, d MMM yyyy HH:mm:ss Z ...")

    def dateParse(d: String) = {
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
                  case _ => None
                }
              }
            }
          }
        }
      }
    }

    threads.drop(toDrop).zipWithIndex.map{case (t, i) => {

      val threadIndex = i+toDrop+1

      println("thread "+(threadIndex) +" / "+threadSize)
      val thread = getOneThread(service, myEmail, t.getId)

      val messages = thread.getMessages.toList

      val intros = messages.flatMap(message => {
        val messageUsers = parseUsersFromMessage(message)
        //val personsByEmail = messageUsers.map(m => emailFromUserString(m))

        val newPersons = messageUsers diff knownEmails.toSet

        val fromTry = try {

          val userHeaderValue = userHeaderValueByName(message.getPayload.getHeaders, "From")
          //val em = emailFromUserString(userHeaderValue.head)
          //println(userHeaderValue + " --> "+em+" ("+userHeaderValue.size+")")

          //Some(em)
          Some(userHeaderValue.head)
        } catch {
          case _ => {
            println(message)
            None
          }
        }
        val dateString = headerValueByName(message.getPayload().getHeaders(), "Date")

        val date = dateString.flatMap(dateParse)

        if(date.isDefined && fromTry.isDefined && newPersons.size <= 10) {
          val from = fromTry.get

          print(newPersons.size + " new persons...")
          //MUST FIGURE OUT THE WHOLE IDENTITY THING... COULD CONCEIVABLY SKIP AND JUST USE EMAIL ADDRESSES...
          val introsToMake = newPersons.flatMap(p => {
            if (p != myEmail && countNumbersInString(p) <= 8) {
              //knownEmails = knownEmails +: p

              knownEmails +:= p

              if (p != from) {
                Some(IntroductionsRow(null, from, myEmail, p, date.get.getMillis))
              } else {
                Some(IntroductionsRow(null, myEmail, myEmail, p, date.get.getMillis))
              }
            } else
              None
          })



          introsToMake
        } else {
          Nil
        }
      })

      val saved = Await.result(Introduction.createMany(intros), Duration.Inf)

      println("saved "+saved.size+" intros...")

      if(threadIndex % 7 == 0 || threadIndex % 19 == 0 || threadIndex == threadSize) {
        GmailScrapeProgress.updateThreadCount(progress.gmailScrapeProgressId, threadIndex)
      }

      //knownEmails ++= saved.map(_.introPersonEmail)
      saved
    }}

    GmailScrapeProgress.updateStatus(progress.gmailScrapeProgressId, GmailScrapeProgress.STATUS_STOPPED)
  }

}
