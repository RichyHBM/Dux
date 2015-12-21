package implementors

import java.util.Date

import models.UserSession
import modules.AuthenticationModule
import net.sf.ehcache.search.expression.EqualTo
import org.sedis.Pool
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import org.specs2.specification.BeforeEach
import play.api.inject.guice.GuiceApplicationBuilder

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class AuthenticationCacheSpec extends Specification with BeforeEach{

  val injector = new GuiceApplicationBuilder()
    .bindings(new AuthenticationModule)
    .injector

  val user = new UserSession(0, "test", "test@test", new Date(), new Date())

  def before = {
    val pool = new GuiceApplicationBuilder().injector.instanceOf[Pool]
    pool.withJedisClient(c => c.flushAll())
  }

  "Authentication Cache" should {
    "Empty cache should not return users" in new WithApplication{
      val cache = injector.instanceOf[AuthenticationCache]
      cache.getAllLoggedIn().length must equalTo(0)
    }

    "Contain a user after adding one" in new WithApplication{
      val cache = injector.instanceOf[AuthenticationCache]
      cache.getAllLoggedIn().length must equalTo(0)
      cache.createSession(user) mustNotEqual ""
      cache.getAllLoggedIn().length must equalTo(1)
    }

    "Get a user session from the session key" in new WithApplication{
      val cache = injector.instanceOf[AuthenticationCache]
      cache.getAllLoggedIn().length must equalTo(0)
      val session = cache.createSession(user)
      session mustNotEqual ""
      cache.getAllLoggedIn().length must equalTo(1)
      val u = cache.getSession(session)
      u mustNotEqual null
      u._2.toJson() must equalTo( user.toJson() )
    }

    "Get a user session from the user email" in new WithApplication{
      val cache = injector.instanceOf[AuthenticationCache]
      cache.getAllLoggedIn().length must equalTo(0)
      cache.createSession(user) mustNotEqual ""
      cache.getAllLoggedIn().length must equalTo(1)
      val u = cache.getSessionFromEmail(user.email)
      u mustNotEqual null
      u._2.toJson() must equalTo( user.toJson() )
    }

    "Remove user session from the session key" in new WithApplication{
      val cache = injector.instanceOf[AuthenticationCache]
      cache.getAllLoggedIn().length must equalTo(0)
      val session = cache.createSession(user)
      session mustNotEqual ""
      cache.getAllLoggedIn().length must equalTo(1)
      cache.removeSession(session)
      cache.getAllLoggedIn().length must equalTo(0)
    }

    "Remove user session from the user email" in new WithApplication{
      val cache = injector.instanceOf[AuthenticationCache]
      cache.getAllLoggedIn().length must equalTo(0)
      cache.createSession(user) mustNotEqual ""
      cache.getAllLoggedIn().length must equalTo(1)
      cache.removeSessionWithEmail(user.email)
      cache.getAllLoggedIn().length must equalTo(0)
    }

    "Add email token" in new WithApplication{
      val cache = injector.instanceOf[AuthenticationCache]
      cache.createToken(user.email) mustNotEqual ""
    }

    "Get email from token" in new WithApplication{
      val cache = injector.instanceOf[AuthenticationCache]
      val token = cache.createToken(user.email)
      token mustNotEqual ""
      cache.getEmailFromToken(token) must equalTo(user.email)
    }

    "Remove email from token" in new WithApplication{
      val cache = injector.instanceOf[AuthenticationCache]
      val token = cache.createToken(user.email)
      token mustNotEqual ""
      cache.removeEmailFromToken(token)
      cache.getEmailFromToken(token) must equalTo(null)
    }

  }
}


