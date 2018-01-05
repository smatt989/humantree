package com.example.app.models

/**
  * Created by matt on 1/3/18.
  */
case class TreeRequestObject(root: Option[String] = None, emails: Option[Seq[String]] = None)

case class TreeShareObject(root: String)

case class SharedContextRequestObject(key: String)