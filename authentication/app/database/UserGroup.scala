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

object UserGroups {
  val userGroups = TableQuery[UserGroupTableDef]

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  def add(userGroup: UserGroup): Future[Int] = {
    dbConfig.db.run(userGroups += userGroup)
  }

  def add(userId: Long, groupId: Long): Future[Int] = {
    dbConfig.db.run(userGroups += UserGroup(0, userId, groupId))
  }

  def getAllUserIdsFromGroupId(groupId: Long): Future[Seq[Long]] = {
    dbConfig.db.run(userGroups.filter(_.GroupId === groupId).map(ug => ug.UserId).result)
  }

  def getAllGroupIdsForUserId(userId: Long): Future[Seq[Long]] = {
    dbConfig.db.run(userGroups.filter(_.UserId === userId).map(ug => ug.GroupId).result)
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(userGroups.filter(_.Id === id).delete)
  }

  def deleteAllFromUserId(userId: Long): Future[Int] = {
    dbConfig.db.run(userGroups.filter(_.UserId === userId).delete)
  }

  def deleteAllFromGroupId(groupId: Long): Future[Int] = {
    dbConfig.db.run(userGroups.filter(_.GroupId === groupId).delete)
  }

  def listAll(): Future[Seq[UserGroup]] = {
    dbConfig.db.run(userGroups.result)
  }
}
