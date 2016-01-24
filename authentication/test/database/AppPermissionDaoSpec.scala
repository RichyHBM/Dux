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
class AppPermissionDaoSpec extends Specification {

  def app1 = new App("App1", "App 1 Description")
  def app2 = new App("App2", "App 2 Description")
  def app3 = new App("App3", "App 3 Description")

  def permission1 = new Permission("Permission1", "Permission 1 Description")
  def permission2 = new Permission("Permission2", "Permission 2 Description")
  def permission3 = new Permission("Permission3", "Permission 3 Description")

  "AppPermissionDaoSpec" should {
    "Add group permission" in Statics.WithFreshDatabase {
      Await.result(Apps.add(app1), 2.seconds) must equalTo(1)
      Await.result(Apps.add(app2), 2.seconds) must equalTo(1)
      Await.result(Apps.add(app3), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission3), 2.seconds) must equalTo(1)

      Await.result(AppPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(1, 2), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(2, 1), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(2, 3), 2.seconds) must equalTo(1)
    }

    "Add group permissions" in Statics.WithFreshDatabase {
      Await.result(Apps.add(app1), 2.seconds) must equalTo(1)
      Await.result(Apps.add(app2), 2.seconds) must equalTo(1)
      Await.result(Apps.add(app3), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission3), 2.seconds) must equalTo(1)

      val appPermissions = Seq(
        AppPermission(0, 1, 1),
        AppPermission(0, 1, 2),
        AppPermission(0, 2, 1),
        AppPermission(0, 2, 3)
      )

      Await.result(AppPermissions.add(appPermissions), 2.seconds) must equalTo(4)
    }

    "Not add invalid" in Statics.WithFreshDatabase {
      Await.result(Apps.add(app1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      try {
        Await.result(AppPermissions.add(1, 100), 2.seconds) must equalTo(0)
        false must equalTo(true)
      } catch {
        case _: Exception => true must equalTo(true)
      }
      try {
        Await.result(AppPermissions.add(100, 1), 2.seconds) must equalTo(0)
        false must equalTo(true)
      } catch {
        case _: Exception => true must equalTo(true)
      }
    }

    "Not add duplicates" in Statics.WithFreshDatabase {
      Await.result(Apps.add(app1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)

      Await.result(AppPermissions.add(1, 1), 2.seconds) must equalTo(1)
      try {
        Await.result(AppPermissions.add(1, 1), 2.seconds) must equalTo(0)
        false must equalTo(true)
      } catch {
        case _: Exception => true must equalTo(true)
      }
    }

    "Delete app permissions" in Statics.WithFreshDatabase {
      Await.result(Apps.add(app1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)

      Await.result(AppPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.delete(1), 2.seconds) must equalTo(1)
    }

    "Delete by app" in Statics.WithFreshDatabase {
      Await.result(Apps.add(app1), 2.seconds) must equalTo(1)
      Await.result(Apps.add(app2), 2.seconds) must equalTo(1)
      Await.result(Apps.add(app3), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission3), 2.seconds) must equalTo(1)

      Await.result(AppPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(1, 2), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(2, 1), 2.seconds) must equalTo(1)

      Await.result(AppPermissions.deleteAllFromAppId(1), 2.seconds) must equalTo(2)
    }

    "Delete by permission" in Statics.WithFreshDatabase {
      Await.result(Apps.add(app1), 2.seconds) must equalTo(1)
      Await.result(Apps.add(app2), 2.seconds) must equalTo(1)
      Await.result(Apps.add(app3), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission3), 2.seconds) must equalTo(1)

      Await.result(AppPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(1, 2), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(2, 1), 2.seconds) must equalTo(1)

      Await.result(AppPermissions.deleteAllFromPermissionId(1), 2.seconds) must equalTo(2)
    }

    "List all permissions for app" in Statics.WithFreshDatabase {
      Await.result(Apps.add(app1), 2.seconds) must equalTo(1)
      Await.result(Apps.add(app2), 2.seconds) must equalTo(1)
      Await.result(Apps.add(app3), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission3), 2.seconds) must equalTo(1)

      Await.result(AppPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(1, 2), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(2, 1), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(2, 3), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(3, 3), 2.seconds) must equalTo(1)

      val r = Await.result(AppPermissions.getAllPermissionIdsFromAppId(1), 2.seconds)
      r must contain(1)
      r must contain(2)
      r must not contain(3)
    }

    "List all permissions for apps" in Statics.WithFreshDatabase {
      Await.result(Apps.add(app1), 2.seconds) must equalTo(1)
      Await.result(Apps.add(app2), 2.seconds) must equalTo(1)
      Await.result(Apps.add(app3), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission3), 2.seconds) must equalTo(1)

      Await.result(AppPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(2, 2), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(3, 3), 2.seconds) must equalTo(1)

      val r = Await.result(AppPermissions.getAllPermissionIdsFromAppIds(List(1, 2)), 2.seconds)
      r must contain(1)
      r must contain(2)
      r must not contain(3)
    }

    "List all apps with permission" in Statics.WithFreshDatabase {
      Await.result(Apps.add(app1), 2.seconds) must equalTo(1)
      Await.result(Apps.add(app2), 2.seconds) must equalTo(1)
      Await.result(Apps.add(app3), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission3), 2.seconds) must equalTo(1)

      Await.result(AppPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(1, 2), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(2, 1), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(2, 3), 2.seconds) must equalTo(1)

      val r = Await.result(AppPermissions.getAllAppIdsFromPermissionId(1), 2.seconds)
      r must contain(1)
      r must contain(2)
      r must not contain(3)
    }

    "List all apps with permissions" in Statics.WithFreshDatabase {
      Await.result(Apps.add(app1), 2.seconds) must equalTo(1)
      Await.result(Apps.add(app2), 2.seconds) must equalTo(1)
      Await.result(Apps.add(app3), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission3), 2.seconds) must equalTo(1)

      Await.result(AppPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(2, 2), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(3, 3), 2.seconds) must equalTo(1)

      val r = Await.result(AppPermissions.getAllAppIdsFromPermissionIds(List(1, 2)), 2.seconds)
      r must contain(1)
      r must contain(2)
      r must not contain(3)
    }

    "List all" in Statics.WithFreshDatabase {
      Await.result(Apps.add(app1), 2.seconds) must equalTo(1)
      Await.result(Apps.add(app2), 2.seconds) must equalTo(1)
      Await.result(Apps.add(app3), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission3), 2.seconds) must equalTo(1)

      Await.result(AppPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.listAll(), 2.seconds).length must equalTo(1)
      Await.result(AppPermissions.add(1, 2), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.listAll(), 2.seconds).length must equalTo(2)
      Await.result(AppPermissions.add(2, 1), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.listAll(), 2.seconds).length must equalTo(3)
      Await.result(AppPermissions.add(2, 3), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.listAll(), 2.seconds).length must equalTo(4)
    }
  }
}
