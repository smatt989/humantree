package com.example.app

/**
  * Created by matt on 1/3/18.
  */
case class EmailScrapeRequestObject(email: String, appUserId: Int, startAt: Option[Int] = None)
