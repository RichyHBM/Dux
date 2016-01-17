package controllers

import models.NewUser
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}
import utilities._

@RunWith(classOf[JUnitRunner])
class UserControllerSpec extends Specification {

  "UserControllerSpec" should {

    "Fail to add new user for invalid json" in new WithApplication{
      val add = route(FakeRequest(POST, routes.UserController.newUser().url, Statics.jsonHeaders, "{}")).get
      status(add) must equalTo(BAD_REQUEST)
    }

    "Fail to add new user for invalid password" in new WithApplication{
      val u = new NewUser("John Doe", "test@test.test", "Password", "OtherPassword")
      val add = route(FakeRequest(POST, routes.UserController.newUser().url, Statics.jsonHeaders, u.toJson())).get
      status(add) must equalTo(BAD_REQUEST)
    }
  }
}