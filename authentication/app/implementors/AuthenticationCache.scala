package implementors

import java.util.UUID
import javax.inject.Inject

import interfaces.IAuthenticationCache
import models.UserSession
import org.sedis.Pool
import scala.concurrent.duration._
import collection.JavaConversions._

class AuthenticationCache @Inject()(cache: Pool) extends IAuthenticationCache {
  val sessionCache = "SESSION_CACHE"
  val emailToSessionKeyCache = "EMAIL_TO_SESSION_KEY_CACHE"
  val tokenCache = "TOKEN_CACHE"

  override def getAllLoggedIn(): List[(String, UserSession)] = {
    cache.
      withJedisClient(c => c.hgetAll(sessionCache)).
      map(kv => (kv._1, UserSession.fromJson(kv._2))).
      toList
  }

  override def createSession(session: UserSession): String = {
    val sessionToken = UUID.randomUUID().toString
    cache.withJedisClient(c => c.hset(sessionCache, sessionToken, session.toJson()))
    cache.withJedisClient(c => c.hset(emailToSessionKeyCache, session.email, sessionToken))
    sessionToken
  }

  override def getSession(sessionKey: String): Option[(String, UserSession)] = {
    cache.withJedisClient(c => c.hget(sessionCache, sessionKey)) match {
      case null => None
      case json => Some((sessionKey, UserSession.fromJson(json)))
    }
  }

  override def getSessionFromEmail(email: String): Option[(String, UserSession)] = {
    cache.withJedisClient(c => c.hget(emailToSessionKeyCache, email)) match {
      case null => None
      case sessionToken => getSession(sessionToken)
    }
  }

  override def removeSession(sessionKey: String): Unit = {
    getSession(sessionKey) match {
      case Some(user) => cache.withJedisClient(c => c.hdel(emailToSessionKeyCache, user._2.email))
      case None => {}
    }
    cache.withJedisClient(c => c.hdel(sessionCache, sessionKey))
  }

  override def removeSessionWithEmail(email: String): Unit = {
    getSessionFromEmail(email) match {
      case Some(user) => cache.withJedisClient(c => c.hdel(sessionCache, user._1))
      case None => {}
    }
    cache.withJedisClient(c => c.hdel(emailToSessionKeyCache, email))
  }

  override def createToken(email: String): String = {
    val sessionToken = UUID.randomUUID().toString
    cache.withJedisClient(c => c.hset(tokenCache, sessionToken, email))
    sessionToken
  }

  override def getEmailFromToken(token: String): Option[String] = {
    cache.withJedisClient(c => c.hget(tokenCache, token)) match {
      case null => None
      case s => Some(s)
    }
  }

  override def removeEmailFromToken(token: String): Unit = {
    cache.withJedisClient(c => c.hdel(tokenCache, token))
  }
}
