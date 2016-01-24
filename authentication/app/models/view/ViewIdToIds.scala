package models.view

import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, JsPath, Reads, Json}

case class ViewIdToIds(Id: Long, Ids: Seq[Long]){
  def toJson(): String = Json.stringify(Json.toJson(this))
}

object ViewIdToIds {
  def fromJson(json: String): ViewIdToIds = Json.parse(json).as[ViewIdToIds]

  implicit val viewIdToIdsReads: Reads[ViewIdToIds] = (
    (JsPath \ "Id").read[Long] and
      (JsPath \ "Ids").read[Seq[Long]]
    )(ViewIdToIds.apply _)

  implicit val viewIdToIdsWrites: Writes[ViewIdToIds] = (
    (JsPath \ "Id").write[Long] and
      (JsPath \ "Ids").write[Seq[Long]]
    )(unlift(ViewIdToIds.unapply))
}