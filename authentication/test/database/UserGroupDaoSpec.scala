package database

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import utilities._

import scala.concurrent.Await
import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class UserGroupDaoSpec extends Specification {

  val bytes = Array.fill[Byte](5)(0)

  def user1 = new User("User1", "user1@test.com", bytes, bytes, "api1")
  def user2 = new User("User2", "user2@test.com", bytes, bytes, "api2")
  def user3 = new User("User3", "user3@test.com", bytes, bytes, "api3")

  def group1 = new Group("Group1", "Group 1 Description")
  def group2 = new Group("Group2", "Group 2 Description")
  def group3 = new Group("Group3", "Group 3 Description")

  "UserGroupDaoSpec" should {
    "Add user group" in Statics.WithFreshDatabase {
      Await.result(Users.add(user1), 2.seconds) must equalTo(1)
      Await.result(Users.add(user2), 2.seconds) must equalTo(1)
      Await.result(Users.add(user3), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)

      Await.result(UserGroups.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(2, 1), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(2, 2), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(3, 2), 2.seconds) must equalTo(1)
    }

    "Add user groups" in Statics.WithFreshDatabase {
      Await.result(Users.add(user1), 2.seconds) must equalTo(1)
      Await.result(Users.add(user2), 2.seconds) must equalTo(1)
      Await.result(Users.add(user3), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)

      val userGroups = Seq(
        UserGroup(0, 1, 1),
        UserGroup(0, 2, 1),
        UserGroup(0, 2, 2),
        UserGroup(0, 3, 2)
      )

      Await.result(UserGroups.add(userGroups), 2.seconds) must equalTo(4)
    }

    "Not add invalid" in Statics.WithFreshDatabase {
      Await.result(Users.add(user1), 2.seconds) must equalTo(1)
      Await.result(Users.add(user2), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)

      try {
        Await.result(UserGroups.add(100, 1), 2.seconds) must equalTo(0)
        false must equalTo(true)
      } catch {
        case _: Exception => true must equalTo(true)
      }
      try {
        Await.result(UserGroups.add(1, 200), 2.seconds) must equalTo(0)
        false must equalTo(true)
      } catch {
        case _: Exception => true must equalTo(true)
      }
    }

    "Not add duplicates" in Statics.WithFreshDatabase {
      Await.result(Users.add(user1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(1, 1), 2.seconds) must equalTo(1)
      try {
        Await.result(UserGroups.add(1, 1), 2.seconds)
        false must equalTo(true)
      } catch {
        case _: Exception => true must equalTo(true)
      }
    }

    "Delete user groups" in Statics.WithFreshDatabase {
      Await.result(Users.add(user1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)

      Await.result(UserGroups.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(UserGroups.delete(1), 2.seconds) must equalTo(1)
    }

    "Delete user groups by user" in Statics.WithFreshDatabase {
      Await.result(Users.add(user1), 2.seconds) must equalTo(1)
      Await.result(Users.add(user2), 2.seconds) must equalTo(1)
      Await.result(Users.add(user3), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)

      Await.result(UserGroups.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(1, 2), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(2, 1), 2.seconds) must equalTo(1)

      Await.result(UserGroups.deleteAllFromUserId(1), 2.seconds) must equalTo(2)
    }

    "Delete user groups by group" in Statics.WithFreshDatabase {
      Await.result(Users.add(user1), 2.seconds) must equalTo(1)
      Await.result(Users.add(user2), 2.seconds) must equalTo(1)
      Await.result(Users.add(user3), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)

      Await.result(UserGroups.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(1, 2), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(2, 1), 2.seconds) must equalTo(1)

      Await.result(UserGroups.deleteAllFromGroupId(1), 2.seconds) must equalTo(2)
      Await.result(UserGroups.deleteAllFromGroupId(2), 2.seconds) must equalTo(1)
    }

    "List all users in group" in Statics.WithFreshDatabase {
      Await.result(Users.add(user1), 2.seconds) must equalTo(1)
      Await.result(Users.add(user2), 2.seconds) must equalTo(1)
      Await.result(Users.add(user3), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)

      Await.result(UserGroups.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(2, 1), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(2, 2), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(3, 2), 2.seconds) must equalTo(1)

      val values = Await.result(UserGroups.getAllUserIdsFromGroupId(1), 2.seconds).toList

      values must contain(1)
      values must contain(2)
      values must not contain(3)
    }

    "List all users in groups" in Statics.WithFreshDatabase {
      Await.result(Users.add(user1), 2.seconds) must equalTo(1)
      Await.result(Users.add(user2), 2.seconds) must equalTo(1)
      Await.result(Users.add(user3), 2.seconds) must equalTo(1)

      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group3), 2.seconds) must equalTo(1)

      Await.result(UserGroups.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(2, 2), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(3, 3), 2.seconds) must equalTo(1)

      val values = Await.result(UserGroups.getAllUserIdsFromGroupIds(List(1, 2)), 2.seconds).toList

      values must contain(1)
      values must contain(2)
      values must not contain(3)
    }

    "List all groups for user" in Statics.WithFreshDatabase {
      Await.result(Users.add(user1), 2.seconds) must equalTo(1)
      Await.result(Users.add(user2), 2.seconds) must equalTo(1)
      Await.result(Users.add(user3), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)

      Await.result(UserGroups.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(2, 1), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(2, 2), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(3, 2), 2.seconds) must equalTo(1)

      val values = Await.result(UserGroups.getAllGroupIdsForUserId(2), 2.seconds).toList

      values must contain(1)
      values must contain(2)
      values must not contain(3)
    }

    "List all groups for user" in Statics.WithFreshDatabase {
      Await.result(Users.add(user1), 2.seconds) must equalTo(1)
      Await.result(Users.add(user2), 2.seconds) must equalTo(1)
      Await.result(Users.add(user3), 2.seconds) must equalTo(1)

      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group3), 2.seconds) must equalTo(1)

      Await.result(UserGroups.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(2, 2), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(3, 3), 2.seconds) must equalTo(1)

      val values = Await.result(UserGroups.getAllGroupIdsForUserIds(List(1, 2)), 2.seconds).toList

      values must contain(1)
      values must contain(2)
      values must not contain(3)
    }

    "List all user groups" in Statics.WithFreshDatabase {
      Await.result(Users.add(user1), 2.seconds) must equalTo(1)
      Await.result(Users.add(user2), 2.seconds) must equalTo(1)
      Await.result(Users.add(user3), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)

      Await.result(UserGroups.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(UserGroups.listAll, 2.seconds).length must equalTo(1)

      Await.result(UserGroups.add(2, 1), 2.seconds) must equalTo(1)
      Await.result(UserGroups.listAll, 2.seconds).length must equalTo(2)

      Await.result(UserGroups.add(2, 2), 2.seconds) must equalTo(1)
      Await.result(UserGroups.listAll, 2.seconds).length must equalTo(3)

      Await.result(UserGroups.add(3, 2), 2.seconds) must equalTo(1)
      Await.result(UserGroups.listAll, 2.seconds).length must equalTo(4)
    }
  }
}
