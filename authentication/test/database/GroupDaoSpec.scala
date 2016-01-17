package database

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class GroupDaoSpec extends Specification {

  "GroupDao" should {
    "Add group" in new WithApplication(FakeApp.fakeApp){
      Groups.add(new Group("TEST", "Description")).map(r => r must equalTo(1))
    }

    "Not add duplicates" in new WithApplication(FakeApp.fakeApp){
      Groups.add(new Group("TEST", "Description")).map(r => r must equalTo(1))
      Groups.add(new Group("TEST", "Description")).map(r => r must equalTo(0))
    }

    "Delete added group" in new WithApplication(FakeApp.fakeApp){
      Groups.add(new Group("TEST", "Description")).map(r => r must equalTo(1))
      Groups.delete(1).map(r => r  must equalTo(1))
    }

    "Not delete invalid group" in new WithApplication(FakeApp.fakeApp){
      Groups.delete(1).map(r => r  must equalTo(0))
    }

    "Edit group" in new WithApplication(FakeApp.fakeApp){
      Groups.add(new Group("TEST", "Description")).map(r => r must equalTo(1))
      Groups.edit(1, "GROUP", "New description").map(r => r must equalTo(1))
      Groups.get(1).map(r => {
        r must equalTo(Some)
        r match {
          case Some(r) => {
            r.Name must equalTo("GROUP")
            r.Description must equalTo("New description")
          }
        }
      })
    }

    "Not edit invalid group" in new WithApplication(FakeApp.fakeApp){
      Groups.edit(1, "GROUP", "New description").map(r => r must equalTo(0))
    }

    "Get by id" in new WithApplication(FakeApp.fakeApp){
      Groups.add(new Group("TEST", "Description")).map(r => r must equalTo(1))
      Groups.get(1).map(r => r must equalTo(Some))
    }

    "Get by name" in new WithApplication(FakeApp.fakeApp){
      Groups.add(new Group("TEST", "Description")).map(r => r must equalTo(1))
      Groups.get("TEST").map(r => r must equalTo(Some))
    }

    "Get by id invalid" in new WithApplication(FakeApp.fakeApp){
      Groups.get(1).map(r => r must equalTo(None))
    }

    "Get by name invalid" in new WithApplication(FakeApp.fakeApp){
      Groups.get("TEST").map(r => r must equalTo(None))
    }

    "Get by list" in new WithApplication(FakeApp.fakeApp){
      Groups.add(new Group("TEST", "Description")).map(r => r must equalTo(1))
      Groups.add(new Group("TEST2", "Description")).map(r => r must equalTo(1))
      Groups.add(new Group("TEST3", "Description")).map(r => r must equalTo(1))

      Groups.get( List[Long](2, 3) ).map(r => {
        r.length must equalTo(2)
        r(0).Name must equalTo("TEST2")
        r(1).Name must equalTo("TEST3")
      })
    }

    "List all groups" in new WithApplication(FakeApp.fakeApp){
      Groups.add(new Group("TEST", "Description")).map(r => r must equalTo(1))
      Groups.add(new Group("TEST2", "Description")).map(r => r must equalTo(1))
      Groups.add(new Group("TEST3", "Description")).map(r => r must equalTo(1))
      Groups.listAll.map(r => r.length must equalTo(3))
    }
  }
}
