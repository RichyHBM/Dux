package controllers

import javax.inject.Inject
import play.Logger
import play.api.cache.CacheApi
import play.api.mvc._
import common.utilities._
import auth.scala._
import auth.models._

class DataStoreAPIv1 @Inject() (cacheApi: CacheApi) extends Controller with AuthenticatedActionBuilder {
  def cache = cacheApi

  def put(name: String, data: String) = AuthenticatedAction(auth.AuthenticationType.None) {
    (name, data) match {
      case (name, data) if(StringUtils isNotEmpty name) => {
        cacheApi.set(name, data)
        Ok
      }
      case _ => BadRequest
    }
  }

  def get(name: String) = AuthenticatedAction(auth.AuthenticationType.None) {
    name match {
      case name if(StringUtils isNotEmpty name) => {
        cacheApi.get[String](name) match {
          case Some(data) => Ok(data)
          case None => Ok("")
        }
      }
      case _ => BadRequest
    }
  }

}
