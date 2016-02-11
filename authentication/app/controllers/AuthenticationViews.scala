package controllers

import javax.inject._

import auth.models._
import auth.scala._
import com.fasterxml.jackson.databind.node.ObjectNode
import play.api._
import play.api.cache.CacheApi
import play.api.mvc._

class AuthenticationViews @Inject()(cacheApi: CacheApi, configuration: Configuration) extends Controller with AuthenticatedActionBuilder {
  def cache = cacheApi
  def config = configuration

  def index = AuthenticatedAction(auth.AuthenticationType.None) { request =>
    val response = new BasicAuthViewResponse("Authentication", request.user)
    val viewData: ObjectNode = play.libs.Json.newObject()
    Ok(views.html.index.render(viewData, response))
  }

  def groups = AuthenticatedAction(auth.AuthenticationType.None) { request =>
    val response = new BasicAuthViewResponse("Authentication", request.user)
    val viewData: ObjectNode = play.libs.Json.newObject()
    Ok(views.html.groups.render(viewData, response))
  }

  def permissions = AuthenticatedAction(auth.AuthenticationType.None) { request =>
    val response = new BasicAuthViewResponse("Authentication", request.user)
    val viewData: ObjectNode = play.libs.Json.newObject()
    Ok(views.html.permissions.render(viewData, response))
  }

  def users = AuthenticatedAction(auth.AuthenticationType.None) { request =>
    val response = new BasicAuthViewResponse("Authentication", request.user)
    val viewData: ObjectNode = play.libs.Json.newObject()
    Ok(views.html.users.render(viewData, response))
  }
}
