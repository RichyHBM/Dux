package auth

import auth.interfaces._
import auth.models._
import auth.scala.DefinedStrings
import play.api.Logger
import play.api.Play.current
import play.api.http.HeaderNames._
import play.api.libs.ws.WS
import play.mvc.Http.MimeTypes

import _root_.scala.concurrent.Await
import _root_.scala.concurrent.duration._
import _root_.scala.language.postfixOps

object Authenticator {

  def logOut(url: String, session: String) = {
    Await.result(WS.url(url + "/view-api/log-out").
      withRequestTimeout(5 * 1000).
      post(session), 10 seconds)
  }

  def logIn(url: String, email: String, password: String, privilege: String, authCache: IAuthCache): SessionUser = {

    val webResponse = WS.url(url + "/view-api/log-in").
      withHeaders(CONTENT_TYPE -> MimeTypes.JSON).
      withRequestTimeout(5 * 1000).
      withFollowRedirects(true).
      post(new LogIn(email,password, privilege).toJson())

    try{
      val response = Await.result(webResponse, 10 seconds)
      val authUser = AuthenticatedUser.fromJson(response.body)
      val session: String = response.cookie(DefinedStrings.sessionCookieName).get.value.getOrElse("")
      new SessionUser(session, authUser)
    }catch{
      case e:Exception => {
        Logger.error(e.getMessage)
        null
      }
    }

  }

  def checkSession(url: String, session: String, privilege: String, authCache: IAuthCache): SessionUser = session match {
    case session: String => {
      authCache.getUserFromCache(session) match {
        case u: AuthenticatedUser => SessionUser(session, u)
        case _ => {
          val response = WS.url(url + "/view-api/auth-session").
            withHeaders(CONTENT_TYPE -> MimeTypes.JSON).
            withRequestTimeout(5 * 1000).
            withFollowRedirects(true).
            post(new AuthToken(session, privilege).toJson())

          try{
            val authUser = AuthenticatedUser.fromJson(Await.result(response, 10 seconds).body)
            val session = authCache.storeUserInCache(authUser)
            new SessionUser(session, authUser)
          }catch{
            case e: Exception => {
              Logger.error(e.getMessage)
              null
            }
          }
        }
      }
    }
    case _ => null
  }

  def checkApiKey(url: String, apiKey: String, privilege: String): AuthenticatedUser = apiKey match {
    case apiKey: String => {
      val response = WS.url(url + "/view-api/auth-api-key").
        withHeaders(CONTENT_TYPE -> MimeTypes.JSON).
        withRequestTimeout(5 * 1000).
        withFollowRedirects(true).
        post(new AuthToken(apiKey, privilege).toJson())

      try{
        AuthenticatedUser.fromJson(Await.result(response, 10 seconds).body)
      }catch{
        case e:Exception => {
          Logger.error(e.getMessage)
          null
        }
      }
    }
    case _ => null
  }
}
