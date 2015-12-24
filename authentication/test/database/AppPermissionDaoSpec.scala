package database

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class AppPermissionDaoSpec extends Specification {

  def app1 = App(1, "App1", "App 1 Description")
  def app2 = App(2, "App2", "App 2 Description")
  def app3 = App(3, "App3", "App 3 Description")

  def permission1 = Permission(1, "Permission1", "Permission 1 Description")
  def permission2 = Permission(2, "Permission2", "Permission 2 Description")
  def permission3 = Permission(3, "Permission3", "Permission 3 Description")

  "AppPermissionDaoSpec" should {
    "Add group permission" in new WithApplication(FakeApp.fakeApp){
      Apps.add(app1).map(r => r must equalTo(1))
      Apps.add(app2).map(r => r must equalTo(1))
      Apps.add(app3).map(r => r must equalTo(1))

      Permissions.add(permission1).map(r => r must equalTo(1))
      Permissions.add(permission2).map(r => r must equalTo(1))
      Permissions.add(permission3).map(r => r must equalTo(1))

      AppPermissions.add(1, 1).map(r => r must equalTo(1))
      AppPermissions.add(1, 2).map(r => r must equalTo(1))
      AppPermissions.add(2, 1).map(r => r must equalTo(1))
      AppPermissions.add(2, 3).map(r => r must equalTo(1))
    }

    "Not add invalid" in new WithApplication(FakeApp.fakeApp){
      Apps.add(app1).map(r => r must equalTo(1))
      Permissions.add(permission1).map(r => r must equalTo(1))

      AppPermissions.add(1, 100).map(r => r must equalTo(0))
      AppPermissions.add(100, 1).map(r => r must equalTo(0))
    }

    "Not add duplicates" in new WithApplication(FakeApp.fakeApp){
      Apps.add(app1).map(r => r must equalTo(1))
      Permissions.add(permission1).map(r => r must equalTo(1))

      AppPermissions.add(1, 1).map(r => r must equalTo(1))
      AppPermissions.add(1, 1).map(r => r must equalTo(0))
    }

    "Delete app permissions" in new WithApplication(FakeApp.fakeApp){
      Apps.add(app1).map(r => r must equalTo(1))
      Permissions.add(permission1).map(r => r must equalTo(1))

      AppPermissions.add(1, 1).map(r => r must equalTo(1))
      AppPermissions.delete(1).map(r => r must equalTo(1))
    }

    "Delete by app" in new WithApplication(FakeApp.fakeApp){
      Apps.add(app1).map(r => r must equalTo(1))
      Apps.add(app2).map(r => r must equalTo(1))
      Apps.add(app3).map(r => r must equalTo(1))

      Permissions.add(permission1).map(r => r must equalTo(1))
      Permissions.add(permission2).map(r => r must equalTo(1))
      Permissions.add(permission3).map(r => r must equalTo(1))

      AppPermissions.add(1, 1).map(r => r must equalTo(1))
      AppPermissions.add(1, 2).map(r => r must equalTo(1))
      AppPermissions.add(2, 1).map(r => r must equalTo(1))

      AppPermissions.deleteAllFromAppId(1).map(r => r must equalTo(2))
    }

    "Delete by permission" in new WithApplication(FakeApp.fakeApp){
      Apps.add(app1).map(r => r must equalTo(1))
      Apps.add(app2).map(r => r must equalTo(1))
      Apps.add(app3).map(r => r must equalTo(1))

      Permissions.add(permission1).map(r => r must equalTo(1))
      Permissions.add(permission2).map(r => r must equalTo(1))
      Permissions.add(permission3).map(r => r must equalTo(1))

      AppPermissions.add(1, 1).map(r => r must equalTo(1))
      AppPermissions.add(1, 2).map(r => r must equalTo(1))
      AppPermissions.add(2, 1).map(r => r must equalTo(1))

      AppPermissions.deleteAllFromPermissionId(1).map(r => r must equalTo(2))
    }

    "List all" in new WithApplication(FakeApp.fakeApp){
      Apps.add(app1).map(r => r must equalTo(1))
      Apps.add(app2).map(r => r must equalTo(1))
      Apps.add(app3).map(r => r must equalTo(1))

      Permissions.add(permission1).map(r => r must equalTo(1))
      Permissions.add(permission2).map(r => r must equalTo(1))
      Permissions.add(permission3).map(r => r must equalTo(1))

      AppPermissions.add(1, 1).map(r => r must equalTo(1))
      AppPermissions.listAll().map(r => r must equalTo(1))
      AppPermissions.add(1, 2).map(r => r must equalTo(1))
      AppPermissions.listAll().map(r => r must equalTo(2))
      AppPermissions.add(2, 1).map(r => r must equalTo(1))
      AppPermissions.listAll().map(r => r must equalTo(3))
      AppPermissions.add(2, 3).map(r => r must equalTo(1))
      AppPermissions.listAll().map(r => r must equalTo(4))
    }
  }
}
