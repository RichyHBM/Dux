package controllers

import com.fasterxml.jackson.databind.node.ObjectNode
import play.api._
import play.api.mvc._
import play.api.cache.CacheApi
import play.api.libs.json._
import play.mvc.Http
import views.html._
import com.google.inject._
import auth.scala._
import auth.models._

import common.models.BasicViewResponse

class DataStoreViews @Inject()(cacheApi: CacheApi) extends Controller with AuthenticatedActionBuilder {
  def cache = cacheApi

  def index = AuthenticatedAction(auth.AuthenticationType.None) { request =>
    val response = new BasicAuthViewResponse("Data Store", request.user)
    val viewData: ObjectNode = play.libs.Json.newObject()
    Ok(views.html.index.render(viewData, response))
  }
}
