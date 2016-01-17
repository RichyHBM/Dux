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
      Apps.add(new App("TEST", "Description")).map(r => r must equalTo(1))
    }

    "Not add duplicates" in new WithApplication(FakeApp.fakeApp){
      Apps.add(new App("TEST", "Description")).map(r => r must equalTo(1))
      Apps.add(new App("TEST", "Description")).map(r => r must equalTo(0))
    }

    "Delete added app" in new WithApplication(FakeApp.fakeApp){
      Apps.add(new App("TEST", "Description")).map(r => r must equalTo(1))
      Apps.delete(1).map(r => r  must equalTo(1))
    }

    "Not delete invalid app" in new WithApplication(FakeApp.fakeApp){
      Apps.delete(1).map(r => r  must equalTo(0))
    }

    "Edit app" in new WithApplication(FakeApp.fakeApp){
      Apps.add(new App("TEST", "Description")).map(r => r must equalTo(1))
      Apps.edit(1, "APP", "New description").map(r => r must equalTo(1))
      Apps.get(1).map(r => {
        r must equalTo(Some)
        r match {
          case Some(r) => {
            r.Name must equalTo("APP")
            r.Description must equalTo("New description")
          }
          case None => {}
        }
      })
    }

    "Not edit invalid app" in new WithApplication(FakeApp.fakeApp){
      Apps.edit(1, "APP", "New description").map(r => r must equalTo(0))
    }

    "Get by id" in new WithApplication(FakeApp.fakeApp){
      Apps.add(new App("TEST", "Description")).map(r => r must equalTo(1))
      Apps.get(1).map(r => r must equalTo(Some))
    }

    "Get by name" in new WithApplication(FakeApp.fakeApp){
      Apps.add(new App("TEST", "Description")).map(r => r must equalTo(1))
      Apps.get("TEST").map(r => r must equalTo(Some))
    }

    "Get by id invalid" in new WithApplication(FakeApp.fakeApp){
      Apps.get(1).map(r => r must equalTo(None))
    }

    "Get by name invalid" in new WithApplication(FakeApp.fakeApp){
      Apps.get("TEST").map(r => r must equalTo(None))
    }

    "Get by list" in new WithApplication(FakeApp.fakeApp){
      Apps.add(new App("TEST", "Description")).map(r => r must equalTo(1))
      Apps.add(new App("TEST2", "Description")).map(r => r must equalTo(1))
      Apps.add(new App("TEST3", "Description")).map(r => r must equalTo(1))

      Apps.get( List[Long](2, 3) ).map(r => {
        r.length must equalTo(2)
        r(0).Name must equalTo("TEST2")
        r(1).Name must equalTo("TEST3")
      })
    }

    "List all apps" in new WithApplication(FakeApp.fakeApp){
      Apps.add(new App("TEST", "Description")).map(r => r must equalTo(1))
      Apps.add(new App("TEST2", "Description")).map(r => r must equalTo(1))
      Apps.add(new App("TEST3", "Description")).map(r => r must equalTo(1))
      Apps.listAll.map(r => r.length must equalTo(3))
    }
  }
}
