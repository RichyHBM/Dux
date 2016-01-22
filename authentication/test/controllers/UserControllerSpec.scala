package controllers

import java.util.Date

import database.{Users, User}
import models.NewUser
import models.view.ViewUser
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}
import utilities._
import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class UserControllerSpec extends Specification {
  def user = new User(
    "Test User",
    "test@test.com",
    Array.fill[Byte](5)(0),
    Array.fill[Byte](5)(0),
    "test_api_key")

  "UserControllerSpec" should {

    "Add user" in new WithApplication {
      Users.add(user).map(r => {
        val u = new NewUser("John Doe", "test@test.test", "Password", "Password")
        val response = route(FakeRequest(POST, routes.UserController.newUser().url, Statics.jsonHeaders, u.toJson())).get
        status(response) must equalTo(OK)
      })
    }

    "Edit user" in new WithApplication {
      Users.add(user).map(r => {
        val u = new ViewUser(1, "Test2", "email@email", "apikey", new Date(), 0, false)
        val response = route(FakeRequest(POST, routes.UserController.editUser().url, Statics.jsonHeaders, u.toJson())).get
        status(response) must equalTo(OK)
      })
    }

    "Get all users" in new WithApplication {
      Users.add(user).map(r => {
        val response = route(FakeRequest(POST, routes.UserController.getAllUsers().url)).get
        status(response) must equalTo(OK)
        Json.fromJson[Array[ViewUser]](contentAsJson(response)).fold(
          e => 1 must equalTo(0),
          ar => ar.length must equalTo(1)
        )
      })
    }

    "Delete user" in new WithApplication {
      Users.add(user).map(r => {
        val e = new ViewUser(1, "Test2", "email@email", "apikey", new Date(), 0, false)
        val response = route(FakeRequest(POST, routes.UserController.deleteUser().url, Statics.jsonHeaders, e.toJson())).get
        status(response) must equalTo(OK)
      })
    }

    "Fail to add new user for invalid json" in new WithApplication {
      val response = route(FakeRequest(POST, routes.UserController.newUser().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to add new user for invalid password" in new WithApplication {
      val u = new NewUser("John Doe", "test@test.test", "Password", "OtherPassword")
      val response = route(FakeRequest(POST, routes.UserController.newUser().url, Statics.jsonHeaders, u.toJson())).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to edit user for invalid password" in new WithApplication {
      val response = route(FakeRequest(POST, routes.UserController.editUser().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to delete user for invalid password" in new WithApplication {
      val response = route(FakeRequest(POST, routes.UserController.deleteUser().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }
  }
}