package database

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class AppPermission(Id: Long,
                         AppId: Long,
                         PermissionId: Long)

class AppPermissionTableDef(tag: Tag) extends Table[AppPermission](tag, Structure.AppPermission.Name) {
  def Id = column[Long](Structure.AppPermission.Columns.Id, O.PrimaryKey, O.AutoInc)
  def AppId = column[Long](Structure.AppPermission.Columns.AppId)
  def PermissionId = column[Long](Structure.AppPermission.Columns.PermissionId)

  override def * = (Id, AppId, PermissionId) <>(AppPermission.tupled, AppPermission.unapply)
  def app = foreignKey(Structure.Apps.Name, AppId, Apps.apps)(_.Id)
  def permission = foreignKey(Structure.Permissions.Name, PermissionId, Permissions.permissions)(_.Id)
  def appPermissionIndex = index(Structure.Apps.Name + "_" + Structure.Permissions.Name + "_IDX", (AppId, PermissionId), unique = true)
}

object AppPermissions {
  val appPermissions = TableQuery[AppPermissionTableDef]

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  def add(appPermission: AppPermission): Future[Int] = {
    dbConfig.db.run(appPermissions += appPermission)
  }

  def add(appId: Long, permissionId: Long): Future[Int] = {
    dbConfig.db.run(appPermissions += AppPermission(0, appId, permissionId))
  }

  def getAllPermissionIdsFromAppId(appId: Long): Future[Seq[Long]] = {
    dbConfig.db.run(appPermissions.filter(_.AppId === appId).map(ap => ap.PermissionId).result)
  }

  def getAllAppIdsFromPermissionId(permissionId: Long): Future[Seq[Long]] = {
    dbConfig.db.run(appPermissions.filter(_.PermissionId === permissionId).map(ap => ap.AppId).result)
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(appPermissions.filter(_.Id === id).delete)
  }

  def deleteAllFromAppId(appId: Long): Future[Int] = {
    dbConfig.db.run(appPermissions.filter(_.AppId === appId).delete)
  }

  def deleteAllFromPermissionId(permissionId: Long): Future[Int] = {
    dbConfig.db.run(appPermissions.filter(_.PermissionId === permissionId).delete)
  }

  def listAll(): Future[Seq[AppPermission]] = {
    dbConfig.db.run(appPermissions.result)
  }
}