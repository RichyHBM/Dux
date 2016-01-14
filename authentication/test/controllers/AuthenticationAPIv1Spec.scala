package controllers

import java.util.Date

import database.FakeApp
import models.{NewUser, UserSession}
import models.view._
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import play.mvc.Http.MimeTypes


@RunWith(classOf[JUnitRunner])
class AuthenticationAPIv1Spec extends Specification {
  val jsonHeaders = FakeHeaders(Seq((CONTENT_TYPE, MimeTypes.JSON)))

  "AuthenticationAPIv1Spec" should {

    "List all logged-in users" in new WithApplication{
      val list = route(FakeRequest(POST, "/view-api/get-logged-in-users")).get
      status(list) must equalTo(OK)
      contentType(list) must beSome.which(_ == MimeTypes.JSON)
    }

    "Remove a logged-in user" in new WithApplication{
      val user = new UserSession(0, "test", "test@test", new Date(), new Date(), "test")
      val remove = route(FakeRequest(POST, "/view-api/delete-session", jsonHeaders, user.toJson())).get

      status(remove) must equalTo(OK)
    }

    "Be bad request with bad data" in new WithApplication{
      val remove = route(FakeRequest(POST, "/view-api/delete-session")).get

      status(remove) must equalTo(BAD_REQUEST)
    }

    "Fail to add new user for invalid json" in new WithApplication{
      val add = route(FakeRequest(POST, "/view-api/new-user", jsonHeaders, "{}")).get
      status(add) must equalTo(BAD_REQUEST)
    }

    "Fail to add new user for invalid password" in new WithApplication{
      val u = new NewUser("John Doe", "test@test.test", "Password", "OtherPassword")
      val add = route(FakeRequest(POST, "/view-api/new-user", jsonHeaders, u.toJson())).get
      status(add) must equalTo(BAD_REQUEST)
    }

    "Fail to add new group for invalid json" in new WithApplication{
      val add = route(FakeRequest(POST, "/view-api/new-group", jsonHeaders, "{}")).get
      status(add) must equalTo(BAD_REQUEST)
    }

    "Fail to add new app for invalid json" in new WithApplication{
      val add = route(FakeRequest(POST, "/view-api/new-app", jsonHeaders, "{}")).get
      status(add) must equalTo(BAD_REQUEST)
    }

    "Fail to add new permission for invalid json" in new WithApplication{
      val add = route(FakeRequest(POST, "/view-api/new-permission", jsonHeaders, "{}")).get
      status(add) must equalTo(BAD_REQUEST)
    }
  }
}

