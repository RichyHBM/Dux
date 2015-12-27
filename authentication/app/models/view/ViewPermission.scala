package models.view

import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, JsPath, Reads, Json}


case class ViewPermission(Id: Long,
                 Name: String,
                 Description: String){

  def toJson(): String = Json.stringify(Json.toJson(this))
}

object ViewPermission {
  def fromJson(json: String): ViewPermission = Json.parse(json).as[ViewPermission]

  implicit val viewPermissionReads: Reads[ViewPermission] = (
    (JsPath \ "Id").read[Long] and
      (JsPath \ "Name").read[String] and
      (JsPath \ "Description").read[String]
    )(ViewPermission.apply _)

  implicit val viewPermissionWrites: Writes[ViewPermission] = (
    (JsPath \ "Id").write[Long] and
      (JsPath \ "Name").write[String] and
      (JsPath \ "Description").write[String]
    )(unlift(ViewPermission.unapply))
}