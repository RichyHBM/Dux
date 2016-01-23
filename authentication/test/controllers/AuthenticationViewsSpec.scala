package controllers

import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.Helpers._
import play.api.test._
import utilities._
import scala.concurrent.duration._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class AuthenticationViewsSpec extends Specification {

  "AuthenticationViews" should {

    "send 404 on a bad request" in Statics.WithFreshDatabase {
      route(FakeRequest(GET, "/boum")) must beSome.which (status(_) == NOT_FOUND)
    }

    "render the index page" in Statics.WithFreshDatabase {
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Authentication")
    }

    "render the apps page" in Statics.WithFreshDatabase {
      val home = route(FakeRequest(GET, "/apps")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Authentication")
    }

    "render the groups page" in Statics.WithFreshDatabase {
      val home = route(FakeRequest(GET, "/groups")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Authentication")
    }

    "render the permissions page" in Statics.WithFreshDatabase {
      val home = route(FakeRequest(GET, "/permissions")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Authentication")
    }

    "render the users page" in Statics.WithFreshDatabase {
      val home = route(FakeRequest(GET, "/users")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Authentication")
    }
  }
}
