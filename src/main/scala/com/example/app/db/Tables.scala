package com.example.app.db
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.driver.PostgresDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(DeviceTokens.schema, GmailAccessTokens.schema, GmailScrapeProgresses.schema, IdentityLinks.schema, Introductions.schema, Migrations.schema, NodeAnnotations.schema, ScraperActors.schema, ShareableContexts.schema, UserAccounts.schema, UserConnections.schema, UserSessions.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table DeviceTokens
   *  @param deviceTokenId Database column DEVICE_TOKEN_ID SqlType(INTEGER), AutoInc, PrimaryKey
   *  @param userId Database column USER_ID SqlType(INTEGER)
   *  @param deviceToken Database column DEVICE_TOKEN SqlType(VARCHAR), Default(None) */
  case class DeviceTokensRow(deviceTokenId: Int, userId: Int, deviceToken: Option[String] = None)
  /** GetResult implicit for fetching DeviceTokensRow objects using plain SQL queries */
  implicit def GetResultDeviceTokensRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[DeviceTokensRow] = GR{
    prs => import prs._
    DeviceTokensRow.tupled((<<[Int], <<[Int], <<?[String]))
  }
  /** Table description of table DEVICE_TOKENS. Objects of this class serve as prototypes for rows in queries. */
  class DeviceTokens(_tableTag: Tag) extends Table[DeviceTokensRow](_tableTag, Some("BEE"), "DEVICE_TOKENS") {
    def * = (deviceTokenId, userId, deviceToken) <> (DeviceTokensRow.tupled, DeviceTokensRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(deviceTokenId), Rep.Some(userId), deviceToken).shaped.<>({r=>import r._; _1.map(_=> DeviceTokensRow.tupled((_1.get, _2.get, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column DEVICE_TOKEN_ID SqlType(INTEGER), AutoInc, PrimaryKey */
    val deviceTokenId: Rep[Int] = column[Int]("DEVICE_TOKEN_ID", O.AutoInc, O.PrimaryKey)
    /** Database column USER_ID SqlType(INTEGER) */
    val userId: Rep[Int] = column[Int]("USER_ID")
    /** Database column DEVICE_TOKEN SqlType(VARCHAR), Default(None) */
    val deviceToken: Rep[Option[String]] = column[Option[String]]("DEVICE_TOKEN", O.Default(None))

    /** Foreign key referencing UserAccounts (database name DEVICE_TOKENS_TO_USER_FK) */
    lazy val userAccountsFk = foreignKey("DEVICE_TOKENS_TO_USER_FK", userId, UserAccounts)(r => r.userAccountId, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  }
  /** Collection-like TableQuery object for table DeviceTokens */
  lazy val DeviceTokens = new TableQuery(tag => new DeviceTokens(tag))

  /** Entity class storing rows of table GmailAccessTokens
   *  @param gmailAccesTokenId Database column GMAIL_ACCES_TOKEN_ID SqlType(VARCHAR), PrimaryKey
   *  @param userId Database column USER_ID SqlType(INTEGER)
   *  @param email Database column EMAIL SqlType(VARCHAR)
   *  @param accessToken Database column ACCESS_TOKEN SqlType(VARCHAR) */
  case class GmailAccessTokensRow(gmailAccesTokenId: String, userId: Int, email: String, accessToken: String)
  /** GetResult implicit for fetching GmailAccessTokensRow objects using plain SQL queries */
  implicit def GetResultGmailAccessTokensRow(implicit e0: GR[String], e1: GR[Int]): GR[GmailAccessTokensRow] = GR{
    prs => import prs._
    GmailAccessTokensRow.tupled((<<[String], <<[Int], <<[String], <<[String]))
  }
  /** Table description of table GMAIL_ACCESS_TOKENS. Objects of this class serve as prototypes for rows in queries. */
  class GmailAccessTokens(_tableTag: Tag) extends Table[GmailAccessTokensRow](_tableTag, Some("BEE"), "GMAIL_ACCESS_TOKENS") {
    def * = (gmailAccesTokenId, userId, email, accessToken) <> (GmailAccessTokensRow.tupled, GmailAccessTokensRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(gmailAccesTokenId), Rep.Some(userId), Rep.Some(email), Rep.Some(accessToken)).shaped.<>({r=>import r._; _1.map(_=> GmailAccessTokensRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column GMAIL_ACCES_TOKEN_ID SqlType(VARCHAR), PrimaryKey */
    val gmailAccesTokenId: Rep[String] = column[String]("GMAIL_ACCES_TOKEN_ID", O.PrimaryKey)
    /** Database column USER_ID SqlType(INTEGER) */
    val userId: Rep[Int] = column[Int]("USER_ID")
    /** Database column EMAIL SqlType(VARCHAR) */
    val email: Rep[String] = column[String]("EMAIL")
    /** Database column ACCESS_TOKEN SqlType(VARCHAR) */
    val accessToken: Rep[String] = column[String]("ACCESS_TOKEN")

    /** Foreign key referencing UserAccounts (database name GMAIL_ACCES_TOKEN_TO_USERS_FK) */
    lazy val userAccountsFk = foreignKey("GMAIL_ACCES_TOKEN_TO_USERS_FK", userId, UserAccounts)(r => r.userAccountId, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  }
  /** Collection-like TableQuery object for table GmailAccessTokens */
  lazy val GmailAccessTokens = new TableQuery(tag => new GmailAccessTokens(tag))

  /** Entity class storing rows of table GmailScrapeProgresses
   *  @param gmailScrapeProgressId Database column GMAIL_SCRAPE_PROGRESS_ID SqlType(VARCHAR), PrimaryKey
   *  @param gmailAccessTokenId Database column GMAIL_ACCESS_TOKEN_ID SqlType(VARCHAR)
   *  @param totalThreads Database column TOTAL_THREADS SqlType(INTEGER), Default(None)
   *  @param threadsProcessed Database column THREADS_PROCESSED SqlType(INTEGER)
   *  @param status Database column STATUS SqlType(VARCHAR)
   *  @param lastPulledMillis Database column LAST_PULLED_MILLIS SqlType(BIGINT) */
  case class GmailScrapeProgressesRow(gmailScrapeProgressId: String, gmailAccessTokenId: String, totalThreads: Option[Int] = None, threadsProcessed: Int, status: String, lastPulledMillis: Long)
  /** GetResult implicit for fetching GmailScrapeProgressesRow objects using plain SQL queries */
  implicit def GetResultGmailScrapeProgressesRow(implicit e0: GR[String], e1: GR[Option[Int]], e2: GR[Int], e3: GR[Long]): GR[GmailScrapeProgressesRow] = GR{
    prs => import prs._
    GmailScrapeProgressesRow.tupled((<<[String], <<[String], <<?[Int], <<[Int], <<[String], <<[Long]))
  }
  /** Table description of table GMAIL_SCRAPE_PROGRESSES. Objects of this class serve as prototypes for rows in queries. */
  class GmailScrapeProgresses(_tableTag: Tag) extends Table[GmailScrapeProgressesRow](_tableTag, Some("BEE"), "GMAIL_SCRAPE_PROGRESSES") {
    def * = (gmailScrapeProgressId, gmailAccessTokenId, totalThreads, threadsProcessed, status, lastPulledMillis) <> (GmailScrapeProgressesRow.tupled, GmailScrapeProgressesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(gmailScrapeProgressId), Rep.Some(gmailAccessTokenId), totalThreads, Rep.Some(threadsProcessed), Rep.Some(status), Rep.Some(lastPulledMillis)).shaped.<>({r=>import r._; _1.map(_=> GmailScrapeProgressesRow.tupled((_1.get, _2.get, _3, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column GMAIL_SCRAPE_PROGRESS_ID SqlType(VARCHAR), PrimaryKey */
    val gmailScrapeProgressId: Rep[String] = column[String]("GMAIL_SCRAPE_PROGRESS_ID", O.PrimaryKey)
    /** Database column GMAIL_ACCESS_TOKEN_ID SqlType(VARCHAR) */
    val gmailAccessTokenId: Rep[String] = column[String]("GMAIL_ACCESS_TOKEN_ID")
    /** Database column TOTAL_THREADS SqlType(INTEGER), Default(None) */
    val totalThreads: Rep[Option[Int]] = column[Option[Int]]("TOTAL_THREADS", O.Default(None))
    /** Database column THREADS_PROCESSED SqlType(INTEGER) */
    val threadsProcessed: Rep[Int] = column[Int]("THREADS_PROCESSED")
    /** Database column STATUS SqlType(VARCHAR) */
    val status: Rep[String] = column[String]("STATUS")
    /** Database column LAST_PULLED_MILLIS SqlType(BIGINT) */
    val lastPulledMillis: Rep[Long] = column[Long]("LAST_PULLED_MILLIS")

    /** Foreign key referencing GmailAccessTokens (database name GMAIL_SCRAPE_PROGRESS_TO_GMAIL_ACCESS_TOKEN_FK) */
    lazy val gmailAccessTokensFk = foreignKey("GMAIL_SCRAPE_PROGRESS_TO_GMAIL_ACCESS_TOKEN_FK", gmailAccessTokenId, GmailAccessTokens)(r => r.gmailAccesTokenId, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  }
  /** Collection-like TableQuery object for table GmailScrapeProgresses */
  lazy val GmailScrapeProgresses = new TableQuery(tag => new GmailScrapeProgresses(tag))

  /** Entity class storing rows of table IdentityLinks
   *  @param identityLinkId Database column IDENTITY_LINK_ID SqlType(VARCHAR), PrimaryKey
   *  @param userId Database column USER_ID SqlType(INTEGER)
   *  @param leftSide Database column LEFT_SIDE SqlType(VARCHAR)
   *  @param rightSide Database column RIGHT_SIDE SqlType(VARCHAR)
   *  @param linkName Database column LINK_NAME SqlType(VARCHAR), Default(None) */
  case class IdentityLinksRow(identityLinkId: String, userId: Int, leftSide: String, rightSide: String, linkName: Option[String] = None)
  /** GetResult implicit for fetching IdentityLinksRow objects using plain SQL queries */
  implicit def GetResultIdentityLinksRow(implicit e0: GR[String], e1: GR[Int], e2: GR[Option[String]]): GR[IdentityLinksRow] = GR{
    prs => import prs._
    IdentityLinksRow.tupled((<<[String], <<[Int], <<[String], <<[String], <<?[String]))
  }
  /** Table description of table IDENTITY_LINKS. Objects of this class serve as prototypes for rows in queries. */
  class IdentityLinks(_tableTag: Tag) extends Table[IdentityLinksRow](_tableTag, Some("BEE"), "IDENTITY_LINKS") {
    def * = (identityLinkId, userId, leftSide, rightSide, linkName) <> (IdentityLinksRow.tupled, IdentityLinksRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(identityLinkId), Rep.Some(userId), Rep.Some(leftSide), Rep.Some(rightSide), linkName).shaped.<>({r=>import r._; _1.map(_=> IdentityLinksRow.tupled((_1.get, _2.get, _3.get, _4.get, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column IDENTITY_LINK_ID SqlType(VARCHAR), PrimaryKey */
    val identityLinkId: Rep[String] = column[String]("IDENTITY_LINK_ID", O.PrimaryKey)
    /** Database column USER_ID SqlType(INTEGER) */
    val userId: Rep[Int] = column[Int]("USER_ID")
    /** Database column LEFT_SIDE SqlType(VARCHAR) */
    val leftSide: Rep[String] = column[String]("LEFT_SIDE")
    /** Database column RIGHT_SIDE SqlType(VARCHAR) */
    val rightSide: Rep[String] = column[String]("RIGHT_SIDE")
    /** Database column LINK_NAME SqlType(VARCHAR), Default(None) */
    val linkName: Rep[Option[String]] = column[Option[String]]("LINK_NAME", O.Default(None))

    /** Foreign key referencing UserAccounts (database name IDENTITY_LINK_TO_USERS_FK) */
    lazy val userAccountsFk = foreignKey("IDENTITY_LINK_TO_USERS_FK", userId, UserAccounts)(r => r.userAccountId, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  }
  /** Collection-like TableQuery object for table IdentityLinks */
  lazy val IdentityLinks = new TableQuery(tag => new IdentityLinks(tag))

  /** Entity class storing rows of table Introductions
   *  @param introductionId Database column INTRODUCTION_ID SqlType(VARCHAR), PrimaryKey
   *  @param senderPersonEmail Database column SENDER_PERSON_EMAIL SqlType(VARCHAR)
   *  @param receiverPersonEmail Database column RECEIVER_PERSON_EMAIL SqlType(VARCHAR)
   *  @param introPersonEmail Database column INTRO_PERSON_EMAIL SqlType(VARCHAR)
   *  @param introTimeMillis Database column INTRO_TIME_MILLIS SqlType(BIGINT) */
  case class IntroductionsRow(introductionId: String, senderPersonEmail: String, receiverPersonEmail: String, introPersonEmail: String, introTimeMillis: Long)
  /** GetResult implicit for fetching IntroductionsRow objects using plain SQL queries */
  implicit def GetResultIntroductionsRow(implicit e0: GR[String], e1: GR[Long]): GR[IntroductionsRow] = GR{
    prs => import prs._
    IntroductionsRow.tupled((<<[String], <<[String], <<[String], <<[String], <<[Long]))
  }
  /** Table description of table INTRODUCTIONS. Objects of this class serve as prototypes for rows in queries. */
  class Introductions(_tableTag: Tag) extends Table[IntroductionsRow](_tableTag, Some("BEE"), "INTRODUCTIONS") {
    def * = (introductionId, senderPersonEmail, receiverPersonEmail, introPersonEmail, introTimeMillis) <> (IntroductionsRow.tupled, IntroductionsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(introductionId), Rep.Some(senderPersonEmail), Rep.Some(receiverPersonEmail), Rep.Some(introPersonEmail), Rep.Some(introTimeMillis)).shaped.<>({r=>import r._; _1.map(_=> IntroductionsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column INTRODUCTION_ID SqlType(VARCHAR), PrimaryKey */
    val introductionId: Rep[String] = column[String]("INTRODUCTION_ID", O.PrimaryKey)
    /** Database column SENDER_PERSON_EMAIL SqlType(VARCHAR) */
    val senderPersonEmail: Rep[String] = column[String]("SENDER_PERSON_EMAIL")
    /** Database column RECEIVER_PERSON_EMAIL SqlType(VARCHAR) */
    val receiverPersonEmail: Rep[String] = column[String]("RECEIVER_PERSON_EMAIL")
    /** Database column INTRO_PERSON_EMAIL SqlType(VARCHAR) */
    val introPersonEmail: Rep[String] = column[String]("INTRO_PERSON_EMAIL")
    /** Database column INTRO_TIME_MILLIS SqlType(BIGINT) */
    val introTimeMillis: Rep[Long] = column[Long]("INTRO_TIME_MILLIS")
  }
  /** Collection-like TableQuery object for table Introductions */
  lazy val Introductions = new TableQuery(tag => new Introductions(tag))

  /** Entity class storing rows of table Migrations
   *  @param migrationId Database column MIGRATION_ID SqlType(INTEGER), PrimaryKey */
  case class MigrationsRow(migrationId: Int)
  /** GetResult implicit for fetching MigrationsRow objects using plain SQL queries */
  implicit def GetResultMigrationsRow(implicit e0: GR[Int]): GR[MigrationsRow] = GR{
    prs => import prs._
    MigrationsRow(<<[Int])
  }
  /** Table description of table MIGRATIONS. Objects of this class serve as prototypes for rows in queries. */
  class Migrations(_tableTag: Tag) extends Table[MigrationsRow](_tableTag, Some("BEE"), "MIGRATIONS") {
    def * = migrationId <> (MigrationsRow, MigrationsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = Rep.Some(migrationId).shaped.<>(r => r.map(_=> MigrationsRow(r.get)), (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column MIGRATION_ID SqlType(INTEGER), PrimaryKey */
    val migrationId: Rep[Int] = column[Int]("MIGRATION_ID", O.PrimaryKey)
  }
  /** Collection-like TableQuery object for table Migrations */
  lazy val Migrations = new TableQuery(tag => new Migrations(tag))

  /** Entity class storing rows of table NodeAnnotations
   *  @param nodeAnnotationId Database column NODE_ANNOTATION_ID SqlType(VARCHAR), PrimaryKey
   *  @param userId Database column USER_ID SqlType(INTEGER)
   *  @param nodeName Database column NODE_NAME SqlType(VARCHAR)
   *  @param annotation Database column ANNOTATION SqlType(VARCHAR) */
  case class NodeAnnotationsRow(nodeAnnotationId: String, userId: Int, nodeName: String, annotation: String)
  /** GetResult implicit for fetching NodeAnnotationsRow objects using plain SQL queries */
  implicit def GetResultNodeAnnotationsRow(implicit e0: GR[String], e1: GR[Int]): GR[NodeAnnotationsRow] = GR{
    prs => import prs._
    NodeAnnotationsRow.tupled((<<[String], <<[Int], <<[String], <<[String]))
  }
  /** Table description of table NODE_ANNOTATIONS. Objects of this class serve as prototypes for rows in queries. */
  class NodeAnnotations(_tableTag: Tag) extends Table[NodeAnnotationsRow](_tableTag, Some("BEE"), "NODE_ANNOTATIONS") {
    def * = (nodeAnnotationId, userId, nodeName, annotation) <> (NodeAnnotationsRow.tupled, NodeAnnotationsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(nodeAnnotationId), Rep.Some(userId), Rep.Some(nodeName), Rep.Some(annotation)).shaped.<>({r=>import r._; _1.map(_=> NodeAnnotationsRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column NODE_ANNOTATION_ID SqlType(VARCHAR), PrimaryKey */
    val nodeAnnotationId: Rep[String] = column[String]("NODE_ANNOTATION_ID", O.PrimaryKey)
    /** Database column USER_ID SqlType(INTEGER) */
    val userId: Rep[Int] = column[Int]("USER_ID")
    /** Database column NODE_NAME SqlType(VARCHAR) */
    val nodeName: Rep[String] = column[String]("NODE_NAME")
    /** Database column ANNOTATION SqlType(VARCHAR) */
    val annotation: Rep[String] = column[String]("ANNOTATION")

    /** Foreign key referencing UserAccounts (database name NODE_ANNOTATION_TO_USERS_FK) */
    lazy val userAccountsFk = foreignKey("NODE_ANNOTATION_TO_USERS_FK", userId, UserAccounts)(r => r.userAccountId, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  }
  /** Collection-like TableQuery object for table NodeAnnotations */
  lazy val NodeAnnotations = new TableQuery(tag => new NodeAnnotations(tag))

  /** Entity class storing rows of table ScraperActors
   *  @param scraperActorId Database column SCRAPER_ACTOR_ID SqlType(VARCHAR), PrimaryKey
   *  @param userId Database column USER_ID SqlType(INTEGER)
   *  @param email Database column EMAIL SqlType(VARCHAR)
   *  @param startedMillis Database column STARTED_MILLIS SqlType(BIGINT)
   *  @param finishedMillis Database column FINISHED_MILLIS SqlType(BIGINT), Default(None)
   *  @param updatedMillis Database column UPDATED_MILLIS SqlType(BIGINT)
   *  @param terminatedMillis Database column TERMINATED_MILLIS SqlType(BIGINT), Default(None) */
  case class ScraperActorsRow(scraperActorId: String, userId: Int, email: String, startedMillis: Long, finishedMillis: Option[Long] = None, updatedMillis: Long, terminatedMillis: Option[Long] = None)
  /** GetResult implicit for fetching ScraperActorsRow objects using plain SQL queries */
  implicit def GetResultScraperActorsRow(implicit e0: GR[String], e1: GR[Int], e2: GR[Long], e3: GR[Option[Long]]): GR[ScraperActorsRow] = GR{
    prs => import prs._
    ScraperActorsRow.tupled((<<[String], <<[Int], <<[String], <<[Long], <<?[Long], <<[Long], <<?[Long]))
  }
  /** Table description of table SCRAPER_ACTORS. Objects of this class serve as prototypes for rows in queries. */
  class ScraperActors(_tableTag: Tag) extends Table[ScraperActorsRow](_tableTag, Some("BEE"), "SCRAPER_ACTORS") {
    def * = (scraperActorId, userId, email, startedMillis, finishedMillis, updatedMillis, terminatedMillis) <> (ScraperActorsRow.tupled, ScraperActorsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(scraperActorId), Rep.Some(userId), Rep.Some(email), Rep.Some(startedMillis), finishedMillis, Rep.Some(updatedMillis), terminatedMillis).shaped.<>({r=>import r._; _1.map(_=> ScraperActorsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5, _6.get, _7)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column SCRAPER_ACTOR_ID SqlType(VARCHAR), PrimaryKey */
    val scraperActorId: Rep[String] = column[String]("SCRAPER_ACTOR_ID", O.PrimaryKey)
    /** Database column USER_ID SqlType(INTEGER) */
    val userId: Rep[Int] = column[Int]("USER_ID")
    /** Database column EMAIL SqlType(VARCHAR) */
    val email: Rep[String] = column[String]("EMAIL")
    /** Database column STARTED_MILLIS SqlType(BIGINT) */
    val startedMillis: Rep[Long] = column[Long]("STARTED_MILLIS")
    /** Database column FINISHED_MILLIS SqlType(BIGINT), Default(None) */
    val finishedMillis: Rep[Option[Long]] = column[Option[Long]]("FINISHED_MILLIS", O.Default(None))
    /** Database column UPDATED_MILLIS SqlType(BIGINT) */
    val updatedMillis: Rep[Long] = column[Long]("UPDATED_MILLIS")
    /** Database column TERMINATED_MILLIS SqlType(BIGINT), Default(None) */
    val terminatedMillis: Rep[Option[Long]] = column[Option[Long]]("TERMINATED_MILLIS", O.Default(None))

    /** Foreign key referencing UserAccounts (database name SCRAPER_ACTOR_TO_USERS_FK) */
    lazy val userAccountsFk = foreignKey("SCRAPER_ACTOR_TO_USERS_FK", userId, UserAccounts)(r => r.userAccountId, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  }
  /** Collection-like TableQuery object for table ScraperActors */
  lazy val ScraperActors = new TableQuery(tag => new ScraperActors(tag))

  /** Entity class storing rows of table ShareableContexts
   *  @param shareableContextId Database column SHAREABLE_CONTEXT_ID SqlType(VARCHAR), PrimaryKey
   *  @param userId Database column USER_ID SqlType(INTEGER)
   *  @param treeRoot Database column TREE_ROOT SqlType(VARCHAR)
   *  @param createdAtMillis Database column CREATED_AT_MILLIS SqlType(BIGINT) */
  case class ShareableContextsRow(shareableContextId: String, userId: Int, treeRoot: String, createdAtMillis: Long)
  /** GetResult implicit for fetching ShareableContextsRow objects using plain SQL queries */
  implicit def GetResultShareableContextsRow(implicit e0: GR[String], e1: GR[Int], e2: GR[Long]): GR[ShareableContextsRow] = GR{
    prs => import prs._
    ShareableContextsRow.tupled((<<[String], <<[Int], <<[String], <<[Long]))
  }
  /** Table description of table SHAREABLE_CONTEXTS. Objects of this class serve as prototypes for rows in queries. */
  class ShareableContexts(_tableTag: Tag) extends Table[ShareableContextsRow](_tableTag, Some("BEE"), "SHAREABLE_CONTEXTS") {
    def * = (shareableContextId, userId, treeRoot, createdAtMillis) <> (ShareableContextsRow.tupled, ShareableContextsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(shareableContextId), Rep.Some(userId), Rep.Some(treeRoot), Rep.Some(createdAtMillis)).shaped.<>({r=>import r._; _1.map(_=> ShareableContextsRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column SHAREABLE_CONTEXT_ID SqlType(VARCHAR), PrimaryKey */
    val shareableContextId: Rep[String] = column[String]("SHAREABLE_CONTEXT_ID", O.PrimaryKey)
    /** Database column USER_ID SqlType(INTEGER) */
    val userId: Rep[Int] = column[Int]("USER_ID")
    /** Database column TREE_ROOT SqlType(VARCHAR) */
    val treeRoot: Rep[String] = column[String]("TREE_ROOT")
    /** Database column CREATED_AT_MILLIS SqlType(BIGINT) */
    val createdAtMillis: Rep[Long] = column[Long]("CREATED_AT_MILLIS")

    /** Foreign key referencing UserAccounts (database name SHAREABLE_CONTEXT_TO_USERS_FK) */
    lazy val userAccountsFk = foreignKey("SHAREABLE_CONTEXT_TO_USERS_FK", userId, UserAccounts)(r => r.userAccountId, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  }
  /** Collection-like TableQuery object for table ShareableContexts */
  lazy val ShareableContexts = new TableQuery(tag => new ShareableContexts(tag))

  /** Entity class storing rows of table UserAccounts
   *  @param userAccountId Database column USER_ACCOUNT_ID SqlType(INTEGER), AutoInc, PrimaryKey
   *  @param email Database column EMAIL SqlType(VARCHAR)
   *  @param hashedPassword Database column HASHED_PASSWORD SqlType(VARCHAR) */
  case class UserAccountsRow(userAccountId: Int, email: String, hashedPassword: String)
  /** GetResult implicit for fetching UserAccountsRow objects using plain SQL queries */
  implicit def GetResultUserAccountsRow(implicit e0: GR[Int], e1: GR[String]): GR[UserAccountsRow] = GR{
    prs => import prs._
    UserAccountsRow.tupled((<<[Int], <<[String], <<[String]))
  }
  /** Table description of table USER_ACCOUNTS. Objects of this class serve as prototypes for rows in queries. */
  class UserAccounts(_tableTag: Tag) extends Table[UserAccountsRow](_tableTag, Some("BEE"), "USER_ACCOUNTS") {
    def * = (userAccountId, email, hashedPassword) <> (UserAccountsRow.tupled, UserAccountsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userAccountId), Rep.Some(email), Rep.Some(hashedPassword)).shaped.<>({r=>import r._; _1.map(_=> UserAccountsRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column USER_ACCOUNT_ID SqlType(INTEGER), AutoInc, PrimaryKey */
    val userAccountId: Rep[Int] = column[Int]("USER_ACCOUNT_ID", O.AutoInc, O.PrimaryKey)
    /** Database column EMAIL SqlType(VARCHAR) */
    val email: Rep[String] = column[String]("EMAIL")
    /** Database column HASHED_PASSWORD SqlType(VARCHAR) */
    val hashedPassword: Rep[String] = column[String]("HASHED_PASSWORD")
  }
  /** Collection-like TableQuery object for table UserAccounts */
  lazy val UserAccounts = new TableQuery(tag => new UserAccounts(tag))

  /** Entity class storing rows of table UserConnections
   *  @param userConnectionId Database column USER_CONNECTION_ID SqlType(INTEGER), AutoInc, PrimaryKey
   *  @param senderUserId Database column SENDER_USER_ID SqlType(INTEGER)
   *  @param receiverUserId Database column RECEIVER_USER_ID SqlType(INTEGER) */
  case class UserConnectionsRow(userConnectionId: Int, senderUserId: Int, receiverUserId: Int)
  /** GetResult implicit for fetching UserConnectionsRow objects using plain SQL queries */
  implicit def GetResultUserConnectionsRow(implicit e0: GR[Int]): GR[UserConnectionsRow] = GR{
    prs => import prs._
    UserConnectionsRow.tupled((<<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table USER_CONNECTIONS. Objects of this class serve as prototypes for rows in queries. */
  class UserConnections(_tableTag: Tag) extends Table[UserConnectionsRow](_tableTag, Some("BEE"), "USER_CONNECTIONS") {
    def * = (userConnectionId, senderUserId, receiverUserId) <> (UserConnectionsRow.tupled, UserConnectionsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userConnectionId), Rep.Some(senderUserId), Rep.Some(receiverUserId)).shaped.<>({r=>import r._; _1.map(_=> UserConnectionsRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column USER_CONNECTION_ID SqlType(INTEGER), AutoInc, PrimaryKey */
    val userConnectionId: Rep[Int] = column[Int]("USER_CONNECTION_ID", O.AutoInc, O.PrimaryKey)
    /** Database column SENDER_USER_ID SqlType(INTEGER) */
    val senderUserId: Rep[Int] = column[Int]("SENDER_USER_ID")
    /** Database column RECEIVER_USER_ID SqlType(INTEGER) */
    val receiverUserId: Rep[Int] = column[Int]("RECEIVER_USER_ID")

    /** Foreign key referencing UserAccounts (database name USER_CONNECTIONS_RECEIVER_TO_USERS_FK) */
    lazy val userAccountsFk1 = foreignKey("USER_CONNECTIONS_RECEIVER_TO_USERS_FK", receiverUserId, UserAccounts)(r => r.userAccountId, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
    /** Foreign key referencing UserAccounts (database name USER_CONNECTIONS_SENDER_TO_USERS_FK) */
    lazy val userAccountsFk2 = foreignKey("USER_CONNECTIONS_SENDER_TO_USERS_FK", senderUserId, UserAccounts)(r => r.userAccountId, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  }
  /** Collection-like TableQuery object for table UserConnections */
  lazy val UserConnections = new TableQuery(tag => new UserConnections(tag))

  /** Entity class storing rows of table UserSessions
   *  @param userSessionId Database column USER_SESSION_ID SqlType(INTEGER), AutoInc, PrimaryKey
   *  @param userId Database column USER_ID SqlType(INTEGER)
   *  @param hashString Database column HASH_STRING SqlType(VARCHAR) */
  case class UserSessionsRow(userSessionId: Int, userId: Int, hashString: String)
  /** GetResult implicit for fetching UserSessionsRow objects using plain SQL queries */
  implicit def GetResultUserSessionsRow(implicit e0: GR[Int], e1: GR[String]): GR[UserSessionsRow] = GR{
    prs => import prs._
    UserSessionsRow.tupled((<<[Int], <<[Int], <<[String]))
  }
  /** Table description of table USER_SESSIONS. Objects of this class serve as prototypes for rows in queries. */
  class UserSessions(_tableTag: Tag) extends Table[UserSessionsRow](_tableTag, Some("BEE"), "USER_SESSIONS") {
    def * = (userSessionId, userId, hashString) <> (UserSessionsRow.tupled, UserSessionsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userSessionId), Rep.Some(userId), Rep.Some(hashString)).shaped.<>({r=>import r._; _1.map(_=> UserSessionsRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column USER_SESSION_ID SqlType(INTEGER), AutoInc, PrimaryKey */
    val userSessionId: Rep[Int] = column[Int]("USER_SESSION_ID", O.AutoInc, O.PrimaryKey)
    /** Database column USER_ID SqlType(INTEGER) */
    val userId: Rep[Int] = column[Int]("USER_ID")
    /** Database column HASH_STRING SqlType(VARCHAR) */
    val hashString: Rep[String] = column[String]("HASH_STRING")

    /** Foreign key referencing UserAccounts (database name USER_SESSIONS_TO_USER_FK) */
    lazy val userAccountsFk = foreignKey("USER_SESSIONS_TO_USER_FK", userId, UserAccounts)(r => r.userAccountId, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Restrict)
  }
  /** Collection-like TableQuery object for table UserSessions */
  lazy val UserSessions = new TableQuery(tag => new UserSessions(tag))
}
