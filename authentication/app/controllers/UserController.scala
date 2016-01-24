package controllers

import java.util.{UUID, Date}
import javax.inject.Inject

import database.{UserGroups, User, Users}
import interfaces.IAuthenticationCache
import models.view
import play.api.Logger
import play.api.cache.CacheApi
import play.api.libs.json.Json
import play.api.mvc.Controller
import auth.scala._
import utilities.{Passwords, RequestParser}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class UserController @Inject()(cacheApi: CacheApi, authCache: IAuthenticationCache) extends Controller with AuthenticatedActionBuilder {
  val authType = auth.AuthenticationType.None

  def cache = cacheApi

  def getAllUsersInGroup = AuthenticatedAction(authType).async(parse.json) { request =>
    RequestParser.parseViewId(request) { viewId => {
      UserGroups.getAllUserIdsFromGroupId(viewId.Id).flatMap( users =>
        Users.get(users.toList).map(l => {
          Ok(Json.toJson(l.map(u => view.ViewUser(u.Id, u.Name, u.Email, u.ApiKey, u.CreatedOn.asInstanceOf[Date], u.FailedAttempts, u.Blocked)).toList))
        })
      )
    }}
  }

  def getAllUsers = AuthenticatedAction(authType).async {
    Users.listAll().map(l => {
      Ok(Json.toJson(l.map(u => view.ViewUser(u.Id, u.Name, u.Email, u.ApiKey, u.CreatedOn.asInstanceOf[Date], u.FailedAttempts, u.Blocked)).toList))
    })
  }

  def newUser = AuthenticatedAction(authType).async(parse.json) { request =>
    RequestParser.parseNewUser(request) {
      newUser => {
        newUser.Password match {
          case p if p != newUser.Confirmation => Future {
            BadRequest("Password and confirmation does not match!")
          }
          case _ => {
            val salt: Array[Byte] = Passwords.getNextSalt
            val password: Array[Byte] = Passwords.hash(newUser.Password, salt)
            val apiKey = UUID.randomUUID().toString

            Users.add(new User(newUser.Name, newUser.Email, password, salt, apiKey)).map(i => Ok(i.toString))
          }
        }
      }
    }
  }

  def editUser = AuthenticatedAction(authType).async(parse.json) { request =>
    RequestParser.parseViewUser(request) {
      viewUser => {
        Users.get(viewUser.Id) map {
          case Some(u) => Users.updateUser(viewUser.Id, User(u.Id, viewUser.Name, viewUser.Email, u.Password, u.Salt, u.ApiKey, u.CreatedOn, viewUser.FailedAttempts, viewUser.Blocked)).map(i => Ok(i.toString))
          case None => Future(BadRequest("User %d not found!".format(viewUser.Id)))
        }
      }.flatMap(r => r)
    }
  }

  def deleteUser = AuthenticatedAction(authType).async(parse.json) { request =>
    RequestParser.parseViewUser(request) {
      viewUser => {
        Logger.info("Deleting user %d: '%s' with email: %s".format(
          viewUser.Id,
          viewUser.Name,
          viewUser.Email))

        Users.delete(viewUser.Id).map(i => Ok(i.toString))
      }
    }
  }
}
