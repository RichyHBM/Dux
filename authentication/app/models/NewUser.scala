package models

import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, JsPath, Reads, Json}

case class NewUser(Name: String,
                   Email: String,
                   Password: String,
                   Confirmation: String) {

  def toJson(): String = Json.stringify(Json.toJson(this))
}

object NewUser {
  def fromJson(json: String): NewUser = Json.parse(json).as[NewUser]

  implicit val newUserReads: Reads[NewUser] = (
      (JsPath \ "Name").read[String] and
      (JsPath \ "Email").read[String] and
      (JsPath \ "Password").read[String] and
      (JsPath \ "Confirmation").read[String]
    )(NewUser.apply _)

  implicit val newUserWrites: Writes[NewUser] = (
      (JsPath \ "Name").write[String] and
      (JsPath \ "Email").write[String] and
      (JsPath \ "Password").write[String] and
      (JsPath \ "Confirmation").write[String]
    )(unlift(NewUser.unapply))

}
