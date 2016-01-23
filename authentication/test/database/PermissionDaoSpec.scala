package database

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import utilities._
import play.api.test._
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class PermissionDaoSpec extends Specification {

  "PermissionDao" should {
    "Add permission" in Statics.WithFreshDatabase {
      Await.result(Permissions.add(new Permission("TEST", "Description")), 2.seconds) must equalTo(1)
    }
    "Not add duplicates" in Statics.WithFreshDatabase {
      Await.result(Permissions.add(new Permission("TEST", "Description")), 2.seconds) must equalTo(1)
      try {
        Await.result(Permissions.add(new Permission("TEST", "Description")), 2.seconds) must equalTo(0)
        false must equalTo(true)
      } catch {
        case _: Exception => true must equalTo(true)
      }
    }
    "Delete added group" in Statics.WithFreshDatabase {
      Await.result(Permissions.add(new Permission("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Permissions.delete(1), 2.seconds) must equalTo(1)
    }

    "Not delete invalid group" in Statics.WithFreshDatabase {
      Await.result(Permissions.delete(1), 2.seconds) must equalTo(0)
    }

    "Edit permission" in Statics.WithFreshDatabase {
      Await.result(Permissions.add(new Permission("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Permissions.edit(1, "PERMISSION", "New description"), 2.seconds) must equalTo(1)
      val r = Await.result(Permissions.get(1), 2.seconds)

      r must equalTo(Some(Permission(1, "PERMISSION", "New description")))
      r match {
        case Some(r) => {
          r.Name must equalTo("PERMISSION")
          r.Description must equalTo("New description")
        }
        case None => false must equalTo(true)
      }
    }

    "Not edit invalid permission" in Statics.WithFreshDatabase {
      Await.result(Permissions.edit(1, "PERMISSION", "New description"), 2.seconds) must equalTo(0)
    }

    "Get by id" in Statics.WithFreshDatabase {
      Await.result(Permissions.add(new Permission("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Permissions.get(1), 2.seconds) must equalTo(Some(Permission(1, "TEST", "Description")))
    }

    "Get by name" in Statics.WithFreshDatabase {
      Await.result(Permissions.add(new Permission("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Permissions.get("TEST"), 2.seconds) must equalTo(Some(Permission(1, "TEST", "Description")))
    }

    "Get by id invalid" in Statics.WithFreshDatabase {
      Await.result(Permissions.get(1), 2.seconds) must equalTo(None)
    }

    "Get by name invalid" in Statics.WithFreshDatabase {
      Await.result(Permissions.get("TEST"), 2.seconds) must equalTo(None)
    }

    "Get by list" in Statics.WithFreshDatabase {
      Await.result(Permissions.add(new Permission("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(new Permission("TEST2", "Description")), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(new Permission("TEST3", "Description")), 2.seconds) must equalTo(1)

      val r = Await.result(Permissions.get( List[Long](2, 3) ), 2.seconds)
      r.length must equalTo(2)
      r(0).Name must equalTo("TEST2")
      r(1).Name must equalTo("TEST3")
    }

    "List all permissions" in Statics.WithFreshDatabase {
      Await.result(Permissions.add(new Permission("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(new Permission("TEST2", "Description")), 2.seconds) must equalTo(1)
      Await.result(Permissions.add(new Permission("TEST3", "Description")), 2.seconds) must equalTo(1)
      Await.result(Permissions.listAll, 2.seconds).length must equalTo(3)
    }
  }
}
