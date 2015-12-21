package models

import java.util.Date

import play.api.libs.json.{JsPath, Reads, Writes, Json}
import play.api.libs.functional.syntax._

case class UserSession(id: Long,
                       name: String,
                       email: String,
                       loggedInAt: Date,
                       lastSeen: Date,
                       lastUsing: String) {

  def toJson(): String = Json.stringify(Json.toJson(this))
}

object UserSession {
  def fromJson(json: String): UserSession = Json.parse(json).as[UserSession]

  implicit val userSessionReads: Reads[UserSession] = (
      (JsPath \ "id").read[Long] and
      (JsPath \ "name").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ "loggedInAt").read[Date] and
      (JsPath \ "lastSeen").read[Date] and
      (JsPath \ "lastUsing").read[String]
    )(UserSession.apply _)

  implicit val userSessionWrites: Writes[UserSession] = (
      (JsPath \ "id").write[Long] and
      (JsPath \ "name").write[String] and
      (JsPath \ "email").write[String] and
      (JsPath \ "loggedInAt").write[Date] and
      (JsPath \ "lastSeen").write[Date] and
      (JsPath \ "lastUsing").write[String]
    )(unlift(UserSession.unapply))

}
