package batched

import controllers.routes
import models.NewUser
import models.view._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utilities._

//Due to a ThreadExecutor issue during unit testing,
// all API calls that interact with Slick need to be run together and before any other test
class ApiSuccessfulRequests extends APIBatchRunner[ApiSuccessfulRequests] {
  def AddNewApp = {
    val v = new ViewApp(0, "Test", "Description")
    val add = route(FakeRequest(POST, routes.AppController.newApp().url, Statics.jsonHeaders, v.toJson())).get
    status(add) must equalTo(OK)
  }

  def EditNewApp = {
    val e = new ViewApp(1, "Test2", "Description2")
    val edit = route(FakeRequest(POST, routes.AppController.editApp().url, Statics.jsonHeaders, e.toJson())).get
    status(edit) must equalTo(OK)
  }

  def AddNewGroup = {
    val v = new ViewGroup(0, "Test", "Description")
    val add = route(FakeRequest(POST, routes.GroupController.newGroup().url, Statics.jsonHeaders, v.toJson())).get
    status(add) must equalTo(OK)
  }

  def EditNewGroup = {
    val e = new ViewGroup(1, "Test2", "Description2")
    val edit = route(FakeRequest(POST, routes.GroupController.editGroup().url, Statics.jsonHeaders, e.toJson())).get
    status(edit) must equalTo(OK)
  }

  def AddNewPermission = {
    val v = new ViewPermission(0, "Test", "Description")
    val add = route(FakeRequest(POST, routes.PermissionController.newPermission().url, Statics.jsonHeaders, v.toJson())).get
    status(add) must equalTo(OK)
  }

  def EditNewPermission = {
    val e = new ViewPermission(1, "Test2", "Description2")
    val edit = route(FakeRequest(POST, routes.PermissionController.editPermission().url, Statics.jsonHeaders, e.toJson())).get
    status(edit) must equalTo(OK)
  }

  def AddNewUser = {
    val u = new NewUser("John Doe", "test@test.test", "Password", "Password")
    val add = route(FakeRequest(POST, routes.UserController.newUser().url, Statics.jsonHeaders, u.toJson())).get
    status(add) must equalTo(OK)
  }
}
