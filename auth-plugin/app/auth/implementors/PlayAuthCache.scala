package auth.implementors

import auth.interfaces.IAuthCache
import auth.models.AuthenticatedUser
import javax.inject.Inject
import play.api.Logger
import play.api.cache.CacheApi
import play.libs.Json
import scala.concurrent.duration._
import java.util.UUID

case class PlayAuthCache @Inject()(cache: CacheApi) extends IAuthCache {

  //By having an internal cache, we limit the amount of calls to the Authentication API
  //If this is not an issue then turn this cache off
  val expiryTimeAmount:Int = 2;

  override def isUserInCache(session: String):Boolean = {
    cache.get[String](session) match {
      case Some(_) => true
      case None => false
    }
  }

  override def storeUserInCache(authenticatedUser: AuthenticatedUser):String = {
    val session: String = UUID.randomUUID().toString()
    cache.set(session, authenticatedUser.toJson(), Duration(expiryTimeAmount, SECONDS))
    session
  }

  override def getUserFromCache(session: String):AuthenticatedUser = {
    cache.get[String](session) match {
      case Some(json) => AuthenticatedUser.fromJson(json)
      case None => null
    }
  }

  override def removeUserFromCache(session: String):Boolean = {
    cache.remove(session)
    true
  }

  override def refreshUserAuthenticationSession(session: String):Boolean = {
    cache.get[String](session) match {
      case Some(json) => {
        cache.set(session, json, Duration(expiryTimeAmount, SECONDS))
        true
      }
      case None => false
    }
  }
}
