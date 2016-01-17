package controllers

import models.NewUser
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.test.Helpers._
import play.api.test.{FakeHeaders, FakeRequest, WithApplication}
import play.mvc.Http.MimeTypes

@RunWith(classOf[JUnitRunner])
class UserControllerSpec extends Specification {
  val jsonHeaders = FakeHeaders(Seq((CONTENT_TYPE, MimeTypes.JSON)))

  "UserControllerSpec" should {
    "Fail to add new user for invalid json" in new WithApplication{
      val add = route(FakeRequest(POST, routes.UserController.newUser().url, jsonHeaders, "{}")).get
      status(add) must equalTo(BAD_REQUEST)
    }

    "Fail to add new user for invalid password" in new WithApplication{
      val u = new NewUser("John Doe", "test@test.test", "Password", "OtherPassword")
      val add = route(FakeRequest(POST, routes.UserController.newUser().url, jsonHeaders, u.toJson())).get
      status(add) must equalTo(BAD_REQUEST)
    }

  }
}