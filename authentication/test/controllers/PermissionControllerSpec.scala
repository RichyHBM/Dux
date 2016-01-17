package controllers

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.test.Helpers._
import play.api.test.{FakeHeaders, FakeRequest, WithApplication}
import play.mvc.Http.MimeTypes

@RunWith(classOf[JUnitRunner])
class PermissionControllerSpec extends Specification {
  val jsonHeaders = FakeHeaders(Seq((CONTENT_TYPE, MimeTypes.JSON)))

  "PermissionControllerSpec" should {
    "Fail to add new permission for invalid json" in new WithApplication{
      val add = route(FakeRequest(POST, routes.PermissionController.newPermission().url, jsonHeaders, "{}")).get
      status(add) must equalTo(BAD_REQUEST)
    }

    "Fail to edit permission for invalid json" in new WithApplication{
      val add = route(FakeRequest(POST, routes.PermissionController.editPermission().url, jsonHeaders, "{}")).get
      status(add) must equalTo(BAD_REQUEST)
    }
  }
}