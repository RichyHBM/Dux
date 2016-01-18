package controllers

import javax.inject.Inject

import database.{Group, Groups}
import interfaces.IAuthenticationCache
import models.view
import play.api.Logger
import play.api.cache.CacheApi
import play.api.libs.json.Json
import play.api.mvc.Controller
import utilities.RequestParser
import auth.scala._
import scala.concurrent.ExecutionContext.Implicits.global


class GroupController @Inject()(cacheApi: CacheApi, authCache: IAuthenticationCache) extends Controller with AuthenticatedActionBuilder {
  val authType = auth.AuthenticationType.None

  def cache = cacheApi

  def getAllGroups = AuthenticatedAction(authType).async {
    Groups.listAll().map(l => {
      Ok(Json.toJson(l.map(g => view.ViewGroup(g.Id, g.Name, g.Description)).toList))
    })
  }

  def newGroup = AuthenticatedAction(authType).async(parse.json) { request =>
    RequestParser.parseViewGroup(request) {
      viewGroup => {
        Groups.add(new Group(viewGroup.Name, viewGroup.Description)).map(i => Ok(i.toString))
      }
    }
  }

  def editGroup = AuthenticatedAction(authType).async(parse.json) { request =>
    RequestParser.parseViewGroup(request) {
      viewGroup => {
        Groups.edit(viewGroup.Id, viewGroup.Name, viewGroup.Description).map(i => Ok(i.toString))
      }
    }
  }

  def deleteGroup = AuthenticatedAction(authType).async(parse.json) { request =>
    RequestParser.parseViewGroup(request) {
      viewGroup => {
        Logger.info("Deleting group %d: '%s' with description: %s".format(
          viewGroup.Id,
          viewGroup.Name,
          viewGroup.Description))
        Groups.delete(viewGroup.Id).map(i => Ok(i.toString))
      }
    }
  }
}
