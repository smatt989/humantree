package com.example.app

import java.util.UUID

import slick.lifted.TableQuery
import slick.profile.{FixedSqlAction, FixedSqlStreamingAction, RelationalProfile}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by matt on 12/1/16.
  */

import AppGlobals.dbConfig.driver.api._

trait SlickObject[IDType, SlickRow, SlickTable <: RelationalProfile#Table[SlickRow]] {

  def deleteQuery(ids: Seq[IDType]): FixedSqlAction[Int, NoStream, Effect.Write]
  def byIdsQuery(ids: Seq[IDType]): FixedSqlStreamingAction[Seq[SlickRow], SlickRow, Effect.Read]


  def table: TableQuery[SlickTable]
  def db = AppGlobals.db

  def idFromRow(a: SlickRow): IDType
  def updateId(a: SlickRow, id: IDType): SlickRow

  def idColumnFromTable(a: SlickTable): Rep[IDType]

  def existsInDb(a: SlickRow): Boolean
  def makeSavingId(a: SlickRow): SlickRow

  def createMany(as: Seq[SlickRow]): Future[Seq[SlickRow]]

  def byIds(ids: Seq[IDType]): Future[Seq[SlickRow]] =
    db.run(byIdsQuery(ids))

  def deleteMany(ids: Seq[IDType]): Future[Int] =
    db.run(deleteQuery(ids))

  def getAll =
    db.run(table.result)

  def create(a: SlickRow): Future[SlickRow] = createMany(Seq(a)).map(_.head)
  def byId(id: IDType): Future[SlickRow] = byIds(Seq(id)).map(_.head)
  def delete(id: IDType): Future[Int] = deleteMany(Seq(id))
}

trait SlickDbObject[SlickRow, SlickTable <: RelationalProfile#Table[SlickRow]] extends SlickObject[Int, SlickRow, SlickTable]{

  //HELPER QUERY STATEMENTS

  def makeSavingId(a: SlickRow) = updateId(a, 0)
  def existsInDb(a: SlickRow) =
    !(idFromRow(a) == 0 || idFromRow(a) == null)

  def createMany(as: Seq[SlickRow]): Future[Seq[SlickRow]] = {
    if(as.nonEmpty) {
      Future.sequence(as.grouped(1000).map(group => {
        val ids = db.run(createQuery(group.map(makeSavingId)))
        ids.map(is => zipWithNewIds(group, is))
      }).toSeq).map(_.flatten)
    } else
      Future.apply(as)
  }

  def createQuery(as: Seq[SlickRow]) =
    (table returning table.map(idColumnFromTable)) ++= as

  def zipWithNewIds(as: Seq[SlickRow], ids: Seq[Int]) =
    ids.zipWithIndex.map{ case (id, index) => updateId(as(index),id)}

  def deleteQuery(ids: Seq[Int]) =
    table.filter(a => idColumnFromTable(a) inSet ids).delete

  def byIdsQuery(ids: Seq[Int]) =
    table.filter(a => idColumnFromTable(a) inSet ids).result

}

trait SlickUUIDObject[SlickRow, SlickTable <: RelationalProfile#Table[SlickRow]] extends SlickObject[String, SlickRow, SlickTable] {

  def makeUUID = UUID.randomUUID().toString

  def makeSavingId(a: SlickRow) = updateId(a, makeUUID)
  def existsInDb(a: SlickRow) =
    idFromRow(a) != null

  def createMany(as: Seq[SlickRow]): Future[Seq[SlickRow]] = {
    if(as.nonEmpty) {
      Future.sequence(as.grouped(1000).map(group => {
        val toCreate = group.map(makeSavingId)
        val ids = db.run(createQuery(toCreate))
        ids.map(_ => toCreate)
      }).toSeq).map(_.flatten)
    } else
      Future.apply(as)
  }

  def createQuery(as: Seq[SlickRow]) =
    table ++= as

  def deleteQuery(ids: Seq[String]) =
    table.filter(a => idColumnFromTable(a) inSet ids).delete

  def byIdsQuery(ids: Seq[String]) =
    table.filter(a => idColumnFromTable(a) inSet ids).result
}

trait UpdatableDBObject[SlickRow, SlickTable <: RelationalProfile#Table[SlickRow]]
  extends Updatable[Int, SlickRow, SlickTable]
  with SlickDbObject[SlickRow, SlickTable]

trait UpdatableUUIDObject[SlickRow, SlickTable <: RelationalProfile#Table[SlickRow]]
  extends Updatable[String, SlickRow, SlickTable]
  with SlickUUIDObject[SlickRow, SlickTable]

trait Updatable[IDType, SlickRow, SlickTable <: RelationalProfile#Table[SlickRow]] extends SlickObject[IDType, SlickRow, SlickTable] {
  def updateQuery(a: SlickRow): FixedSqlAction[Int, NoStream, Effect.Write]

  def save(a: SlickRow): Future[SlickRow] = saveMany(Seq(a)).map(_.head)

  def updateOne(a: SlickRow): Future[SlickRow] =
    updateMany(Seq(a)).map(_.head)

  def updateMany(as: Seq[SlickRow]): Future[Seq[SlickRow]] = {
    val queries = DBIO.sequence(as.map(updateQuery))
    db.run(queries).map(_ => as)
  }

  def saveMany(as: Seq[SlickRow]): Future[Seq[SlickRow]] = {
    val withTempId = as.zipWithIndex
    val doNotExist = withTempId.filterNot(a => existsInDb(a._1))
    val createTempIds = doNotExist.map(_._2)
    val created = createMany(doNotExist.map(_._1))
    val createdWithTempIds = created.map(c =>
      createTempIds.zipWithIndex.map{ case (tempId, index) => (c(index), tempId)}
    )
    val doExist = withTempId.filter(a => existsInDb(a._1))
    val doExistTempIds = doExist.map(_._2)
    val updated = updateMany(doExist.map(_._1))
    val updatedWithTempIds = updated.map( u =>
      doExistTempIds.zipWithIndex.map{ case (tempId, index) => (u(index), tempId)}
    )

    createdWithTempIds.flatMap(c => updatedWithTempIds.map(u => {
      (c ++ u).sortBy(_._2).map(_._1)
    }))
  }
}