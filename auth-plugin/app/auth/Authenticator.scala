package auth

import play.api.mvc._
import auth.interfaces._
import auth.models._

object Authenticator {
  def checkSession(session: String, authCache: IAuthCache): SessionUser = session match {
    case session: String => {
      authCache.getUserFromCache(session) match {
        case u: AuthenticatedUser => SessionUser(session, u)
        case _ => null
      }
    }
    case _ => null
  }

  def checkApiKey(apiKey: String): AuthenticatedUser = apiKey match {
    case apiKey: String => null
    case _ => null
  }
}
