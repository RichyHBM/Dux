package controllers

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.test.{FakeRequest, WithApplication}
import play.api.test.Helpers._
import utilities._

@RunWith(classOf[JUnitRunner])
class GroupControllerSpec extends Specification {

  "GroupControllerSpec" should {

    "Fail to add new group for invalid json" in new WithApplication{
      val add = route(FakeRequest(POST, routes.GroupController.newGroup().url, Statics.jsonHeaders, "{}")).get
      status(add) must equalTo(BAD_REQUEST)
    }

    "Fail to edit group for invalid json" in new WithApplication{
      val add = route(FakeRequest(POST, routes.GroupController.editGroup().url, Statics.jsonHeaders, "{}")).get
      status(add) must equalTo(BAD_REQUEST)
    }
  }
}