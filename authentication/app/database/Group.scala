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

  def add(group: Group): Future[Int] = {
    dbConfig.db.run(groups += group)
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(groups.filter(_.Id === id).delete)
  }

  def setDescription(id: Long, description: String): Future[Int] = {
    val q = for { g <- groups if g.Id === id } yield g.Description
    dbConfig.db.run(q.update(description))
  }

  def get(id: Long): Future[Option[Group]] = {
    dbConfig.db.run(groups.filter(_.Id === id).result.headOption)
  }

  def get(name: String): Future[Option[Group]] = {
    dbConfig.db.run(groups.filter(_.Name === name).result.headOption)
  }

  def get(ids: List[Long]): Future[Seq[Group]] = {
    dbConfig.db.run(groups.withFilter(g => ids.contains( g.Id )).result)
  }

  def listAll(): Future[Seq[Group]] = {
    dbConfig.db.run(groups.result)
  }
}
