package com.example.app.migrations

import com.example.app.AppGlobals
import AppGlobals.dbConfig.driver.api._
object Migration2 extends Migration {

  val id = 2

  class Introductions(tag: Tag) extends Table[(String, String, String, String, Long)](tag, Some(InitDB.SCHEMA_NAME), "INTRODUCTIONS") {
    def id = column[String]("INTRODUCTION_ID", O.PrimaryKey)
    def senderPersonEmail = column[String]("SENDER_PERSON_EMAIL")
    def receiverPersonEmail = column[String]("RECEIVER_PERSON_EMAIL")
    def introPersonEmail = column[String]("INTRO_PERSON_EMAIL")
    def introTimeMillis = column[Long]("INTRO_TIME_MILLIS")

    def * = (id, senderPersonEmail, receiverPersonEmail, introPersonEmail, introTimeMillis)
  }

  val introductions = TableQuery[Introductions]

  def query = (introductions.schema).create
}