package database

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class Permission(Id: Long,
  Name: String,
  Description: String) {

  def this(Name: String, Description: String) = this(0, Name, Description)
}

class PermissionTableDef(tag: Tag) extends Table[Permission](tag, Structure.Permissions.Name) {
  def Id = column[Long](Structure.Permissions.Columns.Id, O.PrimaryKey, O.AutoInc)
  def Name = column[String](Structure.Permissions.Columns.Name)
  def Description = column[String](Structure.Permissions.Columns.Description)

  override def * = (Id, Name, Description) <>(Permission.tupled, Permission.unapply)

  def nameIndex = index(Structure.Permissions.Columns.Name + "_IDX", Name, unique = true)
}

object Permissions {
  val permissions = TableQuery[PermissionTableDef]

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  def add(permission: Permission): Future[Int] = {
    dbConfig.db.run(permissions += permission)
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(permissions.filter(_.Id === id).delete)
  }

  def edit(id: Long, name: String, description: String): Future[Int] = {
    val q = for { p <- permissions if p.Id === id } yield (p.Name, p.Description)
    dbConfig.db.run(q.update(name, description))
  }

  def get(id: Long): Future[Option[Permission]] = {
    dbConfig.db.run(permissions.filter(_.Id === id).result.headOption)
  }

  def get(name: String): Future[Option[Permission]] = {
    dbConfig.db.run(permissions.filter(_.Name === name).result.headOption)
  }

  def get(ids: List[Long]): Future[Seq[Permission]] = {
    dbConfig.db.run(permissions.filter(_.Id.inSet(ids)).result)
  }

  def listAll(): Future[Seq[Permission]] = {
    dbConfig.db.run(permissions.result)
  }
}
