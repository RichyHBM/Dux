package auth.scala

import auth.implementors.PlayAuthCache
import auth.models._
import auth.{AuthenticationType, Authenticator}
import common.utilities.StringUtils
import play.api.Configuration
import play.api.cache.CacheApi
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.Future


trait AuthenticatedActionBuilder {
  def cache: CacheApi

  def config: Configuration

  def AuthenticatedAction(authType: AuthenticationType, privilege: String = "") = new ActionBuilder[AuthenticatedRequest] {
    override def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]) = {
      val authUrl: String = config.getString(DefinedStrings.duxConfigAuthUrl).getOrElse(DefinedStrings.defaultAuthUrl)
      val correctPrivilege: String =
        if (StringUtils.isEmpty(privilege)) config.getString(DefinedStrings.duxDefaultAuthPrivilege).getOrElse("") else privilege

      authType match {
        case AuthenticationType.None => {
          block(AuthenticatedRequest("", null, request))
        }
        case AuthenticationType.Session => {
          authenticateSession(request, authUrl, correctPrivilege ) match {
            case Some(authenticatedRequest) => block(authenticatedRequest)
            case None => Future.successful(Unauthorized("You are not authorised to view this page"))
          }
        }
        case AuthenticationType.ApiKey => {
          authenticateApiKey(request, authUrl, correctPrivilege) match {
            case Some(authenticatedRequest) => block(authenticatedRequest)
            case None => Future.successful(Unauthorized("You are not authorised to view this page"))
          }
        }
        case AuthenticationType.Either => {
          authenticateSession(request, authUrl, correctPrivilege) match {
            case Some(authenticatedRequest) => block(authenticatedRequest)
            case None => {
              authenticateApiKey(request, authUrl, correctPrivilege) match {
                case Some(authenticatedRequest) => block(authenticatedRequest)
                case None => Future.successful(Unauthorized("You are not authorised to view this page"))
              }
            }
          }
        }
        case _ => Future.successful(Unauthorized("You are not authorised to view this page"))
      }
    }

    def authenticateSession[A](request: Request[A], url: String, privilege: String): Option[AuthenticatedRequest[A]] = {
      request.cookies.get(DefinedStrings.sessionCookieName) match {
        case Some(cookie) => {
          Authenticator.checkSession(url, cookie.value, privilege, new PlayAuthCache(cache)) match {
            case sessionUser: SessionUser => Option(AuthenticatedRequest(sessionUser.sessionKey, sessionUser.user, request))
            case null => None
          }
        }
        case None => None
      }
    }

    def authenticateApiKey[A](request: Request[A], url: String, privilege: String): Option[AuthenticatedRequest[A]] = {
      Authenticator.checkApiKey(url, request.headers.get(DefinedStrings.duxApiHeader).getOrElse(""), privilege) match {
        case user: AuthenticatedUser => Option(AuthenticatedRequest("", user, request))
        case null => None
      }
    }
  }
}
