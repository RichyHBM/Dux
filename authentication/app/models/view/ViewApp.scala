package models.view

import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, JsPath, Reads, Json}

case class ViewApp(Id: Long,
                Name: String,
                Description: String){

  def toJson(): String = Json.stringify(Json.toJson(this))
}

object ViewApp {
  def fromJson(json: String): ViewApp = Json.parse(json).as[ViewApp]

  implicit val viewAppReads: Reads[ViewApp] = (
    (JsPath \ "Id").read[Long] and
      (JsPath \ "Name").read[String] and
      (JsPath \ "Description").read[String]
    )(ViewApp.apply _)

  implicit val viewAppWrites: Writes[ViewApp] = (
    (JsPath \ "Id").write[Long] and
      (JsPath \ "Name").write[String] and
      (JsPath \ "Description").write[String]
    )(unlift(ViewApp.unapply))
}