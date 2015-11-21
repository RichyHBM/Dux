package controllers

import com.fasterxml.jackson.databind.node.ObjectNode
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.mvc.Http
import views.html._

import common.models.BasicViewResponse

class Application extends Controller {

  def index = Action {
    val response = new BasicViewResponse("Data Store")
    val viewData: ObjectNode = play.libs.Json.newObject()
    Ok(views.html.index.render(viewData, response))
  }

}
