package controllers

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}
import utilities._

@RunWith(classOf[JUnitRunner])
class PermissionControllerSpec extends Specification {

  "PermissionControllerSpec" should {
    "Fail to add new permission for invalid json" in new WithApplication{
      val response = route(FakeRequest(POST, routes.PermissionController.newPermission().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to edit permission for invalid json" in new WithApplication{
      val response = route(FakeRequest(POST, routes.PermissionController.editPermission().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to delete permission for invalid json" in new WithApplication{
      val response = route(FakeRequest(POST, routes.PermissionController.deletePermission().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }
  }
}