package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class AuthToken(Token: String, Permission: String){
  def toJson(): String = Json.stringify(Json.toJson(this))
}

object AuthToken {
  def fromJson(json: String): AuthToken = Json.parse(json).as[AuthToken]

  implicit val authTokenReads: Reads[AuthToken] = (
      (JsPath \ "Token").read[String] and
      (JsPath \ "Permission").read[String]
    )(AuthToken.apply _)

  implicit val authTokenWrites: Writes[AuthToken] = (
      (JsPath \ "Token").write[String] and
      (JsPath \ "Permission").write[String]
    )(unlift(AuthToken.unapply))
}