package controllers

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.test.Helpers._
import play.api.test.{FakeHeaders, FakeRequest, WithApplication}
import play.mvc.Http.MimeTypes

@RunWith(classOf[JUnitRunner])
class AppControllerSpec extends Specification {
  val jsonHeaders = FakeHeaders(Seq((CONTENT_TYPE, MimeTypes.JSON)))

  "AppControllerSpec" should {
    "Fail to add new app for invalid json" in new WithApplication{
      val add = route(FakeRequest(POST, routes.AppController.newApp().url, jsonHeaders, "{}")).get
      status(add) must equalTo(BAD_REQUEST)
    }

    "Fail to edit app for invalid json" in new WithApplication{
      val add = route(FakeRequest(POST, routes.AppController.editApp().url, jsonHeaders, "{}")).get
      status(add) must equalTo(BAD_REQUEST)
    }
  }
}