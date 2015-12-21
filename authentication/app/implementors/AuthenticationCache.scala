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

  override def getSession(sessionKey: String): (String, UserSession) = {
    (sessionKey, UserSession.fromJson(cache.withJedisClient(c => c.hget(sessionCache, sessionKey))))
  }

  override def getSessionFromEmail(email: String): (String, UserSession) = {
    val sessionToken = cache.withJedisClient(c => c.hget(emailToSessionKeyCache, email))
    getSession(sessionToken)
  }

  override def removeSession(sessionKey: String): Unit = {
    val user = getSession(sessionKey)
    cache.withJedisClient(c => c.hdel(sessionCache, sessionKey))
    cache.withJedisClient(c => c.hdel(emailToSessionKeyCache, user._2.email))
  }

  override def removeSessionWithEmail(email: String): Unit = {
    val sessionUser = getSessionFromEmail(email)
    cache.withJedisClient(c => c.hdel(sessionCache, sessionUser._1))
    cache.withJedisClient(c => c.hdel(emailToSessionKeyCache, email))
  }

  override def createToken(email: String): String = {
    val sessionToken = UUID.randomUUID().toString
    cache.withJedisClient(c => c.hset(tokenCache, sessionToken, email))
    sessionToken
  }

  override def getEmailFromToken(token: String): String = {
    cache.withJedisClient(c => c.hget(tokenCache, token))
  }

  override def removeEmailFromToken(token: String): Unit = {
    cache.withJedisClient(c => c.hdel(tokenCache, token))
  }
}
