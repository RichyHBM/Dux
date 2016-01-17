package controllers

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.test.{FakeRequest, WithApplication, FakeHeaders}
import play.api.test.Helpers._
import play.mvc.Http.MimeTypes

@RunWith(classOf[JUnitRunner])
class GroupControllerSpec extends Specification {
  val jsonHeaders = FakeHeaders(Seq((CONTENT_TYPE, MimeTypes.JSON)))

  "GroupControllerSpec" should {
    "Fail to add new group for invalid json" in new WithApplication{
      val add = route(FakeRequest(POST, routes.GroupController.newGroup().url, jsonHeaders, "{}")).get
      status(add) must equalTo(BAD_REQUEST)
    }
  }
}