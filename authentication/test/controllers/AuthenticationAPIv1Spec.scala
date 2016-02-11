package controllers

import java.util.Date

import auth.models.{LogIn, AuthToken}
import database._
import implementors.AuthenticationCache
import models.UserSession
import org.junit.runner._
import org.sedis.Pool
import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.specification.BeforeEach
import play.api.Play
import play.api.test.Helpers._
import play.api.test._
import play.mvc.Http.MimeTypes
import utilities._

import scala.concurrent.Await
import scala.concurrent.duration._


@RunWith(classOf[JUnitRunner])
class AuthenticationAPIv1Spec extends Specification with BeforeEach{

  def before = {
    val pool = Play.current.injector.instanceOf[Pool]
    pool.withJedisClient(c => c.flushAll())
  }

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

      Await.result(Users.add(user), 2.seconds) must equalTo(1)
      Await.result(Groups.add(new Group("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(new Permission("Permission", "Description")), 2.seconds) must equalTo(1)

      Await.result(UserGroups.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(1, 1), 2.seconds) must equalTo(1)

      val login = new LogIn(user.Email, password, "Permission")
      val response = route(FakeRequest(POST, routes.AuthenticationAPIv1.logIn().url, Statics.jsonHeaders, login.toJson())).get
      status(response) must equalTo(OK)
    }

    "Fail to log in user without permissions" in Statics.WithFreshDatabase {
      val password = "password"
      val salt = Passwords.getNextSalt
      val hash = Passwords.hash(password, salt)
      val user = new User("name", "email@email", hash, salt, "apikey")
      Await.result(Users.add(user), 2.seconds) must equalTo(1)

      val login = new LogIn(user.Email, password, "service")
      val response = route(FakeRequest(POST, routes.AuthenticationAPIv1.logIn().url, Statics.jsonHeaders, login.toJson())).get
      status(response) must equalTo(BAD_REQUEST)
      contentAsString(response) must equalTo("User doesn't have permissions")
    }

    "Fail to log in invalid user" in Statics.WithFreshDatabase {
      val password = "password"
      val salt = Passwords.getNextSalt
      val hash = Passwords.hash(password, salt)
      val user = new User("name", "email@email", hash, salt, "apikey")
      Await.result(Users.add(user), 2.seconds) must equalTo(1)

      val login = new LogIn("foo@foo", "password", "service")
      val response = route(FakeRequest(POST, routes.AuthenticationAPIv1.logIn().url, Statics.jsonHeaders, login.toJson())).get
      status(response) must equalTo(BAD_REQUEST)
      contentAsString(response) must equalTo("User not found")
    }

    "Fail to log in wrong password" in Statics.WithFreshDatabase {
      val password = "password"
      val salt = Passwords.getNextSalt
      val hash = Passwords.hash(password, salt)

      val user = new User("name", "email@email", hash, salt, "apikey")
      Await.result(Users.add(user), 2.seconds) must equalTo(1)

      val login = new LogIn("email@email", "something", "service")
      val response = route(FakeRequest(POST, routes.AuthenticationAPIv1.logIn().url, Statics.jsonHeaders, login.toJson())).get
      status(response) must equalTo(BAD_REQUEST)
      contentAsString(response) must equalTo("Invalid password")
    }

    "Authenticate with API Key" in Statics.WithFreshDatabase {
      val password = "password"
      val salt = Passwords.getNextSalt
      val hash = Passwords.hash(password, salt)

      val user = new User("name", "email@email", hash, salt, "apikey")

      Await.result(Users.add(user), 2.seconds) must equalTo(1)
      Await.result(Groups.add(new Group("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(new Permission("Permission", "Description")), 2.seconds) must equalTo(1)

      Await.result(UserGroups.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(1, 1), 2.seconds) must equalTo(1)

      val login = new AuthToken(user.ApiKey, "Permission")
      val response = route(FakeRequest(POST, routes.AuthenticationAPIv1.authenticateApiKey().url, Statics.jsonHeaders, login.toJson())).get
      status(response) must equalTo(OK)
    }

    "Fail to authenticate with API Key user without permissions" in Statics.WithFreshDatabase {
      val password = "password"
      val salt = Passwords.getNextSalt
      val hash = Passwords.hash(password, salt)
      val user = new User("name", "email@email", hash, salt, "apikey")
      Await.result(Users.add(user), 2.seconds) must equalTo(1)

      val login = new AuthToken(user.ApiKey, "service")
      val response = route(FakeRequest(POST, routes.AuthenticationAPIv1.authenticateApiKey().url, Statics.jsonHeaders, login.toJson())).get
      status(response) must equalTo(BAD_REQUEST)
      contentAsString(response) must equalTo("User doesn't have permissions")
    }

    "Fail to authenticate with API Key invalid user" in Statics.WithFreshDatabase {
      val password = "password"
      val salt = Passwords.getNextSalt
      val hash = Passwords.hash(password, salt)
      val user = new User("name", "email@email", hash, salt, "apikey")
      Await.result(Users.add(user), 2.seconds) must equalTo(1)

      val login = new AuthToken("123", "service")
      val response = route(FakeRequest(POST, routes.AuthenticationAPIv1.authenticateApiKey().url, Statics.jsonHeaders, login.toJson())).get
      status(response) must equalTo(BAD_REQUEST)
      contentAsString(response) must equalTo("User not found")
    }

    "Authenticate with Session" in Statics.WithFreshDatabase {
      val password = "password"
      val salt = Passwords.getNextSalt
      val hash = Passwords.hash(password, salt)

      val user = new User("name", "email@email", hash, salt, "apikey")

      Await.result(Users.add(user), 2.seconds) must equalTo(1)
      Await.result(Groups.add(new Group("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(new Permission("Permission", "Description")), 2.seconds) must equalTo(1)

      Await.result(UserGroups.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(1, 1), 2.seconds) must equalTo(1)

      val userSession = new UserSession(user.Id, user.Name, user.Email, new Date(), new Date(), "test")
      val cache = Play.current.injector.instanceOf[AuthenticationCache]
      cache.getAllLoggedIn().length must equalTo(0)
      val session = cache.createSession(userSession)
      session mustNotEqual ""

      val login = new AuthToken(session, "Permission")
      val response = route(FakeRequest(POST, routes.AuthenticationAPIv1.authenticateSession().url, Statics.jsonHeaders, login.toJson())).get
      status(response) must equalTo(OK)
    }

    "Fail to authenticate with Session user without permissions" in Statics.WithFreshDatabase {
      val password = "password"
      val salt = Passwords.getNextSalt
      val hash = Passwords.hash(password, salt)
      val user = new User("name", "email@email", hash, salt, "apikey")
      Await.result(Users.add(user), 2.seconds) must equalTo(1)

      val userSession = new UserSession(user.Id, user.Name, user.Email, new Date(), new Date(), "test")
      val cache = Play.current.injector.instanceOf[AuthenticationCache]
      cache.getAllLoggedIn().length must equalTo(0)
      val session = cache.createSession(userSession)
      session mustNotEqual ""

      val login = new AuthToken(session, "service")
      val response = route(FakeRequest(POST, routes.AuthenticationAPIv1.authenticateSession().url, Statics.jsonHeaders, login.toJson())).get
      status(response) must equalTo(BAD_REQUEST)
      contentAsString(response) must equalTo("User doesn't have permissions")
    }

    "Fail to authenticate with Session invalid user" in Statics.WithFreshDatabase {
      val password = "password"
      val salt = Passwords.getNextSalt
      val hash = Passwords.hash(password, salt)
      val user = new User("name", "email@email", hash, salt, "apikey")
      Await.result(Users.add(user), 2.seconds) must equalTo(1)

      val login = new AuthToken("123", "service")
      val response = route(FakeRequest(POST, routes.AuthenticationAPIv1.authenticateSession().url, Statics.jsonHeaders, login.toJson())).get
      status(response) must equalTo(BAD_REQUEST)
      contentAsString(response) must equalTo("User not found")
    }
  }
}
