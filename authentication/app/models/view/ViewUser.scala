package models.view

import java.util.Date

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads, Writes}

case class ViewUser(Id: Long,
                    Name: String,
                    Email: String,
                    ApiKey: String,
                    CreatedOn: Date,
                    FailedAttempts: Int,
                    Blocked: Boolean){

  def toJson(): String = Json.stringify(Json.toJson(this))
}

object ViewUser {
  def fromJson(json: String): ViewUser = Json.parse(json).as[ViewUser]

  implicit val viewUserReads: Reads[ViewUser] = (
    (JsPath \ "Id").read[Long] and
      (JsPath \ "Name").read[String] and
      (JsPath \ "Email").read[String] and
      (JsPath \ "ApiKey").read[String] and
      (JsPath \ "CreatedOn").read[Date] and
      (JsPath \ "FailedAttempts").read[Int] and
      (JsPath \ "Blocked").read[Boolean]
    )(ViewUser.apply _)

  implicit val viewUserWrites: Writes[ViewUser] = (
    (JsPath \ "Id").write[Long] and
      (JsPath \ "Name").write[String] and
      (JsPath \ "Email").write[String] and
      (JsPath \ "ApiKey").write[String] and
      (JsPath \ "CreatedOn").write[Date] and
      (JsPath \ "FailedAttempts").write[Int] and
      (JsPath \ "Blocked").write[Boolean]
    )(unlift(ViewUser.unapply))

}

