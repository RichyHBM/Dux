package utilities

import models.{LogIn, NewUser}
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

  def parseViewUser(request: Request[JsValue])(block: (ViewUser) => Future[Result] ):Future[Result] = {
    request.body.validate[ViewUser].fold(
      errors => {
        Future {
          BadRequest(JsError.toJson(errors))
        }
      }, viewUser => {
        block(viewUser)
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

  def parseLogIn(request: Request[JsValue])(block: (LogIn) => Future[Result] ):Future[Result] = {
    request.body.validate[LogIn].fold(
      errors => {
        Future {
          BadRequest(JsError.toJson(errors))
        }
      }, logIn => {
        block(logIn)
      }
    )
  }

}
