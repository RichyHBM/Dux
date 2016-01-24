package models.view

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class ViewId(Id: Long){
  def toJson(): String = Json.stringify(Json.toJson(this))
}

object ViewId {
  def fromJson(json: String): ViewId = Json.parse(json).as[ViewId]
  implicit val viewIdReads: Reads[ViewId] = (JsPath \ "Id").read[Long].map(ViewId(_))
  implicit val viewIdWrites: Writes[ViewId] = (JsPath \ "Id").write[Long].contramap[ViewId](_.Id)
}
