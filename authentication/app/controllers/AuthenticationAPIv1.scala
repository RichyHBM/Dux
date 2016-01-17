package controllers

import java.util.{UUID, Date}

import com.fasterxml.jackson.databind.node.ObjectNode
import models.view.{ViewApp, ViewPermission, ViewGroup}
import models.{NewUser, view, UserSession}
import play.api._
import play.api.mvc._
import play.api.cache.CacheApi
import play.api.libs.json._
import play.mvc.Http
import utilities.{RequestParser, Passwords}
import views.html._
import javax.inject._
import auth.scala._
import auth.models._
import database._
import scala.concurrent.ExecutionContext.Implicits.global
import common.models.BasicViewResponse
import interfaces._

import scala.concurrent.Future

class AuthenticationAPIv1 @Inject()(cacheApi: CacheApi, authCache: IAuthenticationCache) extends Controller with AuthenticatedActionBuilder {
  val authType = auth.AuthenticationType.None
  def cache = cacheApi

  def listAllLoggedIn = AuthenticatedAction(authType) {
    Ok(Json.toJson(authCache.getAllLoggedIn().map(kv => kv._2)))
  }

  def removeSession = AuthenticatedAction(authType) { request =>
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
