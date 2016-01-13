package database

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import java.sql.Timestamp
import java.util.Date
import play.api.test._
import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class UserGroupDaoSpec extends Specification {

  val bytes = Array.fill[Byte](5)(0)

  def user1 = new User("User1", "user1@test.com", bytes, bytes, "api1")
  def user2 = new User("User2", "user2@test.com", bytes, bytes, "api2")
  def user3 = new User("User3", "user3@test.com", bytes, bytes, "api3")

  def group1 = new Group("Group1", "Group 1 Description")
  def group2 = new Group("Group2", "Group 2 Description")

  "UserGroupDaoSpec" should {
    "Add user group" in new WithApplication(FakeApp.fakeApp){
      Users.add(user1).map(r => r must equalTo(1))
      Users.add(user2).map(r => r must equalTo(1))
      Users.add(user3).map(r => r must equalTo(1))
      Groups.add(group1).map(r => r must equalTo(1))
      Groups.add(group2).map(r => r must equalTo(1))

      UserGroups.add(1, 1).map(r => r must equalTo(1))
      UserGroups.add(2, 1).map(r => r must equalTo(1))
      UserGroups.add(2, 2).map(r => r must equalTo(1))
      UserGroups.add(3, 2).map(r => r must equalTo(1))
    }

    "Not add invalid" in new WithApplication(FakeApp.fakeApp){
      Users.add(user1).map(r => r must equalTo(1))
      Users.add(user2).map(r => r must equalTo(1))
      Groups.add(group1).map(r => r must equalTo(1))

      UserGroups.add(100, 1).map(r => r must equalTo(0))
      UserGroups.add(1, 200).map(r => r must equalTo(0))
    }

    "Not add duplicates" in new WithApplication(FakeApp.fakeApp){
      Users.add(user1).map(r => r must equalTo(1))
      Groups.add(group1).map(r => r must equalTo(1))

      UserGroups.add(1, 1).map(r => r must equalTo(1))
      UserGroups.add(1, 1).map(r => r must equalTo(0))
    }

    "Delete user groups" in new WithApplication(FakeApp.fakeApp){
      Users.add(user1).map(r => r must equalTo(1))
      Groups.add(group1).map(r => r must equalTo(1))

      UserGroups.add(1, 1).map(r => r must equalTo(1))
      UserGroups.delete(1).map(r => r must equalTo(1))
    }

    "Delete user groups by user" in new WithApplication(FakeApp.fakeApp){
      Users.add(user1).map(r => r must equalTo(1))
      Users.add(user2).map(r => r must equalTo(1))
      Users.add(user3).map(r => r must equalTo(1))
      Groups.add(group1).map(r => r must equalTo(1))
      Groups.add(group2).map(r => r must equalTo(1))

      UserGroups.add(1, 1).map(r => r must equalTo(1))
      UserGroups.add(1, 2).map(r => r must equalTo(1))
      UserGroups.add(2, 1).map(r => r must equalTo(1))

      UserGroups.deleteAllFromUserId(1).map(r => r must equalTo(2))
    }

    "Delete user groups by group" in new WithApplication(FakeApp.fakeApp){
      Users.add(user1).map(r => r must equalTo(1))
      Users.add(user2).map(r => r must equalTo(1))
      Users.add(user3).map(r => r must equalTo(1))
      Groups.add(group1).map(r => r must equalTo(1))
      Groups.add(group2).map(r => r must equalTo(1))

      UserGroups.add(1, 1).map(r => r must equalTo(1))
      UserGroups.add(1, 2).map(r => r must equalTo(1))
      UserGroups.add(2, 1).map(r => r must equalTo(1))

      UserGroups.deleteAllFromGroupId(1).map(r => r must equalTo(2))
      UserGroups.deleteAllFromGroupId(2).map(r => r must equalTo(1))
    }

    "List all users in group" in new WithApplication(FakeApp.fakeApp){
      Users.add(user1).map(r => r must equalTo(1))
      Users.add(user2).map(r => r must equalTo(1))
      Users.add(user3).map(r => r must equalTo(1))
      Groups.add(group1).map(r => r must equalTo(1))
      Groups.add(group2).map(r => r must equalTo(1))

      UserGroups.add(1, 1).map(r => r must equalTo(1))
      UserGroups.add(2, 1).map(r => r must equalTo(1))
      UserGroups.add(2, 2).map(r => r must equalTo(1))
      UserGroups.add(3, 2).map(r => r must equalTo(1))

      UserGroups.getAllUserIdsFromGroupId(1).map(r => r must contain(1))
      UserGroups.getAllUserIdsFromGroupId(1).map(r => r must contain(2))
      UserGroups.getAllUserIdsFromGroupId(1).map(r => r must not contain(3))
    }

    "List all groups for user" in new WithApplication(FakeApp.fakeApp){
      Users.add(user1).map(r => r must equalTo(1))
      Users.add(user2).map(r => r must equalTo(1))
      Users.add(user3).map(r => r must equalTo(1))
      Groups.add(group1).map(r => r must equalTo(1))
      Groups.add(group2).map(r => r must equalTo(1))

      UserGroups.add(1, 1).map(r => r must equalTo(1))
      UserGroups.add(2, 1).map(r => r must equalTo(1))
      UserGroups.add(2, 2).map(r => r must equalTo(1))
      UserGroups.add(3, 2).map(r => r must equalTo(1))

      UserGroups.getAllGroupIdsForUserId(2).map(r => r must contain(1))
      UserGroups.getAllGroupIdsForUserId(2).map(r => r must contain(2))
      UserGroups.getAllGroupIdsForUserId(1).map(r => r must not contain(2))
    }

    "List all user groups" in new WithApplication(FakeApp.fakeApp){
      Users.add(user1).map(r => r must equalTo(1))
      Users.add(user2).map(r => r must equalTo(1))
      Users.add(user3).map(r => r must equalTo(1))
      Groups.add(group1).map(r => r must equalTo(1))
      Groups.add(group2).map(r => r must equalTo(1))

      UserGroups.add(1, 1).map(r => r must equalTo(1))
      UserGroups.listAll.map(r => r.length must equalTo(1))
      UserGroups.add(2, 1).map(r => r must equalTo(1))
      UserGroups.listAll.map(r => r.length must equalTo(2))
      UserGroups.add(2, 2).map(r => r must equalTo(1))
      UserGroups.listAll.map(r => r.length must equalTo(3))
      UserGroups.add(3, 2).map(r => r must equalTo(1))
      UserGroups.listAll.map(r => r.length must equalTo(4))
    }
  }
}
