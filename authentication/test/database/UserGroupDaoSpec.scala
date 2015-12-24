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
  val timestamp = new Timestamp(new Date().getTime())

  def user1 = User(1, "User1", "user1@test.com", bytes, bytes, "api1", timestamp, 0, false)
  def user2 = User(2, "User2", "user2@test.com", bytes, bytes, "api2", timestamp, 0, false)
  def user3 = User(3, "User3", "user3@test.com", bytes, bytes, "api3", timestamp, 0, false)

  def group1 = Group(1, "Group1", "Group 1 Description")
  def group2 = Group(2, "Group2", "Group 2 Description")

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
