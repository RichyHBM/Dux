package database

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import utilities._
import scala.concurrent.duration._
import scala.concurrent.Await
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
    "Add group permission" in Statics.WithFreshDatabase {
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group3), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission3), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(1, 2), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(2, 1), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(2, 3), 2.seconds) must equalTo(1)
    }

    "Not add invalid" in Statics.WithFreshDatabase {
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.add(1, 100), 2.seconds) must equalTo(0)
      Await.result(GroupPermissions.add(100, 1), 2.seconds) must equalTo(0)
    }

    "Not add duplicates" in Statics.WithFreshDatabase {
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.add(1, 1), 2.seconds) must equalTo(1)
      try {
        Await.result(GroupPermissions.add(1, 1), 2.seconds) must equalTo(0)
        false must equalTo(true)
      } catch {
        case _: Exception => true must equalTo(true)
      }
    }

    "Delete group permissions" in Statics.WithFreshDatabase {
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.delete(1), 2.seconds) must equalTo(1)
    }

    "Delete by group" in Statics.WithFreshDatabase {
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group3), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission3), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(1, 2), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(2, 1), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.deleteAllFromGroupId(1), 2.seconds) must equalTo(2)
    }

    "Delete by permission" in Statics.WithFreshDatabase {
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group3), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission3), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(1, 2), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(2, 1), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.deleteAllFromPermissionId(1), 2.seconds) must equalTo(2)
    }

    "List all permissions for group" in Statics.WithFreshDatabase {
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group3), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission3), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(1, 2), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(2, 1), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(2, 3), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.getAllPermissionIdsFromGroupId(1), 2.seconds) must contain(1)
      Await.result(GroupPermissions.getAllPermissionIdsFromGroupId(1), 2.seconds) must contain(2)
      Await.result(GroupPermissions.getAllPermissionIdsFromGroupId(1), 2.seconds) must not contain(3)
    }

    "List all permissions for groups" in Statics.WithFreshDatabase {
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group3), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission3), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(2, 2), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(3, 3), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.getAllPermissionIdsFromGroupIds(List(1, 2)), 2.seconds) must contain(1)
      Await.result(GroupPermissions.getAllPermissionIdsFromGroupIds(List(1, 2)), 2.seconds) must contain(2)
      Await.result(GroupPermissions.getAllPermissionIdsFromGroupIds(List(1, 2)), 2.seconds) must not contain(3)
    }

    "List all groups from permission" in Statics.WithFreshDatabase {
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group3), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission3), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(1, 2), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(2, 1), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(2, 3), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(3, 3), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.getAllGroupIdsFromPermissionId(1), 2.seconds) must contain(1)
      Await.result(GroupPermissions.getAllGroupIdsFromPermissionId(1), 2.seconds) must contain(2)
      Await.result(GroupPermissions.getAllGroupIdsFromPermissionId(1), 2.seconds) must not contain(3)
    }

    "List all groups from permissions" in Statics.WithFreshDatabase {
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group3), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission3), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(2, 2), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(3, 3), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.getAllGroupIdsFromPermissionIds(List(1, 2)), 2.seconds) must contain(1)
      Await.result(GroupPermissions.getAllGroupIdsFromPermissionIds(List(1, 2)), 2.seconds) must contain(2)
      Await.result(GroupPermissions.getAllGroupIdsFromPermissionIds(List(1, 2)), 2.seconds) must not contain(3)
    }

    "List all" in Statics.WithFreshDatabase {
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group3), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission3), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.listAll(), 2.seconds).length must equalTo(1)
      Await.result(GroupPermissions.add(1, 2), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.listAll(), 2.seconds).length must equalTo(2)
      Await.result(GroupPermissions.add(2, 1), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.listAll(), 2.seconds).length must equalTo(3)
      Await.result(GroupPermissions.add(2, 3), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.listAll(), 2.seconds).length must equalTo(4)
    }
  }
}
