package com.example.app.migrations

import com.example.app.{AppGlobals, DBLauncher}
import slick.dbio
import AppGlobals.dbConfig.driver.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import slick.codegen.SourceCodeGenerator

trait Migration {

  def id: Int
  def query: DBIOAction[_, _ <: dbio.NoStream, _ <: dbio.Effect]
}

object CodeGenerator {
  def main(args: Array[String]): Unit = {
    Migration.codegen()
  }
}

object DBSetup {
  def main(args: Array[String]): Unit = {
    Migration.setupDB()
  }
}

object Migration {

  private[this] lazy val migrations: Seq[Migration] =
    Migrations.all


  lazy val db = AppGlobals.db

  def initDB() = {
    if(!dBInitialized)
      insertOneMigration(InitDB)
    else
      System.out.println("DB already initialized...")
  }

  def run() = {
    if(dBInitialized) {
      val latest = latestMigrationInDB()
      migrations.drop(latest).foreach(f => {
        insertOneMigration(f)
      })
      System.out.println("Done.")
    } else
      System.out.println("DB not initialized...")
  }

  def setupDB() = {
    initDB()
    run()
  }

  def codegen() = {
    SourceCodeGenerator.run(
      //AppGlobals.dbConfig.driverName,
      "slick.driver.PostgresDriver",
      DBLauncher.cpds.getDriverClass,
      DBLauncher.cpds.getJdbcUrl,
      "src/main/scala",
      "com.example.app.db",
      Some(DBLauncher.cpds.getUser),
      Some(DBLauncher.cpds.getPassword)
    )
  }

  def dBInitialized =
    Await.result(db.run(sql"""select TABLE_NAME from information_schema.tables where TABLE_NAME = ${InitDB.MIGRATION_TABLE_NAME}""".as[(String)]), Duration.Inf).nonEmpty

  def latestMigrationInDB() = {
    val r = Await.result(db.run(InitDB.migrationTable.result), Duration.Inf).max - 1
    System.out.println("Last migration: "+r)
    System.out.println("Migrations to run: "+(migrations.size - r))
    r
  }

  private[this] def insertOneMigration(m: Migration) = {
    System.out.println("Starting migration "+m.id)
    Await.result(db.run(DBIO.seq(m.query, InitDB.migrationTable += (m.id + 1)).transactionally), Duration.Inf)
    System.out.println("Finished migration "+m.id)
  }
}
