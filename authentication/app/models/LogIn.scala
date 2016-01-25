package models

import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, JsPath, Reads, Json}

case class LogIn(Email: String, Password: String, Permission: String) {

  def toJson(): String = Json.stringify(Json.toJson(this))
}

object LogIn {
  def fromJson(json: String): LogIn = Json.parse(json).as[LogIn]

  implicit val logInReads: Reads[LogIn] = (
      (JsPath \ "Email").read[String] and
      (JsPath \ "Password").read[String] and
      (JsPath \ "Permission").read[String]
    )(LogIn.apply _)

  implicit val logInWrites: Writes[LogIn] = (
      (JsPath \ "Email").write[String] and
      (JsPath \ "Password").write[String] and
      (JsPath \ "Permission").write[String]
    )(unlift(LogIn.unapply))

}
