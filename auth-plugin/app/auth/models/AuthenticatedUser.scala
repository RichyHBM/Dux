package auth.models

import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.beans.BeanProperty

case class AuthenticatedUser(@BeanProperty var id: Long,
                             @BeanProperty var name: String,
                             @BeanProperty var email: String) {

  def toJson(): String = Json.stringify(Json.toJson(this))
}

object AuthenticatedUser {
  def fromJson(json: String): AuthenticatedUser = Json.parse(json).as[AuthenticatedUser]

  implicit val authenticatedUserReads: Reads[AuthenticatedUser] = (
      (JsPath \ "id").read[Long] and
      (JsPath \ "name").read[String] and
      (JsPath \ "email").read[String]
    )(AuthenticatedUser.apply _)

  implicit val authenticatedUserWrites: Writes[AuthenticatedUser] = (
      (JsPath \ "id").write[Long] and
      (JsPath \ "name").write[String] and
      (JsPath \ "email").write[String]
    )(unlift(AuthenticatedUser.unapply))

}