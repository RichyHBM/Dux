package database

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class PermissionApp(Id: Long,
  PermissionId: Long,
  AppId: Long)

class PermissionAppTableDef(tag: Tag) extends Table[PermissionApp](tag, Structure.PermissionApp.Name) {
  def Id = column[Long](Structure.PermissionApp.Columns.Id, O.PrimaryKey, O.AutoInc)
  def PermissionId = column[Long](Structure.PermissionApp.Columns.PermissionId)
  def AppId = column[Long](Structure.PermissionApp.Columns.AppId)

  override def * = (Id, PermissionId, AppId) <>(PermissionApp.tupled, PermissionApp.unapply)
  def permission = foreignKey(Structure.Permissions.Name, PermissionId, Permissions.permissions)(_.Id)
  def app = foreignKey(Structure.Apps.Name, AppId, Apps.apps)(_.Id)
  def permissionAppIndex = index(Structure.Permissions.Name + "_" + Structure.Apps.Name + "_IDX", (PermissionId, AppId), unique = true)
}
