package database

import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class Group(Id: Long,
  Name: String,
  Description: String)

class GroupTableDef(tag: Tag) extends Table[Group](tag, Structure.Groups.Name) {
  def Id = column[Long](Structure.Groups.Columns.Id, O.PrimaryKey, O.AutoInc)
  def Name = column[String](Structure.Groups.Columns.Name)
  def Description = column[String](Structure.Groups.Columns.Description)

  override def * = (Id, Name, Description) <>(Group.tupled, Group.unapply)

  def nameIndex = index(Structure.Groups.Columns.Name + "_IDX", Name, unique = true)
}

object Groups {
  val groups = TableQuery[GroupTableDef]

  def add(dbConfigProvider: DatabaseConfigProvider, group: Group): Future[Option[Exception]] = {
    val dbConfig = dbConfigProvider.get[JdbcProfile]
    dbConfig.db.run(groups += group).map(res => None).recover {
      case ex: Exception => Some(ex)
    }
  }

  def delete(dbConfigProvider: DatabaseConfigProvider, id: Long): Future[Int] = {
    val dbConfig = dbConfigProvider.get[JdbcProfile]
    dbConfig.db.run(groups.filter(_.Id === id).delete)
  }

  def get(dbConfigProvider: DatabaseConfigProvider, id: Long): Future[Option[Group]] = {
    val dbConfig = dbConfigProvider.get[JdbcProfile]
    dbConfig.db.run(groups.filter(_.Id === id).result.headOption)
  }

  def listAll(dbConfigProvider: DatabaseConfigProvider): Future[Seq[Group]] = {
    val dbConfig = dbConfigProvider.get[JdbcProfile]
    dbConfig.db.run(groups.result)
  }
}
