package database

import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class Permission(Id: Long,
  Name: String,
  Description: String)

class PermissionTableDef(tag: Tag) extends Table[Permission](tag, Structure.Permissions.Name) {
  def Id = column[Long](Structure.Permissions.Columns.Id, O.PrimaryKey, O.AutoInc)
  def Name = column[String](Structure.Permissions.Columns.Name)
  def Description = column[String](Structure.Permissions.Columns.Description)

  override def * = (Id, Name, Description) <>(Permission.tupled, Permission.unapply)

  def nameIndex = index(Structure.Permissions.Columns.Name + "_IDX", Name, unique = true)
}

object Permissions {
  val permissions = TableQuery[PermissionTableDef]

  def add(dbConfigProvider: DatabaseConfigProvider, permission: Permission): Future[Option[Exception]] = {
    val dbConfig = dbConfigProvider.get[JdbcProfile]
    dbConfig.db.run(permissions += permission).map(res => None).recover {
      case ex: Exception => Some(ex)
    }
  }

  def delete(dbConfigProvider: DatabaseConfigProvider, id: Long): Future[Int] = {
    val dbConfig = dbConfigProvider.get[JdbcProfile]
    dbConfig.db.run(permissions.filter(_.Id === id).delete)
  }

  def get(dbConfigProvider: DatabaseConfigProvider, id: Long): Future[Option[Permission]] = {
    val dbConfig = dbConfigProvider.get[JdbcProfile]
    dbConfig.db.run(permissions.filter(_.Id === id).result.headOption)
  }

  def listAll(dbConfigProvider: DatabaseConfigProvider): Future[Seq[Permission]] = {
    val dbConfig = dbConfigProvider.get[JdbcProfile]
    dbConfig.db.run(permissions.result)
  }
}
