package utilities

import models._
import models.view._
import play.api.libs.json.{JsError, JsValue}
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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

  def parseViewId(request: Request[JsValue])(block: (ViewId) => Future[Result] ):Future[Result] = {
    request.body.validate[ViewId].fold(
      errors => {
        Future {
          BadRequest(JsError.toJson(errors))
        }
      }, viewId => {
        block(viewId)
      }
    )
  }

  def parseViewIdToIds(request: Request[JsValue])(block: (ViewIdToIds) => Future[Result] ):Future[Result] = {
    request.body.validate[ViewIdToIds].fold(
      errors => {
        Future {
          BadRequest(JsError.toJson(errors))
        }
      }, viewIdToIds => {
        block(viewIdToIds)
      }
    )
  }

  def parseAuthToken(request: Request[JsValue])(block: (AuthToken) => Future[Result] ):Future[Result] = {
    request.body.validate[AuthToken].fold(
      errors => {
        Future {
          BadRequest(JsError.toJson(errors))
        }
      }, authToken => {
        block(authToken)
      }
    )
  }

}
