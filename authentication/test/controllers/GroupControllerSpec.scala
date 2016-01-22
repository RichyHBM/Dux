package controllers

import database.{Group, Groups}
import models.view.ViewGroup
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.Json
import play.api.test.{FakeRequest, WithApplication}
import play.api.test.Helpers._
import utilities._
import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class GroupControllerSpec extends Specification {

  "GroupControllerSpec" should {

    "Add group" in new WithApplication {
      Groups.add(new Group("TEST", "Description")).map(r => {
        val v = new ViewGroup(0, "Test", "Description")
        val response = route(FakeRequest(POST, routes.GroupController.newGroup().url, Statics.jsonHeaders, v.toJson())).get
        status(response) must equalTo(OK)
      })
    }

    "Edit group" in new WithApplication {
      Groups.add(new Group("TEST", "Description")).map(r => {
        val e = new ViewGroup(1, "Test2", "Description2")
        val response = route(FakeRequest(POST, routes.GroupController.editGroup().url, Statics.jsonHeaders, e.toJson())).get
        status(response) must equalTo(OK)
      })
    }

    "Get all groups" in new WithApplication {
      Groups.add(new Group("TEST", "Description")).map(r => {
        val response = route(FakeRequest(POST, routes.GroupController.getAllGroups().url)).get
        status(response) must equalTo(OK)
        Json.fromJson[Array[ViewGroup]](contentAsJson(response)).fold(
          e => 1 must equalTo(0),
          ar => ar.length must equalTo(1)
        )
      })
    }

    "Delete group" in new WithApplication {
      Groups.add(new Group("TEST", "Description")).map(r => {
        val e = new ViewGroup(1, "Test2", "Description2")
        val response = route(FakeRequest(POST, routes.GroupController.deleteGroup().url, Statics.jsonHeaders, e.toJson())).get
        status(response) must equalTo(OK)
      })
    }

    "Fail to add new group for invalid json" in new WithApplication {
      val response = route(FakeRequest(POST, routes.GroupController.newGroup().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to edit group for invalid json" in new WithApplication {
      val response = route(FakeRequest(POST, routes.GroupController.editGroup().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to delete group for invalid json" in new WithApplication {
      val response = route(FakeRequest(POST, routes.GroupController.deleteGroup().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }
  }
}