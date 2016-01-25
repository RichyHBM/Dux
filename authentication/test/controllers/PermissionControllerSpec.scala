package controllers

import database._
import models.view.{ViewIdToIds, ViewId, ViewPermission}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}
import utilities._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class PermissionControllerSpec extends Specification {
  def permission1 = new Permission("Permission1", "Permission 1 Description")
  def permission2 = new Permission("Permission2", "Permission 2 Description")
  def permission3 = new Permission("Permission3", "Permission 3 Description")

  "PermissionControllerSpec" should {

    "Add permission" in Statics.WithFreshDatabase {
      val v = new ViewPermission(0, "Test", "Description")
      val response = route(FakeRequest(POST, routes.PermissionController.newPermission().url, Statics.jsonHeaders, v.toJson())).get
      status(response) must equalTo(OK)
    }

    "Edit permission" in Statics.WithFreshDatabase {
      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      val e = new ViewPermission(1, "Test2", "Description2")
      val response = route(FakeRequest(POST, routes.PermissionController.editPermission().url, Statics.jsonHeaders, e.toJson())).get
      contentAsString(response) must equalTo("1")
    }

    "Get all permissions" in Statics.WithFreshDatabase {
      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      val response = route(FakeRequest(POST, routes.PermissionController.getAllPermissions().url)).get
      status(response) must equalTo(OK)
      Json.fromJson[Array[ViewPermission]](contentAsJson(response)).fold(
        e => 1 must equalTo(0),
        ar => ar.length must equalTo(1)
      )
    }

    "Get all permissions for app" in Statics.WithFreshDatabase {
      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission3), 2.seconds) must equalTo(1)

      Await.result(Apps.add(new App("TEST", "Description")), 2.seconds) must equalTo(1)

      Await.result(AppPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(AppPermissions.add(1, 2), 2.seconds) must equalTo(1)

      val e = new ViewId(1)
      val response = route(FakeRequest(POST, routes.PermissionController.getAllPermissionsForApp().url, Statics.jsonHeaders, e.toJson())).get
      status(response) must equalTo(OK)
      Json.fromJson[Array[ViewPermission]](contentAsJson(response)).fold(
        e => 1 must equalTo(0),
        ar => {
          ar.length must equalTo(2)
          ar.head.Name must equalTo(permission1.Name)
          ar(1).Name must equalTo(permission2.Name)
        }
      )
    }

    "Add permission for app" in Statics.WithFreshDatabase {
      Await.result(Permissions.add(permission1), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(permission2), 2.seconds) must equalTo(1)

      Await.result(Apps.add(new App("TEST", "Description")), 2.seconds) must equalTo(1)

      Await.result(AppPermissions.add(1, 2), 2.seconds) must equalTo(1)

      val e = new ViewIdToIds(1, Seq(1,2))
      val response = route(FakeRequest(POST, routes.PermissionController.addPermissionsForApp().url, Statics.jsonHeaders, e.toJson())).get
      status(response) must equalTo(OK)
      contentAsString(response) must equalTo("2")

      Await.result(AppPermissions.listAll(), 2.seconds).length must equalTo(2)
    }

    "Delete permission" in Statics.WithFreshDatabase {
      val e = new ViewPermission(1, "Test2", "Description2")
      val response = route(FakeRequest(POST, routes.PermissionController.deletePermission().url, Statics.jsonHeaders, e.toJson())).get
      status(response) must equalTo(OK)
    }

    "Fail to add new permission for invalid json" in Statics.WithFreshDatabase {
      val response = route(FakeRequest(POST, routes.PermissionController.newPermission().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to edit permission for invalid json" in Statics.WithFreshDatabase {
      val response = route(FakeRequest(POST, routes.PermissionController.editPermission().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to delete permission for invalid json" in Statics.WithFreshDatabase {
      val response = route(FakeRequest(POST, routes.PermissionController.deletePermission().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }
  }
}