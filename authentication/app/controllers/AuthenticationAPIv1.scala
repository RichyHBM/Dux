package controllers

import java.util.Date
import javax.inject._

import auth.models.AuthenticatedUser
import auth.scala._
import database._
import interfaces._
import models.UserSession
import play.api._
import play.api.cache.CacheApi
import play.api.libs.json._
import play.api.mvc._
import utilities.{Passwords, RequestParser}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthenticationAPIv1 @Inject()(cacheApi: CacheApi, authCache: IAuthenticationCache, configuration: Configuration) extends Controller with AuthenticatedActionBuilder {
  val authType = auth.AuthenticationType.None

  def cache = cacheApi
  def config = configuration

  def listAllLoggedIn = AuthenticatedAction(authType) {
    Ok(Json.toJson(authCache.getAllLoggedIn().map(kv => kv._2)))
  }

  def logOut = AuthenticatedAction(authType) { request =>
    request.body.asText match {
      case Some(session: String) => {
        authCache.removeSession(session)
        Ok
      }
      case _ => BadRequest
    }
  }

  def removeSession = AuthenticatedAction(authType) { request =>
    request.body.asJson match {
      case Some(json: JsValue) => {
        val user = UserSession.fromJson(json.toString())
        authCache.removeSessionWithEmail(user.email)
        Ok
      }
      case _ => BadRequest
    }
  }

  def authenticateApiKey = AuthenticatedAction(authType).async(parse.json) { request =>
      RequestParser.parseAuthToken(request) { authApiKey => {
        Users.getFromApiKey(authApiKey.Token).flatMap {
          case Some(u) => {
            UserGroups.getAllGroupIdsForUserId(u.Id).flatMap(groupIds => {
              GroupPermissions.getAllPermissionIdsFromGroupIds(groupIds).flatMap(permissionIds => {
                Permissions.get(permissionIds.toList).flatMap(permissions => {
                  permissions.exists(permission => permission.Name == authApiKey.Permission) match {
                    case true => {
                      val userSession = new UserSession(u.Id, u.Name, u.Email, new Date(), new Date(), authApiKey.Permission)
                      val session = authCache.createSession(userSession)
                      Future { Ok(new AuthenticatedUser(u.Id, u.Name, u.Email).toJson()).withCookies(Cookie(DefinedStrings.sessionCookieName, session)) }
                    }
                    case false => Future {BadRequest("User doesn't have permissions") }
                  }
                })
              })
            })
          }
          case None => Future { BadRequest("User not found") }
        }
      }
    }
  }

  def authenticateSession = AuthenticatedAction(authType).async(parse.json) { request =>
    RequestParser.parseAuthToken(request) { authSession => {
      authCache.getSession(authSession.Token) match {
        case Some((s, u)) => {
          Users.getFromEmail(u.email).flatMap {
            case Some(user) => {
              UserGroups.getAllGroupIdsForUserId(user.Id).flatMap(groupIds => {
                GroupPermissions.getAllPermissionIdsFromGroupIds(groupIds).flatMap(permissionIds => {
                  Permissions.get(permissionIds.toList).flatMap(permissions => {
                    permissions.exists(permission => permission.Name == authSession.Permission) match {
                      case true => {
                        authCache.renewSession(s, u)
                        Future {
                          Ok(new AuthenticatedUser(u.id, u.name, u.email).toJson()).withCookies(Cookie(DefinedStrings.sessionCookieName, s))
                        }
                      }
                      case false => Future {
                        BadRequest("User doesn't have permissions")
                      }
                    }
                  })
                })
              })
            }
            case None => Future { BadRequest("User not found") }
          }
        }
        case None => Future { BadRequest("User not found") }
      }
    }}
  }

  def logIn = AuthenticatedAction(authType).async(parse.json) { request =>
    RequestParser.parseLogIn(request) { logIn => {
        Users.getFromEmail(logIn.Email).flatMap {
          case Some(u) => {
            Passwords.isExpectedPassword(logIn.Password, u.Salt, u.Password) match {
              case true => {
                UserGroups.getAllGroupIdsForUserId(u.Id).flatMap(groupIds => {
                  GroupPermissions.getAllPermissionIdsFromGroupIds(groupIds).flatMap(permissionIds => {
                    Permissions.get(permissionIds.toList).flatMap(permissions => {
                      permissions.exists(permission => permission.Name == logIn.Permission) match {
                        case true => {
                          val userSession = new UserSession(u.Id, u.Name, u.Email, new Date(), new Date(), logIn.Permission)
                          val session = authCache.createSession(userSession)
                          Future { Ok(new AuthenticatedUser(u.Id, u.Name, u.Email).toJson()).withCookies(Cookie(DefinedStrings.sessionCookieName, session)) }
                        }
                        case false => Future {BadRequest("User doesn't have permissions") }
                      }
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
