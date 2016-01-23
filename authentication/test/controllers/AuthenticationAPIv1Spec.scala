package controllers

import java.util.Date

import database.{Users, User}
import models.{LogIn, NewUser, UserSession}
import models.view._
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import scala.concurrent.duration._

import play.api.test._
import play.api.test.Helpers._
import play.mvc.Http.MimeTypes
import utilities._
import scala.concurrent.ExecutionContext.Implicits.global


@RunWith(classOf[JUnitRunner])
class AuthenticationAPIv1Spec extends Specification {
  "AuthenticationAPIv1Spec" should {

    "List all logged-in users" in Statics.WithFreshDatabase {
      val list = route(FakeRequest(POST, routes.AuthenticationAPIv1.listAllLoggedIn().url)).get
      status(list) must equalTo(OK)
      contentType(list) must beSome.which(_ == MimeTypes.JSON)
    }

    "Remove a logged-in user" in Statics.WithFreshDatabase {
      val user = new UserSession(0, "test", "test@test", new Date(), new Date(), "test")
      val remove = route(FakeRequest(POST, routes.AuthenticationAPIv1.removeSession().url, Statics.jsonHeaders, user.toJson())).get

      status(remove) must equalTo(OK)
    }

    "Be bad request with bad data" in Statics.WithFreshDatabase {
      val remove = route(FakeRequest(POST, routes.AuthenticationAPIv1.removeSession().url)).get

      status(remove) must equalTo(BAD_REQUEST)
    }

    "Log In" in Statics.WithFreshDatabase {
      val password = "password"
      val salt = Passwords.getNextSalt
      val hash = Passwords.hash(password, salt)

      val user = new User("name", "email@email", hash, salt, "apikey")

      val login = new LogIn(user.Email, password, "service")
      val response = route(FakeRequest(POST, routes.AuthenticationAPIv1.logIn().url, Statics.jsonHeaders, login.toJson())).get
      status(response) must equalTo(OK)
    }

    "Fail to log in invalid user" in Statics.WithFreshDatabase {
      val password = "password"
      val salt = Passwords.getNextSalt
      val hash = Passwords.hash(password, salt)
      val user = new User("name", "email@email", hash, salt, "apikey")

      val login = new LogIn("foo@foo", "password", "service")
      val response = route(FakeRequest(POST, routes.AuthenticationAPIv1.logIn().url, Statics.jsonHeaders, login.toJson())).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to log in wrong password" in Statics.WithFreshDatabase {
      val password = "password"
      val salt = Passwords.getNextSalt
      val hash = Passwords.hash(password, salt)

      val user = new User("name", "email@email", hash, salt, "apikey")

      val login = new LogIn("email@email", "something", "service")
      val response = route(FakeRequest(POST, routes.AuthenticationAPIv1.logIn().url, Statics.jsonHeaders, login.toJson())).get
      status(response) must equalTo(BAD_REQUEST)
    }
  }
}
