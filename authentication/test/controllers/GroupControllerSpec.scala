package controllers

import database._
import models.view.{ViewIdToIds, ViewId, ViewGroup}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.Json
import play.api.test.{FakeRequest, WithApplication}
import play.api.test.Helpers._
import utilities._
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class GroupControllerSpec extends Specification {
  def group1 = new Group("Group1", "Group 1 Description")
  def group2 = new Group("Group2", "Group 2 Description")
  def group3 = new Group("Group3", "Group 3 Description")

  "GroupControllerSpec" should {

    "Add group" in Statics.WithFreshDatabase {
      val v = new ViewGroup(0, "Test", "Description")
      val response = route(FakeRequest(POST, routes.GroupController.newGroup().url, Statics.jsonHeaders, v.toJson())).get
      status(response) must equalTo(OK)
    }

    "Edit group" in Statics.WithFreshDatabase {
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      val e = new ViewGroup(1, "Test2", "Description2")
      val response = route(FakeRequest(POST, routes.GroupController.editGroup().url, Statics.jsonHeaders, e.toJson())).get
      contentAsString(response) must equalTo("1")
    }

    "Get all groups" in Statics.WithFreshDatabase {
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      val response = route(FakeRequest(POST, routes.GroupController.getAllGroups().url)).get
      status(response) must equalTo(OK)
      Json.fromJson[Array[ViewGroup]](contentAsJson(response)).fold(
        e => 1 must equalTo(0),
        ar => ar.length must equalTo(1)
      )
    }

    "Get all groups with permission" in Statics.WithFreshDatabase {
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group3), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(new Permission("TEST", "Description")), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.add(1, 1), 2.seconds) must equalTo(1)
      Await.result(GroupPermissions.add(2, 1), 2.seconds) must equalTo(1)

      val e = new ViewId(1)
      val response = route(FakeRequest(POST, routes.GroupController.getAllGroupsWithPermission().url, Statics.jsonHeaders, e.toJson())).get
      status(response) must equalTo(OK)
      Json.fromJson[Array[ViewGroup]](contentAsJson(response)).fold(
        e => 1 must equalTo(0),
        ar => {
          ar.length must equalTo(2)
          ar.head.Name must equalTo(group1.Name)
          ar(1).Name must equalTo(group2.Name)
        }
      )
    }

    "Add groups to permission" in Statics.WithFreshDatabase {
      Await.result(Groups.add(group1), 2.seconds) must equalTo(1)
      Await.result(Groups.add(group2), 2.seconds) must equalTo(1)

      Await.result(Permissions.add(new Permission("TEST", "Description")), 2.seconds) must equalTo(1)

      Await.result(GroupPermissions.add(2, 1), 2.seconds) must equalTo(1)

      val e = new ViewIdToIds(1, Seq(1,2))
      val response = route(FakeRequest(POST, routes.GroupController.addGroupsToPermission().url, Statics.jsonHeaders, e.toJson())).get
      status(response) must equalTo(OK)
      contentAsString(response) must equalTo("2")

      Await.result(GroupPermissions.listAll(), 2.seconds).length must equalTo(2)
    }

    "Delete group" in Statics.WithFreshDatabase {
      val e = new ViewGroup(1, "Test2", "Description2")
      val response = route(FakeRequest(POST, routes.GroupController.deleteGroup().url, Statics.jsonHeaders, e.toJson())).get
      status(response) must equalTo(OK)
    }

    "Fail to add new group for invalid json" in Statics.WithFreshDatabase {
      val response = route(FakeRequest(POST, routes.GroupController.newGroup().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to edit group for invalid json" in Statics.WithFreshDatabase {
      val response = route(FakeRequest(POST, routes.GroupController.editGroup().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to delete group for invalid json" in Statics.WithFreshDatabase {
      val response = route(FakeRequest(POST, routes.GroupController.deleteGroup().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }
  }
}