package auth.models

import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, JsPath, Reads, Json}

case class SessionUser(sessionKey: String, user: AuthenticatedUser){
  def toJson(): String = Json.stringify(Json.toJson(this))
}

object SessionUser {
  def fromJson(json: String): SessionUser = Json.parse(json).as[SessionUser]

  implicit val sessionUserReads: Reads[SessionUser] = (
      (JsPath \ "sessionKey").read[String] and
      (JsPath \ "user").read[AuthenticatedUser]
    )(SessionUser.apply _)

  implicit val sessionUserWrites: Writes[SessionUser] = (
      (JsPath \ "sessionKey").write[String] and
      (JsPath \ "user").write[AuthenticatedUser]
    )(unlift(SessionUser.unapply))
}