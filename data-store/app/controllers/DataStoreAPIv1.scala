package controllers

import com.google.inject.Inject
import play.Logger
import play.api.cache.CacheApi
import play.api.mvc._
import common.utilities._

class DataStoreAPIv1 @Inject() (cache: CacheApi) extends Controller {

  def put(name: String, data: String) = Action {
    (name, data) match {
      case (name, data) if(StringUtils isNotEmpty name) => {
        cache.set(name, data)
        Ok
      }
      case _ => BadRequest
    }
  }

  def get(name: String) = Action {
    name match {
      case name if(StringUtils isNotEmpty name) => {
        Ok(cache.get(name).toString)
      }
      case _ => BadRequest
    }
  }

}
