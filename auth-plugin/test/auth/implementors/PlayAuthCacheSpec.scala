package auth.implementors

import auth.interfaces.IAuthCache
import auth.models.AuthenticatedUser
import common.utilities.StringUtils

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.cache.CacheApi
import play.api.libs.json.{Json, JsValue}
import play.test.WithApplication

import play.api.test._
import play.api.test.Helpers._

import collection.mutable.{HashMap, HashSet}
import scala.collection.mutable
import scala.concurrent.duration.Duration

class MockPlayCacheApi extends CacheApi {
  val memory = new HashMap[String, Any]

  override def set(key: String, value: Any, expiration: Duration): Unit = {
    memory.put(key, value)
  }

  override def get[T](key: String)(implicit evidence$2: scala.reflect.ClassTag[T]): Option[T] = {
    memory.get(key) match {
      case Some(v) => Option(v.asInstanceOf[T])
      case None => None
    }
  }

  override def getOrElse[A](key: String, expiration: Duration)(orElse: => A)(implicit evidence$1: scala.reflect.ClassTag[A]): A = {
    memory.get(key) match {
      case Some(v) => v.asInstanceOf[A]
      case None => orElse
    }
  }

  override def remove(key: String): Unit = {
    memory.remove(key)
  }
}

@RunWith(classOf[JUnitRunner])
class PlayAuthCacheSpec extends Specification {
  "PlayAuthCache" should {
    "add user to cache" in {
      val authCache: IAuthCache = new PlayAuthCache(new MockPlayCacheApi)
      val user = new AuthenticatedUser(1, "test", "test@test")
      val sessionSet = new HashSet[String]

      for( a <- 0 to 999){
        val session = authCache.storeUserInCache(user)
        StringUtils.isNotEmpty(session) must equalTo(true)
        sessionSet.add(session) must equalTo(true)
      }
      sessionSet.size must equalTo(1000)
    }

    "not contain random user" in {
      val authCache: IAuthCache = new PlayAuthCache(new MockPlayCacheApi)
      authCache.isUserInCache("randomSessionKey") must equalTo(false)
    }

    "contain user" in {
      val authCache: IAuthCache = new PlayAuthCache(new MockPlayCacheApi)
      val user = new AuthenticatedUser(1, "test", "test@test")

      val session = authCache.storeUserInCache(user)
      StringUtils.isNotEmpty(session) must equalTo(true)
      authCache.isUserInCache(session) must equalTo(true)
    }

    "not return for random user" in {
      val authCache: IAuthCache = new PlayAuthCache(new MockPlayCacheApi)
      authCache.getUserFromCache("randomSessionKey") must equalTo(null)
    }

    "return user" in {
      val authCache: IAuthCache = new PlayAuthCache(new MockPlayCacheApi)
      val user = new AuthenticatedUser(1, "test", "test@test")

      val session = authCache.storeUserInCache(user)
      StringUtils.isNotEmpty(session) must equalTo(true)

      val cachedUser = authCache.getUserFromCache(session)

      cachedUser must not equalTo(null)

      user.id must equalTo(cachedUser.id)
      user.name must equalTo(cachedUser.name)
      user.email must equalTo(cachedUser.email)
    }

    "remove user" in {
      val authCache: IAuthCache = new PlayAuthCache(new MockPlayCacheApi)
      val user = new AuthenticatedUser(1, "test", "test@test")

      val session = authCache.storeUserInCache(user)
      StringUtils.isNotEmpty(session) must equalTo(true)

      authCache.removeUserFromCache(session) must equalTo(true)
      authCache.getUserFromCache(session) must equalTo(null)
    }

    "not refresh random user" in {
      val authCache: IAuthCache = new PlayAuthCache(new MockPlayCacheApi)
      authCache.refreshUserAuthenticationSession("randomSessionKey") must equalTo(false)
    }

    "refresh user" in {
      val authCache: IAuthCache = new PlayAuthCache(new MockPlayCacheApi)
      val user = new AuthenticatedUser(1, "test", "test@test")

      val session = authCache.storeUserInCache(user)
      StringUtils.isNotEmpty(session) must equalTo(true)
      authCache.refreshUserAuthenticationSession(session) must equalTo(true)
    }
  }
}