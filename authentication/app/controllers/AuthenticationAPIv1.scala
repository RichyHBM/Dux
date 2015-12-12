package controllers

import com.fasterxml.jackson.databind.node.ObjectNode
import play.api._
import play.api.mvc._
import play.api.cache.CacheApi
import play.api.libs.json._
import play.mvc.Http
import views.html._
import javax.inject._
import auth.scala._
import auth.models._
import database.models._

import common.models.BasicViewResponse

class AuthenticationAPIv1 @Inject()(cacheApi: CacheApi) extends Controller with AuthenticatedActionBuilder {
  def cache = cacheApi

  def index = AuthenticatedAction(auth.AuthenticationType.None) {
    Ok( "" )
  }
}
