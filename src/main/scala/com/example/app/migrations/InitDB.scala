package com.example.app.migrations

import com.example.app.AppGlobals
import AppGlobals.dbConfig.driver.api._

object InitDB extends Migration {

  val id = 0

  val MIGRATION_TABLE_NAME = "MIGRATIONS"

  val SCHEMA_NAME = "BEE"

  class Migrations(tag: Tag) extends Table[(Int)](tag, Some(SCHEMA_NAME), MIGRATION_TABLE_NAME) {
    def ido = column[Int]("MIGRATION_ID", O.PrimaryKey)

    def * = (ido)
  }

  lazy val migrationTable = TableQuery[Migrations]

  def createSchema = {
    val toInsert = "\""+SCHEMA_NAME+"\""
    sqlu"""CREATE SCHEMA #${toInsert}"""
  }

  def query = DBIO.seq(createSchema, migrationTable.schema.create).transactionally
}