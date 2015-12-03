package auth.scala

import auth.implementors.PlayAuthCache
import auth.interfaces.IAuthCache
import com.google.inject.Inject
import play.api.Logger
import play.api.cache.CacheApi
import play.api.mvc.Results._
import play.api.mvc._
import auth.models._
import auth.AuthenticationType
import auth.Authenticator
import scala.concurrent.Future


trait AuthenticatedActionBuilder {
  def cache: play.cache.CacheApi

  def AuthenticatedAction(authType: AuthenticationType) = new ActionBuilder[AuthenticatedRequest] {
    override def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]) = {
      authType match {
        case AuthenticationType.None => {
          block(AuthenticatedRequest("", null, request))
        }
        case AuthenticationType.Session => {
          authenticateSession(request) match {
            case Some(authenticatedRequest) => block(authenticatedRequest)
            case None => Future.successful(Unauthorized("You are not authorised to view this page"))
          }
        }
        case AuthenticationType.ApiKey => {
          authenticateApiKey(request) match {
            case Some(authenticatedRequest) => block(authenticatedRequest)
            case None => Future.successful(Unauthorized("You are not authorised to view this page"))
          }
        }
        case AuthenticationType.Either => {
          authenticateSession(request) match {
            case Some(authenticatedRequest) => block(authenticatedRequest)
            case None => {
              authenticateApiKey(request) match {
                case Some(authenticatedRequest) => block(authenticatedRequest)
                case None => Future.successful(Unauthorized("You are not authorised to view this page"))
              }
            }
          }
        }
        case _ => Future.successful(Unauthorized("You are not authorised to view this page"))
      }
    }

    def authenticateSession[A](request: Request[A]): Option[AuthenticatedRequest[A]] = {
      request.cookies.get("session") match {
        case Some(cookie) => {
          Authenticator.checkSession(cookie.value, new PlayAuthCache(cache)) match {
            case sessionUser: SessionUser => Option(AuthenticatedRequest(sessionUser.sessionKey, sessionUser.user, request))
            case null => None
          }
        }
        case None => None
      }
    }

    def authenticateApiKey[A](request: Request[A]): Option[AuthenticatedRequest[A]] = {
      Authenticator.checkApiKey("") match {
        case user: AuthenticatedUser => Option(AuthenticatedRequest("", user, request))
        case null => None
      }
    }
  }
}
