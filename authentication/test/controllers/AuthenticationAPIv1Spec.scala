package controllers

import java.util.Date

import models.UserSession
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._


@RunWith(classOf[JUnitRunner])
class AuthenticationAPIv1Spec extends Specification {

  "AuthenticationAPIv1Spec" should {

    "List all users" in new WithApplication{
      val list = route(FakeRequest(POST, "/view-api/get-logged-in-users")).get
      status(list) must equalTo(OK)
      contentType(list) must beSome.which(_ == "application/json")
    }

    "Remove a user" in new WithApplication{
      val user = new UserSession(0, "test", "test@test", new Date(), new Date(), "test")
      val remove = route(FakeRequest(POST, "/view-api/delete-session", FakeHeaders(Seq(("Content-Type", "application/json"))), user.toJson())).get

      status(remove) must equalTo(OK)
    }

    "Be bad request with bad data" in new WithApplication{
      val remove = route(FakeRequest(POST, "/view-api/delete-session")).get

      status(remove) must equalTo(BAD_REQUEST)
    }
  }
}

