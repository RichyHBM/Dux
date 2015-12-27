package models.view

import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, JsPath, Reads, Json}

case class ViewGroup(Id: Long,
               Name: String,
               Description: String){

  def toJson(): String = Json.stringify(Json.toJson(this))
}

object ViewGroup {
  def fromJson(json: String): ViewGroup = Json.parse(json).as[ViewGroup]

  implicit val viewGroupReads: Reads[ViewGroup] = (
    (JsPath \ "Id").read[Long] and
      (JsPath \ "Name").read[String] and
      (JsPath \ "Description").read[String]
    )(ViewGroup.apply _)

  implicit val viewGroupWrites: Writes[ViewGroup] = (
    (JsPath \ "Id").write[Long] and
      (JsPath \ "Name").write[String] and
      (JsPath \ "Description").write[String]
    )(unlift(ViewGroup.unapply))
}