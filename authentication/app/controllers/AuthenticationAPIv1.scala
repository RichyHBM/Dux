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

  def logIn = AuthenticatedAction(authType).async(parse.json) { request =>
    RequestParser.parseLogIn(request) { logIn => {
        Users.getFromEmail(logIn.Email).flatMap {
          case Some(u) => {
            Passwords.isExpectedPassword(logIn.Password, u.Salt, u.Password) match {
              case true => {
                UserGroups.getAllGroupIdsForUserId(u.Id).flatMap(groupIds => {
                  GroupPermissions.getAllPermissionIdsFromGroupIds(groupIds).flatMap(permissionIds => {
                    AppPermissions.getAllAppIdsFromPermissionIds(permissionIds).flatMap(appIds => {
                      Apps.get(appIds.toList).flatMap(apps => {
                        apps.exists(app => app.Name == logIn.Service) match {
                          case true => {
                            val userSession = new UserSession(u.Id, u.Name, u.Email, new Date(), new Date(), logIn.Service)
                            val session = authCache.createSession(userSession)
                            Future { Ok.withCookies(Cookie("Session", session)) }
                          }
                          case false => Future {BadRequest("User doesn't have permissions") }
                        }
                      })
                    })
                  })
                })
              }
              case false => Future { BadRequest("Invalid password") }
            }
          }
          case None => Future { BadRequest("User not found") }
        }
      }
    }
  }
}
