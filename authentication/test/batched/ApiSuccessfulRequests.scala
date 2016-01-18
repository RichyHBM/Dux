package batched

import controllers.routes
import models.NewUser
import models.view._
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utilities._
import scala.concurrent.ExecutionContext.Implicits.global

//Due to a ThreadExecutor issue during unit testing,
// all API calls that interact with Slick need to be run together and before any other test
class ApiSuccessfulRequests extends APIBatchRunner[ApiSuccessfulRequests] {

  //<editor-fold desc="App Tests">

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

  def GetAllApps = {
    val response = route(FakeRequest(POST, routes.AppController.getAllApps().url)).get
    status(response) must equalTo(OK)
    Json.fromJson[Array[ViewApp]](contentAsJson(response)).fold(
      e => 1 must equalTo(0),
      ar => ar.length must equalTo(1)
    )
  }

  def DeleteApp = {
    val e = new ViewApp(1, "Test2", "Description2")
    val edit = route(FakeRequest(POST, routes.AppController.deleteApp().url, Statics.jsonHeaders, e.toJson())).get
    status(edit) must equalTo(OK)
  }

  //</editor-fold>

  //<editor-fold desc="Group Tests">

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

  def GetAllGroups = {
    val response = route(FakeRequest(POST, routes.GroupController.getAllGroups().url)).get
    status(response) must equalTo(OK)
    Json.fromJson[Array[ViewGroup]](contentAsJson(response)).fold(
      e => 1 must equalTo(0),
      ar => ar.length must equalTo(1)
    )
  }

  def DeleteGroup = {
    val e = new ViewGroup(1, "Test2", "Description2")
    val edit = route(FakeRequest(POST, routes.GroupController.deleteGroup().url, Statics.jsonHeaders, e.toJson())).get
    status(edit) must equalTo(OK)
  }

  //</editor-fold>

  //<editor-fold desc="Permission Tests">

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

  def GetAllPermissions = {
    val response = route(FakeRequest(POST, routes.PermissionController.getAllPermissions().url)).get
    status(response) must equalTo(OK)
    Json.fromJson[Array[ViewPermission]](contentAsJson(response)).fold(
      e => 1 must equalTo(0),
      ar => ar.length must equalTo(1)
    )
  }

  def DeletePermission = {
    val e = new ViewPermission(1, "Test2", "Description2")
    val edit = route(FakeRequest(POST, routes.PermissionController.deletePermission().url, Statics.jsonHeaders, e.toJson())).get
    status(edit) must equalTo(OK)
  }

  //</editor-fold>

  //<editor-fold desc="User Tests">

  def AddNewUser = {
    val u = new NewUser("John Doe", "test@test.test", "Password", "Password")
    val add = route(FakeRequest(POST, routes.UserController.newUser().url, Statics.jsonHeaders, u.toJson())).get
    status(add) must equalTo(OK)
  }

  def GetAllUsers = {
    val response = route(FakeRequest(POST, routes.UserController.getAllUsers().url)).get
    status(response) must equalTo(OK)
    Json.fromJson[Array[ViewUser]](contentAsJson(response)).fold(
      e => 1 must equalTo(0),
      ar => ar.length must equalTo(1)
    )
  }

  //</editor-fold>

}
