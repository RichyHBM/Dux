package controllers

import javax.inject.Inject

import database.{App, Apps}
import interfaces.IAuthenticationCache
import models.view
import play.api.Logger
import play.api.cache.CacheApi
import play.api.libs.json.Json
import play.api.mvc.Controller
import auth.scala._
import utilities.RequestParser
import scala.concurrent.ExecutionContext.Implicits.global


class AppController @Inject()(cacheApi: CacheApi, authCache: IAuthenticationCache) extends Controller with AuthenticatedActionBuilder {
  val authType = auth.AuthenticationType.None

  def cache = cacheApi

  def getAllApps = AuthenticatedAction(authType).async {
    Apps.listAll().map(l => {
      Ok(Json.toJson(l.map(a => view.ViewApp(a.Id, a.Name, a.Description)).toList))
    })
  }

  def newApp = AuthenticatedAction(authType).async(parse.json) { request =>
    RequestParser.parseViewApp(request) {
      viewApp => {
        Apps.add(new App(viewApp.Name, viewApp.Description)).map(i => Ok(i.toString))
      }
    }
  }

  def editApp = AuthenticatedAction(authType).async(parse.json) { request =>
    RequestParser.parseViewApp(request) {
      viewApp => {
        Apps.edit(viewApp.Id, viewApp.Name, viewApp.Description).map(i => Ok(i.toString))
      }
    }
  }

  def deleteApp = AuthenticatedAction(authType).async(parse.json) { request =>
    RequestParser.parseViewApp(request) {
      viewApp => {
        Logger.info("Deleting app %d: '%s' with description: %s".format(
          viewApp.Id,
          viewApp.Name,
          viewApp.Description))
        Apps.delete(viewApp.Id).map(i => Ok(i.toString))
      }
    }
  }
}