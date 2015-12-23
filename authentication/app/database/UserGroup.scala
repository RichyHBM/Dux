package database

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class UserGroup(Id: Long,
  UserId: Long,
  GroupId: Long)

class UserGroupTableDef(tag: Tag) extends Table[UserGroup](tag, Structure.UserGroup.Name) {
  def Id = column[Long](Structure.UserGroup.Columns.Id, O.PrimaryKey, O.AutoInc)
  def UserId = column[Long](Structure.UserGroup.Columns.UserId)
  def GroupId = column[Long](Structure.UserGroup.Columns.GroupId)

  override def * = (Id, UserId, GroupId) <>(UserGroup.tupled, UserGroup.unapply)
  def user = foreignKey(Structure.Users.Name, UserId, Users.users)(_.Id)
  def group = foreignKey(Structure.Groups.Name, GroupId, Groups.groups)(_.Id)
  def userGroupIndex = index(Structure.Users.Name + "_" + Structure.Groups.Name + "_IDX", (UserId, GroupId), unique = true)
}
