package utilities

import models.NewUser
import models.view._
import play.api._
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsValue, JsError}
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object RequestParser {

  def parseNewUser(request: Request[JsValue])(block: (NewUser) => Future[Result] ):Future[Result] = {
    request.body.validate[NewUser].fold(
      errors => {
        Future {
          BadRequest(JsError.toJson(errors))
        }
      }, newUser => {
        block(newUser)
      }
    )
  }

  def parseViewGroup(request: Request[JsValue])(block: (ViewGroup) => Future[Result] ):Future[Result] = {
    request.body.validate[ViewGroup].fold(
      errors => {
        Future {
          BadRequest(JsError.toJson(errors))
        }
      }, viewGroup => {
        block(viewGroup)
      }
    )
  }

  def parseViewApp(request: Request[JsValue])(block: (ViewApp) => Future[Result] ):Future[Result] = {
    request.body.validate[ViewApp].fold(
      errors => {
        Future {
          BadRequest(JsError.toJson(errors))
        }
      }, viewApp => {
        block(viewApp)
      }
    )
  }

  def parseViewPermission(request: Request[JsValue])(block: (ViewPermission) => Future[Result] ):Future[Result] = {
    request.body.validate[ViewPermission].fold(
      errors => {
        Future {
          BadRequest(JsError.toJson(errors))
        }
      }, viewPermission => {
        block(viewPermission)
      }
    )
  }

}
