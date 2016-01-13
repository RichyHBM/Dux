package controllers

import java.util.Date

import com.fasterxml.jackson.databind.node.ObjectNode
import models.view.{ViewApp, ViewPermission, ViewGroup}
import models.{view, UserSession}
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

import scala.concurrent.Future

class AuthenticationAPIv1 @Inject()(cacheApi: CacheApi, authCache: IAuthenticationCache) extends Controller with AuthenticatedActionBuilder {
  def cache = cacheApi

  val authType = auth.AuthenticationType.None

  def listAllLoggedIn = AuthenticatedAction(authType) {
    Ok( Json.toJson( authCache.getAllLoggedIn().map(kv => kv._2) ) )
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

  def getAllApps = AuthenticatedAction(authType).async {
    Apps.listAll().map( l => {
      Ok( Json.toJson( l.map(a => view.ViewApp(a.Id, a.Name, a.Description)).toList ) )
    })
  }

  def getAllGroups = AuthenticatedAction(authType).async {
    Groups.listAll().map( l => {
      Ok( Json.toJson( l.map(g => view.ViewGroup(g.Id, g.Name, g.Description)).toList ) )
    })
  }

  def getAllPermissions = AuthenticatedAction(authType).async {
    Permissions.listAll().map( l => {
      Ok( Json.toJson( l.map(p => view.ViewPermission(p.Id, p.Name, p.Description)).toList ) )
    })
  }

  def getAllUsers = AuthenticatedAction(authType).async {
    Users.listAll().map( l => {
      Ok( Json.toJson( l.map(u => view.ViewUser(u.Id, u.Name, u.Email, u.ApiKey, u.CreatedOn.asInstanceOf[Date], u.FailedAttempts, u.Blocked)).toList ))
    })
  }

  def newGroup = AuthenticatedAction(authType).async(parse.json) { request =>
    request.body.validate[ViewGroup].fold(
      errors => {
        Future { BadRequest(JsError.toJson(errors)) }
      }, viewGroup => {
        Groups.add(new Group(viewGroup.Name, viewGroup.Description)).map( i => Ok(i.toString) )
      }
    )
  }

  def newApp = AuthenticatedAction(authType).async(parse.json) { request =>
    request.body.validate[ViewApp].fold(
      errors => {
        Future { BadRequest(JsError.toJson(errors)) }
      }, viewApp => {
        Apps.add(new App(viewApp.Name, viewApp.Description)).map( i => Ok(i.toString) )
      }
    )
  }
  def newPermission = AuthenticatedAction(authType).async(parse.json) { request =>
    request.body.validate[ViewPermission].fold(
      errors => {
        Future { BadRequest(JsError.toJson(errors)) }
      }, viewPermission => {
        Permissions.add(new Permission(viewPermission.Name, viewPermission.Description)).map( i => Ok(i.toString) )
      }
    )
  }
}
