package auth.implementors

import auth.interfaces.IAuthCache
import auth.models.AuthenticatedUser
import com.google.inject.Inject
import play.api.cache.CacheApi
import play.libs.Json
import scala.concurrent.duration._
import java.util.UUID

case class PlayAuthCache @Inject()(cache: CacheApi) extends IAuthCache {

  val expiryTimeAmount:Int = 60 * 60 * 24 * 7;

  @Override
  def isUserInCache(session: String):Boolean = {
    cache.get[String](session) match {
      case Some(_) => true
      case None => false
    }
  }

  @Override
  def storeUserInCache(authenticatedUser: AuthenticatedUser):String = {
    val session: String = UUID.randomUUID().toString()
    cache.set(session, authenticatedUser.toJson(), Duration(expiryTimeAmount, SECONDS))
    session
  }

  @Override
  def getUserFromCache(session: String):AuthenticatedUser = {
    cache.get[String](session) match {
      case Some(json) => AuthenticatedUser.fromJson(json)
      case None => null
    }
  }

  @Override
  def removeUserFromCache(session: String):Boolean = {
    cache.remove(session)
    true
  }

  @Override
  def refreshUserAuthenticationSession(session: String):Boolean = {
    cache.get[String](session) match {
      case Some(json) => {
        cache.set(session, json, Duration(expiryTimeAmount, SECONDS))
        true
      }
      case None => false
    }
  }
}
