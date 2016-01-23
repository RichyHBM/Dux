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
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class UserControllerSpec extends Specification {
  val bytes = Array.fill[Byte](5)(0)
  val user1 = new User("Test User", "test1@test.com", bytes, bytes, "test_api_key_1")

  "UserControllerSpec" should {

    "Add user" in Statics.WithFreshDatabase {
      val u = new NewUser("John Doe", "test@test.test", "Password", "Password")
      val response = route(FakeRequest(POST, routes.UserController.newUser().url, Statics.jsonHeaders, u.toJson())).get
      status(response) must equalTo(OK)
    }

    "Edit user" in Statics.WithFreshDatabase {
      Await.result(Users.add(user1), 2.seconds) must equalTo(1)
      val u = new ViewUser(1, "Test2", "email@email", "apikey", new Date(), 0, false)
      val response = route(FakeRequest(POST, routes.UserController.editUser().url, Statics.jsonHeaders, u.toJson())).get
      status(response) must equalTo(OK)
    }

    "Get all users" in Statics.WithFreshDatabase {
      Await.result(Users.add(user1), 2.seconds) must equalTo(1)
      val response = route(FakeRequest(POST, routes.UserController.getAllUsers().url)).get
      status(response) must equalTo(OK)
      Json.fromJson[Array[ViewUser]](contentAsJson(response)).fold(
        e => 1 must equalTo(0),
        ar => ar.length must equalTo(1)
      )
    }

    "Delete user" in Statics.WithFreshDatabase {
      val e = new ViewUser(1, "Test2", "email@email", "apikey", new Date(), 0, false)
      val response = route(FakeRequest(POST, routes.UserController.deleteUser().url, Statics.jsonHeaders, e.toJson())).get
      status(response) must equalTo(OK)
    }

    "Fail to add new user for invalid json" in Statics.WithFreshDatabase {
      val response = route(FakeRequest(POST, routes.UserController.newUser().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to add new user for invalid password" in Statics.WithFreshDatabase {
      val u = new NewUser("John Doe", "test@test.test", "Password", "OtherPassword")
      val response = route(FakeRequest(POST, routes.UserController.newUser().url, Statics.jsonHeaders, u.toJson())).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to edit user for invalid password" in Statics.WithFreshDatabase {
      val response = route(FakeRequest(POST, routes.UserController.editUser().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to delete user for invalid password" in Statics.WithFreshDatabase {
      val response = route(FakeRequest(POST, routes.UserController.deleteUser().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }
  }
}