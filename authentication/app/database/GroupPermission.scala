package database

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class GroupPermission(Id: Long,
  GroupId: Long,
  PermissionId: Long)

class GroupPermissionTableDef(tag: Tag) extends Table[GroupPermission](tag, Structure.GroupPermission.Name) {
  def Id = column[Long](Structure.GroupPermission.Columns.Id, O.PrimaryKey, O.AutoInc)
  def GroupId = column[Long](Structure.GroupPermission.Columns.GroupId)
  def PermissionId = column[Long](Structure.GroupPermission.Columns.PermissionId)

  override def * = (Id, GroupId, PermissionId) <>(GroupPermission.tupled, GroupPermission.unapply)
  def group = foreignKey(Structure.Groups.Name + "_FK", GroupId, Groups.groups)(_.Id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
  def permission = foreignKey(Structure.Permissions.Name + "_FK", PermissionId, Permissions.permissions)(_.Id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
  def groupPermissionIndex = index(Structure.Groups.Name + "_" + Structure.Permissions.Name + "_IDX", (GroupId, PermissionId), unique = true)
}

object GroupPermissions {
  val groupPermissions = TableQuery[GroupPermissionTableDef]

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  def add(groupPermission: GroupPermission): Future[Int] = {
    dbConfig.db.run(groupPermissions += groupPermission)
  }

  def add(groupId: Long, permissionId: Long): Future[Int] = {
    dbConfig.db.run(groupPermissions += GroupPermission(0, groupId, permissionId))
  }

  def getAllPermissionIdsFromGroupId(groupId: Long): Future[Seq[Long]] = {
    dbConfig.db.run(groupPermissions.filter(_.GroupId === groupId).map(gp => gp.PermissionId).result)
  }

  def getAllPermissionIdsFromGroupIds(groupIds: Seq[Long]): Future[Seq[Long]] = {
    dbConfig.db.run(groupPermissions.filter(_.GroupId.inSet(groupIds)).map(gp => gp.PermissionId).result)
  }

  def getAllGroupIdsFromPermissionId(permissionId: Long): Future[Seq[Long]] = {
    dbConfig.db.run(groupPermissions.filter(_.PermissionId === permissionId).map(gp => gp.GroupId).result)
  }

  def getAllGroupIdsFromPermissionIds(permissionIds: Seq[Long]): Future[Seq[Long]] = {
    dbConfig.db.run(groupPermissions.filter(_.PermissionId.inSet(permissionIds)).map(gp => gp.GroupId).result)
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(groupPermissions.filter(_.Id === id).delete)
  }

  def deleteAllFromGroupId(groupId: Long): Future[Int] = {
    dbConfig.db.run(groupPermissions.filter(_.GroupId === groupId).delete)
  }

  def deleteAllFromPermissionId(permissionId: Long): Future[Int] = {
    dbConfig.db.run(groupPermissions.filter(_.PermissionId === permissionId).delete)
  }

  def listAll(): Future[Seq[GroupPermission]] = {
    dbConfig.db.run(groupPermissions.result)
  }
}