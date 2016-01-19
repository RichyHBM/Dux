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
      val response = route(FakeRequest(POST, routes.UserController.newUser().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to add new user for invalid password" in new WithApplication{
      val u = new NewUser("John Doe", "test@test.test", "Password", "OtherPassword")
      val response = route(FakeRequest(POST, routes.UserController.newUser().url, Statics.jsonHeaders, u.toJson())).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to edit user for invalid password" in new WithApplication{
      val response = route(FakeRequest(POST, routes.UserController.editUser().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to delete user for invalid password" in new WithApplication{
      val response = route(FakeRequest(POST, routes.UserController.deleteUser().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }
  }
}