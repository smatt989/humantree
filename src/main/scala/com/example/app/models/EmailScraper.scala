package com.example.app.models

import java.util.concurrent.TimeUnit

import com.example.app.db.Tables.{InteractionsRow, IntroductionsRow, ScraperActorsRow}
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.{MessagePartHeader, Thread => GThread}
import org.joda.time.{DateTime, DateTimeConstants}
import org.joda.time.format.DateTimeFormat
import akka.actor.{Actor, ActorSystem, PoisonPill, Props}
import akka.util.Timeout
import com.example.app.{EmailScrapeRequestObject, MailJetSender}
import org.json4s.ParserUtil.ParseException

import collection.JavaConversions._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class EmailActor extends Actor {

  def receive = {
    case a: EmailScrapeRequestObject =>
      Await.result(ScraperActor.terminateUnfinishedForUserIdEmail(a.email, a.appUserId), Duration.Inf)
      EmailScraper.declareActor(a.email, a.appUserId)
      println("scraping...")

      EmailScraper.basicProcess(a.email, a.appUserId, a.startAt)
      EmailScraper.finishActor(a.email, a.appUserId)
      println("donezo")
      killSelf()
    case a: KillActorRequest =>
      killSelf()
  }

  def killSelf() = {
    println("killing actor...")
    self ! PoisonPill
    //context.stop(self)
  }
}

class NotificationActor extends Actor {

  def receive = {
    case "go" =>
      EmailScraper.weeklyNotification
  }
}

class ActorJanitor extends Actor {

  def receive = {
    case "patrol" =>
      EmailScraper.checkStopped()
    case "update" =>
      EmailScraper.checkDue()
    case "new" =>
      EmailScraper.checkNew()
  }
}

object EmailScraper {
  implicit val timeout = Timeout(FiniteDuration(1, TimeUnit.SECONDS))

  val system = ActorSystem()

  val guardian = system.actorOf(Props[ActorJanitor])

  val weeklyNotifier = system.actorOf(Props[NotificationActor])

  val updateEveryMillis = 24 * 60 * 60 * 1000
  val stuckMillis = 1 * 60 * 1000

  def startupResponseRequestCreator() = {
    system.scheduler.schedule(0 milliseconds, 10 seconds, guardian, "new")
    system.scheduler.schedule(0 milliseconds, 10 seconds, guardian, "patrol")
    system.scheduler.schedule(0 milliseconds, 1 hour, guardian, "update")

    system.scheduler.schedule(0 milliseconds, 1 hour, weeklyNotifier, "go")
  }

  def checkNew() = {
    val newAccounts = Await.result(ScraperActor.unscraped, Duration.Inf)

    newAccounts.map(a => startAnActor(a.email, a.userId))
  }

  def checkStopped() = {
    val stopped = Await.result(ScraperActor.unfinishedNotUpdatedInOver(stuckMillis), Duration.Inf)
    stopped.groupBy(a => (a.email, a.userId)).foreach{case (pair, actors) =>
      actors.foreach(a => {
        killActor(a)
      })
      //TODO: EEEK SHOULD REALLY USE A WATCHER HERE
      Thread.sleep(5000)
      startAnActor(pair._1, pair._2)
    }
  }

  def checkDue() = {
    val dueMillis = updateEveryMillis
    val due = ScraperActor.finishedLongAgo(dueMillis)
    due.foreach(s => {
      startAnActor(s.email, s.userId)
    })
  }

  def weeklyNotification = {
    val now = DateTime.now()

    if(now.dayOfWeek().get() == DateTimeConstants.MONDAY && now.hourOfDay().get() == 17) {
      println("sending weekly emails...")

      val lastWeek = now.minusDays(7).getMillis

      val users = Await.result(User.getAll, Duration.Inf)


      users.foreach(user => {
        val introductions = Insights.introductions(lastWeek, user.userAccountId).sortBy(_.dateMillis)
        val connectors = Insights.connectors(lastWeek, user.userAccountId).sortBy(_.introductions).reverse.take(3)
        val coolingOff = scala.util.Random.shuffle(Insights.coolingOff(user.userAccountId)).take(3)

        if(introductions.size > 0 || connectors.size > 0 || coolingOff.size > 0) {
          val body = weeklyNotificationEmailBody(introductions, connectors, coolingOff)

          MailJetSender.sendEmail("Treople Weekly Update", body, user.email)
        }
      })
    }
  }

  def weeklyNotificationEmailBody(introductions: Seq[IntroductionJson], connectors: Seq[ConnectorSummary], coolingOff: Seq[CoolingInteraction]) = {

    val line1 = Some("<h2>Here's some information about what's been going on in your inbox this past week.</h2>")

    val line2 = if(introductions.size > 0){
      Some(
        "<h3>You made some new connections:</h3>" + introductions.map(s => "<p>"+s.intro+s.sender.map(s => " (via "+s+")").getOrElse("")+"</p>").mkString(" ")
      )
    } else
      None

    val line3 = if(connectors.size > 0) {
      Some(
        "<h3>Your biggest connectors this week:</h3>" + connectors.map(s => "<p>"+s.name+"("+s.introductions+")"+"</p>").mkString(" ")
      )
    } else
      None

    val line4 = if(coolingOff.size > 0) {
      Some(
        "<h3>Some contacts you haven't emailed in a while:</h3>"+coolingOff.map(s => "<p>"+s.email+"</p>").mkString(" ")
      )
    } else
      None

    val line5 = Some("<a href="+MailJetSender.DOMAIN+"#/tree"+">View Treople now</a>")

    val body = Seq(line1, line2, line3, line4, line5).flatten.mkString("<br />")

    body
  }

  def nameActor(a: ScraperActorsRow): String = {
    nameActor(a.email, a.userId)
  }

  def nameActor(email: String, userId: Int): String = {
    userId+"$"+email
  }

  def killActor(a: ScraperActorsRow) = {
    val name = nameActor(a)
    try {
      Await.result(system.actorSelection("user/" + name).resolveOne(), Duration.Inf) ! KillActorRequest(a.email, a.userId)
    } catch {
      case _ => Unit
    }
  }

  def declareActor(email: String, userId: Int) = {
    val now = DateTime.now().getMillis
    val newActor = ScraperActorsRow(null, userId, email, now, None, now)
    Await.result(ScraperActor.create(newActor), Duration.Inf)
  }

  def finishActor(email: String, userId: Int) = {
    val actor = Await.result(ScraperActor.byEmailAndUserId(email, userId), Duration.Inf)
    actor.map(a => {
      val now = DateTime.now().getMillis
      val toUpdate = a.copy(finishedMillis = Some(now))
      Await.result(ScraperActor.updateOne(toUpdate), Duration.Inf)
    })
  }

  def updateActor(email: String, userId: Int) = {
    val actor = Await.result(ScraperActor.byEmailAndUserId(email, userId), Duration.Inf)
    actor.map(a => {
      val now = DateTime.now().getMillis
      val toUpdate = a.copy(updatedMillis = now)
      Await.result(ScraperActor.updateOne(toUpdate), Duration.Inf)
    })
  }

  def startAnActor(email: String, appUserId: Int, startAt: Option[Int] = None) = {
    val request = EmailScrapeRequestObject(email, appUserId, startAt)

    try {
      val myActor = system.actorOf(Props[EmailActor], EmailScraper.nameActor(email, appUserId))

      myActor ! request
    } catch {
      case _ =>
        println("did not create an actor -- already running")
        Unit
    }
  }

  def threadsByLabels(email: String, appUserId: Int, service: Gmail, userId: String, labels: Seq[String], amountToFetch: Int, threads: Seq[GThread] = Nil, pageToken: Option[String] = None, attempt: Int = 0, p: Int = 0): Seq[GThread] = {
    try {
      updateActor(email, appUserId)
      val response = if (pageToken.isEmpty)
        service.users().threads().list(userId).setLabelIds(labels).setMaxResults(100L).execute()
      else
        service.users().threads().list(userId).setLabelIds(labels).setPageToken(pageToken.get).setMaxResults(100L).execute()

      val theseThreads: Seq[GThread] = response.getThreads.toList

/*      val sampleThread = theseThreads.head

      val lastThread = theseThreads.last

      val thread = getOneThread(service, email, sampleThread.getId)
      val lThread = getOneThread(service, email, lastThread.getId)

      val message = thread.getMessages.toList.head
      val lastMessage = lThread.getMessages.toList.head

      val dateString = headerValueByName(message.getPayload.getHeaders, "Date")
      val lastDate = headerValueByName(lastMessage.getPayload.getHeaders, "Date")


      println("threads: "+theseThreads.size+" page: "+p+" date: "+dateString+" to: "+lastDate)*/

      val totalThreads = threads ++ theseThreads

      if (response.getThreads != null && response.getNextPageToken != null && totalThreads.size < amountToFetch)
        threadsByLabels(email, appUserId, service, userId, labels, amountToFetch, totalThreads, Some(response.getNextPageToken), 0, p + 1)
      else
        totalThreads

    } catch {
      case _ =>
        if (attempt < 3) {
          Thread.sleep(5000)
          threadsByLabels(email, appUserId, service, userId, labels, amountToFetch, threads, pageToken, attempt + 1, p)
        } else {
          throw new Exception("could not get threads")
        }
    }
  }

  def getOneThread(service: Gmail, userId: String, threadId: String, attempt: Int = 0): GThread = {
    try {
      service.users().threads().get(userId, threadId).execute()
    } catch {
      case _ =>
        if (attempt < 3) {
          Thread.sleep(5000)
          getOneThread(service, userId, threadId, attempt + 1)
        } else {
          throw new Exception("failed to get thread")
        }
    }
  }

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

    val introductions: Set[IntroductionsRow] = Await.result(Introduction.introductionsByReceiver(myEmail), Duration.Inf).toSet

    val lastIntroduction = if(introductions.size > 0)
      Some(introductions.map(_.introTimeMillis).max)
    else
      None

    println("label query")

    val labelInfo = service.users().labels().get(myEmail, "SENT").execute()
    val totalThreads = labelInfo.getThreadsTotal.toInt

    println("total threads: "+totalThreads)

    val toFindThreads = totalThreads - toDrop

    val threads = threadsByLabels(myEmail, appUserId, service, myEmail, Seq("SENT"), toFindThreads).reverse
    val threadsFetched = threads.size

    GmailScrapeProgress.updateTotalThreads(progress.gmailScrapeProgressId, Some(totalThreads))

    println(introductions.size + " introductions...")

    //var knownEmails = scala.collection.mutable.Buffer(introductions.map(_.introPersonEmail).toSeq:_*)
    val emailByStartDate = scala.collection.mutable.Map[String, Long](introductions.map(a => a.introPersonEmail -> a.introTimeMillis).toSeq:_*)

    threads.zipWithIndex.map{case (t, i) =>

      val threadIndex = i+ (totalThreads - threadsFetched) +1

      println("thread "+threadIndex +" / "+totalThreads)
      val thread = getOneThread(service, myEmail, t.getId)

      val messages = thread.getMessages.toList

      val yikes = messages.map(message => {

        val headers = message.getPayload.getHeaders

        val from = userHeaderValueByName(headers, "From")
        val to = userHeaderValueByName(headers, "To")
        val cc = userHeaderValueByName(headers, "cc")

        val totalPeople = (from ++ to ++ cc).distinct

        //val newPersons = totalPeople.distinct diff knownEmails

        val fromTry = try {

          val userHeaderValue = userHeaderValueByName(headers, "From")

          Some(userHeaderValue.head)
        } catch {
          case _ =>
            println("FROM ERROR")
            None
        }

        val dateString = headerValueByName(message.getPayload.getHeaders, "Date")
        val date = dateString.flatMap(d => DateParserUtil.dateParse(d, firstPass = true))

        if(date.isDefined && fromTry.isDefined && totalPeople.size <= 10) {
          val from = fromTry.get

          print(totalPeople.size + " people...")
          val introsToMake = totalPeople.flatMap(p => {
            val previousDate = emailByStartDate.get(p)

            val thisDate = date.get.getMillis
            if (p != myEmail && countNumbersInString(p) <= 8 && (previousDate.isEmpty || previousDate.get > thisDate)) {

              //knownEmails +:= p
              emailByStartDate ++= Map(p -> thisDate)

              if (p != from) {
                Some(IntroductionsRow(null, from, myEmail, p, thisDate))
              } else {
                Some(IntroductionsRow(null, myEmail, myEmail, p, thisDate))
              }
            } else {
              None
            }
          })

          val interactions = totalPeople.filter(_ != myEmail).map(p => {
            InteractionsRow(null, myEmail, p, date.get.getMillis)
          })

          (introsToMake, interactions)
        } else {

          println(message)
          println("DATE: "+date.isDefined)
          println("FROM: "+fromTry.isDefined)
          println("SIZE: "+totalPeople.size)
          (Nil, Nil)
        }
      })

      val intros = yikes.flatMap(_._1)
      val interactions = yikes.flatMap(_._2)

      val saved = Await.result(Introduction.createMany(intros), Duration.Inf)
      val savedInteractions = Await.result(Interaction.createMany(interactions), Duration.Inf)

      println("saved "+saved.size+" intros...")
      println("saved "+ savedInteractions.size+" interactions...")

      if(threadIndex % 7 == 0 || threadIndex % 19 == 0) {
        Await.result(GmailScrapeProgress.updateThreadCount(progress.gmailScrapeProgressId, threadIndex), Duration.Inf)
        updateActor(myEmail, appUserId)
      }

      //knownEmails ++= saved.map(_.introPersonEmail)
      saved
    }

    Await.result(GmailScrapeProgress.updateThreadCount(progress.gmailScrapeProgressId, totalThreads), Duration.Inf)
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

  //"Thu Mar 16 2017 23:45:02 -0400"
  //"Tue 15 Nov 2005 20:44:36 +0000 GMT"

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
                      println("COULD NOT PARSE: "+d)
                      None
                      //throw new ParseException("COULD NOT PARSE: "+d, new Exception())
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
