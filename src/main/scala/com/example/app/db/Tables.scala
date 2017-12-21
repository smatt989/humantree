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
  lazy val schema: profile.SchemaDescription = DeviceTokens.schema ++ Migrations.schema ++ UserAccounts.schema ++ UserConnections.schema ++ UserSessions.schema
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
