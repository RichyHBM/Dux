package controllers

import com.fasterxml.jackson.databind.node.ObjectNode
import models.UserSession
import play.api._
import play.api.mvc._
import play.api.cache.CacheApi
import play.api.libs.json._
import play.mvc.Http
import views.html._
import javax.inject._
import auth.scala._
import auth.models._
import database._
import scala.concurrent.ExecutionContext.Implicits.global
import common.models.BasicViewResponse
import interfaces._

class AuthenticationAPIv1 @Inject()(cacheApi: CacheApi, authCache: IAuthenticationCache) extends Controller with AuthenticatedActionBuilder {
  def cache = cacheApi

  def listAllLoggedIn = AuthenticatedAction(auth.AuthenticationType.None) {
    Ok( Json.toJson( authCache.getAllLoggedIn().map(kv => kv._2) ) )
  }

  def removeSession = AuthenticatedAction(auth.AuthenticationType.None) { request =>
    request.body.asJson match {
      case Some(json: JsValue) => {
        val user = UserSession.fromJson(json.toString())
        Logger.debug(user.email)
        authCache.removeSessionWithEmail(user.email)
        Ok
      }
      case _ => BadRequest
    }
  }

}
