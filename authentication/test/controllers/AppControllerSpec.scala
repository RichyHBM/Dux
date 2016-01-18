package controllers

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}
import utilities._

@RunWith(classOf[JUnitRunner])
class AppControllerSpec extends Specification {

  "AppControllerSpec" should {
    "Fail to add new app for invalid json" in new WithApplication{
      val response = route(FakeRequest(POST, routes.AppController.newApp().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to edit app for invalid json" in new WithApplication{
      val response = route(FakeRequest(POST, routes.AppController.editApp().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to delete app for invalid json" in new WithApplication{
      val response = route(FakeRequest(POST, routes.AppController.deleteApp().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }
  }
}