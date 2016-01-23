package controllers

import database.{App, Apps}
import models.view.ViewApp
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.Json
import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}
import utilities._
import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class AppControllerSpec extends Specification {
  def app1 = new App("App1", "App 1 Description")

  "AppControllerSpec" should {

    "Add app" in Statics.WithFreshDatabase {
      val v = new ViewApp(0, "Test", "Description")
      val response = route(FakeRequest(POST, routes.AppController.newApp().url, Statics.jsonHeaders, v.toJson())).get
      status(response) must equalTo(OK)
    }

    "Edit app" in Statics.WithFreshDatabase {
      Await.result(Apps.add(app1), 2.seconds) must equalTo(1)
      val e = new ViewApp(1, "Test2", "Description2")
      val response = route(FakeRequest(POST, routes.AppController.editApp().url, Statics.jsonHeaders, e.toJson())).get
      contentAsString(response) must equalTo("1")
    }

    "Get all apps" in Statics.WithFreshDatabase {
      Await.result(Apps.add(app1), 2.seconds) must equalTo(1)
      val response = route(FakeRequest(POST, routes.AppController.getAllApps().url)).get
      status(response) must equalTo(OK)
      Json.fromJson[Array[ViewApp]](contentAsJson(response)).fold(
        e => 1 must equalTo(0),
        ar => ar.length must equalTo(1)
      )
    }

    "Delete app" in Statics.WithFreshDatabase {
      val e = new ViewApp(1, "Test2", "Description2")
      val response = route(FakeRequest(POST, routes.AppController.deleteApp().url, Statics.jsonHeaders, e.toJson())).get
      status(response) must equalTo(OK)
    }

    "Fail to add new app for invalid json" in Statics.WithFreshDatabase {
      val response = route(FakeRequest(POST, routes.AppController.newApp().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to edit app for invalid json" in Statics.WithFreshDatabase {
      val response = route(FakeRequest(POST, routes.AppController.editApp().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "Fail to delete app for invalid json" in Statics.WithFreshDatabase {
      val response = route(FakeRequest(POST, routes.AppController.deleteApp().url, Statics.jsonHeaders, "{}")).get
      status(response) must equalTo(BAD_REQUEST)
    }
  }
}