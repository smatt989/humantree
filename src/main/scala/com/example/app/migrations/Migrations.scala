package com.example.app.migrations

/**
  * Created by matt on 8/4/17.
  */
object Migrations {
  lazy val all: Seq[Migration] = Seq(
    Migration1,
    Migration2,
    Migration3,
    Migration4,
    Migration5,
    Migration6,
    Migration7,
    Migration8
  )
}
