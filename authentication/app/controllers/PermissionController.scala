package controllers

import javax.inject.Inject

import database._
import interfaces.IAuthenticationCache
import models.view
import play.api.Logger
import play.api.cache.CacheApi
import play.api.libs.json.Json
import play.api.mvc.Controller
import auth.scala._
import utilities.RequestParser
import scala.concurrent.ExecutionContext.Implicits.global


class PermissionController @Inject()(cacheApi: CacheApi, authCache: IAuthenticationCache) extends Controller with AuthenticatedActionBuilder {
  val authType = auth.AuthenticationType.None

  def cache = cacheApi

  def getAllPermissionsForApp = AuthenticatedAction(authType).async(parse.json) { request =>
    RequestParser.parseViewId(request) { viewId => {
      AppPermissions.getAllPermissionIdsFromAppId(viewId.Id).flatMap(permissions =>
        Permissions.get(permissions.toList).map(l => {
          Ok(Json.toJson(l.map(p => view.ViewPermission(p.Id, p.Name, p.Description)).toList))
        })
      )
    }}
  }

  def addPermissionsForApp = AuthenticatedAction(authType).async(parse.json) { request =>
    RequestParser.parseViewIdToIds(request) { viewIdToIds => {
      AppPermissions.deleteAllFromAppId(viewIdToIds.Id).flatMap(_ => {
        val appPermissions = viewIdToIds.Ids.map(id => AppPermission(-1, viewIdToIds.Id, id))
        AppPermissions.add(appPermissions).map(r =>
          Ok(r.toString)
        )
      })
    }}
  }

  def getAllPermissions = AuthenticatedAction(authType).async {
    Permissions.listAll().map(l => {
      Ok(Json.toJson(l.map(p => view.ViewPermission(p.Id, p.Name, p.Description)).toList))
    })
  }

  def newPermission = AuthenticatedAction(authType).async(parse.json) { request =>
    RequestParser.parseViewPermission(request) {
      viewPermission => {
        Permissions.add(new Permission(viewPermission.Name, viewPermission.Description)).map(i => Ok(i.toString))
      }
    }
  }

  def editPermission = AuthenticatedAction(authType).async(parse.json) { request =>
    RequestParser.parseViewApp(request) {
      viewPermission => {
        Permissions.edit(viewPermission.Id, viewPermission.Name, viewPermission.Description).map(i => Ok(i.toString))
      }
    }
  }

  def deletePermission = AuthenticatedAction(authType).async(parse.json) { request =>
    RequestParser.parseViewApp(request) {
      viewPermission => {
        Logger.info("Deleting permission %d: '%s' with description: %s".format(
            viewPermission.Id,
            viewPermission.Name,
            viewPermission.Description))
        Permissions.delete(viewPermission.Id).map(i => Ok(i.toString))
      }
    }
  }
}
