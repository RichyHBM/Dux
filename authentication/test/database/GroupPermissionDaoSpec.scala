package database

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class GroupPermissionDaoSpec extends Specification {

  def group1 = new Group("Group1", "Group 1 Description")
  def group2 = new Group("Group2", "Group 2 Description")
  def group3 = new Group("Group3", "Group 3 Description")

  def permission1 = new Permission("Permission1", "Permission 1 Description")
  def permission2 = new Permission("Permission2", "Permission 2 Description")
  def permission3 = new Permission("Permission3", "Permission 3 Description")

  "GroupPermissionDaoSpec" should {
    "Add group permission" in new WithApplication(FakeApp.fakeApp){
      Groups.add(group1).map(r => r must equalTo(1))
      Groups.add(group2).map(r => r must equalTo(1))
      Groups.add(group3).map(r => r must equalTo(1))

      Permissions.add(permission1).map(r => r must equalTo(1))
      Permissions.add(permission2).map(r => r must equalTo(1))
      Permissions.add(permission3).map(r => r must equalTo(1))

      GroupPermissions.add(1, 1).map(r => r must equalTo(1))
      GroupPermissions.add(1, 2).map(r => r must equalTo(1))
      GroupPermissions.add(2, 1).map(r => r must equalTo(1))
      GroupPermissions.add(2, 3).map(r => r must equalTo(1))
    }

    "Not add invalid" in new WithApplication(FakeApp.fakeApp){
      Groups.add(group1).map(r => r must equalTo(1))

      Permissions.add(permission1).map(r => r must equalTo(1))

      GroupPermissions.add(1, 100).map(r => r must equalTo(0))
      GroupPermissions.add(100, 1).map(r => r must equalTo(0))
    }

    "Not add duplicates" in new WithApplication(FakeApp.fakeApp){
      Groups.add(group1).map(r => r must equalTo(1))
      Permissions.add(permission1).map(r => r must equalTo(1))

      GroupPermissions.add(1, 1).map(r => r must equalTo(1))
      GroupPermissions.add(1, 1).map(r => r must equalTo(0))
    }

    "Delete group permissions" in new WithApplication(FakeApp.fakeApp){
      Groups.add(group1).map(r => r must equalTo(1))
      Permissions.add(permission1).map(r => r must equalTo(1))

      GroupPermissions.add(1, 1).map(r => r must equalTo(1))
      GroupPermissions.delete(1).map(r => r must equalTo(1))
    }

    "Delete by group" in new WithApplication(FakeApp.fakeApp){
      Groups.add(group1).map(r => r must equalTo(1))
      Groups.add(group2).map(r => r must equalTo(1))
      Groups.add(group3).map(r => r must equalTo(1))

      Permissions.add(permission1).map(r => r must equalTo(1))
      Permissions.add(permission2).map(r => r must equalTo(1))
      Permissions.add(permission3).map(r => r must equalTo(1))

      GroupPermissions.add(1, 1).map(r => r must equalTo(1))
      GroupPermissions.add(1, 2).map(r => r must equalTo(1))
      GroupPermissions.add(2, 1).map(r => r must equalTo(1))

      GroupPermissions.deleteAllFromGroupId(1).map(r => r must equalTo(2))
    }

    "Delete by permission" in new WithApplication(FakeApp.fakeApp){
      Groups.add(group1).map(r => r must equalTo(1))
      Groups.add(group2).map(r => r must equalTo(1))
      Groups.add(group3).map(r => r must equalTo(1))

      Permissions.add(permission1).map(r => r must equalTo(1))
      Permissions.add(permission2).map(r => r must equalTo(1))
      Permissions.add(permission3).map(r => r must equalTo(1))

      GroupPermissions.add(1, 1).map(r => r must equalTo(1))
      GroupPermissions.add(1, 2).map(r => r must equalTo(1))
      GroupPermissions.add(2, 1).map(r => r must equalTo(1))

      GroupPermissions.deleteAllFromPermissionId(1).map(r => r must equalTo(2))
    }

    "List all permissions for group" in new WithApplication(FakeApp.fakeApp){
      Groups.add(group1).map(r => r must equalTo(1))
      Groups.add(group2).map(r => r must equalTo(1))
      Groups.add(group3).map(r => r must equalTo(1))

      Permissions.add(permission1).map(r => r must equalTo(1))
      Permissions.add(permission2).map(r => r must equalTo(1))
      Permissions.add(permission3).map(r => r must equalTo(1))

      GroupPermissions.add(1, 1).map(r => r must equalTo(1))
      GroupPermissions.add(1, 2).map(r => r must equalTo(1))
      GroupPermissions.add(2, 1).map(r => r must equalTo(1))
      GroupPermissions.add(2, 3).map(r => r must equalTo(1))

      GroupPermissions.getAllPermissionIdsFromGroupId(1).map(r => r must contain(1))
      GroupPermissions.getAllPermissionIdsFromGroupId(1).map(r => r must contain(2))
      GroupPermissions.getAllPermissionIdsFromGroupId(1).map(r => r must not contain(3))
    }

    "List all groups from permission" in new WithApplication(FakeApp.fakeApp){
      Groups.add(group1).map(r => r must equalTo(1))
      Groups.add(group2).map(r => r must equalTo(1))
      Groups.add(group3).map(r => r must equalTo(1))

      Permissions.add(permission1).map(r => r must equalTo(1))
      Permissions.add(permission2).map(r => r must equalTo(1))
      Permissions.add(permission3).map(r => r must equalTo(1))

      GroupPermissions.add(1, 1).map(r => r must equalTo(1))
      GroupPermissions.add(1, 2).map(r => r must equalTo(1))
      GroupPermissions.add(2, 1).map(r => r must equalTo(1))
      GroupPermissions.add(2, 3).map(r => r must equalTo(1))
      GroupPermissions.add(3, 3).map(r => r must equalTo(1))

      GroupPermissions.getAllGroupIdsFromPermissionId(1).map(r => r must contain(1))
      GroupPermissions.getAllGroupIdsFromPermissionId(1).map(r => r must contain(2))
      GroupPermissions.getAllGroupIdsFromPermissionId(1).map(r => r must not contain(3))
    }

    "List all" in new WithApplication(FakeApp.fakeApp){
      Groups.add(group1).map(r => r must equalTo(1))
      Groups.add(group2).map(r => r must equalTo(1))
      Groups.add(group3).map(r => r must equalTo(1))

      Permissions.add(permission1).map(r => r must equalTo(1))
      Permissions.add(permission2).map(r => r must equalTo(1))
      Permissions.add(permission3).map(r => r must equalTo(1))

      GroupPermissions.add(1, 1).map(r => r must equalTo(1))
      GroupPermissions.listAll().map(r => r must equalTo(1))
      GroupPermissions.add(1, 2).map(r => r must equalTo(1))
      GroupPermissions.listAll().map(r => r must equalTo(2))
      GroupPermissions.add(2, 1).map(r => r must equalTo(1))
      GroupPermissions.listAll().map(r => r must equalTo(3))
      GroupPermissions.add(2, 3).map(r => r must equalTo(1))
      GroupPermissions.listAll().map(r => r must equalTo(4))
    }
  }
}
