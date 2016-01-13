package database

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class PermissionDaoSpec extends Specification {

  "PermissionDao" should {
    "Add permission" in new WithApplication(FakeApp.fakeApp){
      Permissions.add(new Permission("TEST", "Description")).map(r => r must equalTo(1))
    }
    "Not add duplicates" in new WithApplication(FakeApp.fakeApp){
      Permissions.add(new Permission("TEST", "Description")).map(r => r must equalTo(1))
      Permissions.add(new Permission("TEST", "Description")).map(r => r must equalTo(0))
    }
    "Delete added group" in new WithApplication(FakeApp.fakeApp){
      Permissions.add(new Permission("TEST", "Description")).map(r => r must equalTo(1))
      Permissions.delete(1).map(r => r  must equalTo(1))
    }

    "Not delete invalid group" in new WithApplication(FakeApp.fakeApp){
      Permissions.delete(1).map(r => r  must equalTo(0))
    }

    "Set description" in new WithApplication(FakeApp.fakeApp){
      Permissions.add(new Permission("TEST", "Description")).map(r => r must equalTo(1))
      Permissions.setDescription(1, "New description").map(r => r must equalTo(1))
    }

    "Not set invalid description" in new WithApplication(FakeApp.fakeApp){
      Permissions.setDescription(1, "New description").map(r => r must equalTo(0))
    }

    "Get by id" in new WithApplication(FakeApp.fakeApp){
      Permissions.add(new Permission("TEST", "Description")).map(r => r must equalTo(1))
      Permissions.get(1).map(r => r must equalTo(Some))
    }

    "Get by name" in new WithApplication(FakeApp.fakeApp){
      Permissions.add(new Permission("TEST", "Description")).map(r => r must equalTo(1))
      Permissions.get("TEST").map(r => r must equalTo(Some))
    }

    "Get by id invalid" in new WithApplication(FakeApp.fakeApp){
      Permissions.get(1).map(r => r must equalTo(None))
    }

    "Get by name invalid" in new WithApplication(FakeApp.fakeApp){
      Permissions.get("TEST").map(r => r must equalTo(None))
    }

    "Get by list" in new WithApplication(FakeApp.fakeApp){
      Permissions.add(new Permission("TEST", "Description")).map(r => r must equalTo(1))
      Permissions.add(new Permission("TEST2", "Description")).map(r => r must equalTo(1))
      Permissions.add(new Permission("TEST3", "Description")).map(r => r must equalTo(1))

      Permissions.get( List[Long](2, 3) ).map(r => {
        r.length must equalTo(2)
        r(0).Name must equalTo("TEST2")
        r(1).Name must equalTo("TEST3")
      })
    }

    "List all permissions" in new WithApplication(FakeApp.fakeApp){
      Permissions.add(new Permission("TEST", "Description")).map(r => r must equalTo(1))
      Permissions.add(new Permission("TEST2", "Description")).map(r => r must equalTo(1))
      Permissions.add(new Permission("TEST3", "Description")).map(r => r must equalTo(1))
      Permissions.listAll.map(r => r.length must equalTo(3))
    }
  }
}
