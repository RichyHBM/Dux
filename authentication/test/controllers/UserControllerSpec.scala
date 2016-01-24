package controllers

import java.util.Date

import database._
import models.NewUser
import models.view.{ViewIdToIds, ViewId, ViewUser}
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
  val user2 = new User("Test User 2", "test2@test.com", bytes, bytes, "test_api_key_2")
  val user3 = new User("Test User 3", "test3@test.com", bytes, bytes, "test_api_key_3")

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

    "Get all users in group" in Statics.WithFreshDatabase {
      Await.result(Users.add(user1), 2.seconds) must equalTo(1)
      Await.result(Users.add(user2), 2.seconds) must equalTo(1)
      Await.result(Users.add(user3), 2.seconds) must equalTo(1)

      Await.result(Groups.add(new Group("Group1", "Group 1 Description")), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(UserGroups.add(2, 1), 2.seconds) must equalTo(1)

      val e = new ViewId(1)
      val response = route(FakeRequest(POST, routes.UserController.getAllUsersInGroup().url, Statics.jsonHeaders, e.toJson())).get
      status(response) must equalTo(OK)
      Json.fromJson[Array[ViewUser]](contentAsJson(response)).fold(
        e => 1 must equalTo(0),
        ar => {
          ar.length must equalTo(2)
          ar.head.Name must equalTo("Test User")
          ar(1).Name must equalTo("Test User 2")
        }
      )
    }

    "Add users to group" in Statics.WithFreshDatabase {
      Await.result(Users.add(user1), 2.seconds) must equalTo(1)
      Await.result(Users.add(user2), 2.seconds) must equalTo(1)

      Await.result(Groups.add(new Group("Group1", "Group 1 Description")), 2.seconds) must equalTo(1)

      Await.result(UserGroups.add(2, 1), 2.seconds) must equalTo(1)

      val e = new ViewIdToIds(1, Seq(1,2))
      val response = route(FakeRequest(POST, routes.UserController.addUsersToGroup().url, Statics.jsonHeaders, e.toJson())).get
      status(response) must equalTo(OK)
      contentAsString(response) must equalTo("2")

      Await.result(UserGroups.listAll(), 2.seconds).length must equalTo(2)
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