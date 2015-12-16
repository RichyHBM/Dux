package database

import play.api.Play
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

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  def add(group: Group): Future[Option[Exception]] = {
    dbConfig.db.run(groups += group).map(res => None).recover {
      case ex: Exception => Some(ex)
    }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(groups.filter(_.Id === id).delete)
  }

  def get(id: Long): Future[Option[Group]] = {
    dbConfig.db.run(groups.filter(_.Id === id).result.headOption)
  }

  def listAll(): Future[Seq[Group]] = {
    dbConfig.db.run(groups.result)
  }
}
