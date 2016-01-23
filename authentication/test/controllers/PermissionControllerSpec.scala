package controllers

import database.{Permission, Permissions}
import models.view.ViewPermission
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