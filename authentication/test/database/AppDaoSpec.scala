package database

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class AppDaoSpec extends Specification {

  "AppDAO" should {

    "Add app" in new WithApplication(FakeApp.fakeApp){
      Apps.add(new App(1, "TEST", "Description")).map(r => r must equalTo(1))
    }

    "Delete added app" in new WithApplication(FakeApp.fakeApp){
      Apps.add(new App(1, "TEST", "Description")).map(r => r must equalTo(1))
      Apps.delete(1).map(r => r  must equalTo(1))
    }

    "Not delete invalid app" in new WithApplication(FakeApp.fakeApp){
      Apps.delete(1).map(r => r  must equalTo(0))
    }

    "Set description" in new WithApplication(FakeApp.fakeApp){
      Apps.add(new App(1, "TEST", "Description")).map(r => r must equalTo(1))
      Apps.setDescription(1, "New description").map(r => r must equalTo(1))
    }

    "Not set invalid description" in new WithApplication(FakeApp.fakeApp){
      Apps.setDescription(1, "New description").map(r => r must equalTo(0))
    }

    "Get by id" in new WithApplication(FakeApp.fakeApp){
      Apps.add(new App(1, "TEST", "Description")).map(r => r must equalTo(1))
      Apps.get(1).map(r => r must equalTo(Some))
    }

    "Get by name" in new WithApplication(FakeApp.fakeApp){
      Apps.add(new App(1, "TEST", "Description")).map(r => r must equalTo(1))
      Apps.get("TEST").map(r => r must equalTo(Some))
    }

    "Get by id invalid" in new WithApplication(FakeApp.fakeApp){
      Apps.get(1).map(r => r must equalTo(None))
    }

    "Get by name invalid" in new WithApplication(FakeApp.fakeApp){
      Apps.get("TEST").map(r => r must equalTo(None))
    }

    "List all apps" in new WithApplication(FakeApp.fakeApp){
      Apps.add(new App(1, "TEST", "Description")).map(r => r must equalTo(1))
      Apps.add(new App(1, "TEST2", "Description")).map(r => r must equalTo(1))
      Apps.add(new App(1, "TEST3", "Description")).map(r => r must equalTo(1))
      Apps.listAll.map(r => r.length must equalTo(3))
    }
  }
}
