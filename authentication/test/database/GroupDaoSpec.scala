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
class GroupDaoSpec extends Specification {

  "GroupDao" should {
    "Add group" in Statics.WithFreshDatabase {
      Await.result(Groups.add(new Group("TEST", "Description")), 2.seconds) must equalTo(1)
    }

    "Not add duplicates" in Statics.WithFreshDatabase {
      Await.result(Groups.add(new Group("TEST", "Description")), 2.seconds) must equalTo(1)

      try {
        Await.result(Groups.add(new Group("TEST", "Description")), 2.seconds) must equalTo(0)
        false must equalTo(true)
      } catch {
        case _: Exception => true must equalTo(true)
      }
    }

    "Delete added group" in Statics.WithFreshDatabase {
      Await.result(Groups.add(new Group("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Groups.delete(1), 2.seconds) must equalTo(1)
    }

    "Not delete invalid group" in Statics.WithFreshDatabase {
      Await.result(Groups.delete(1), 2.seconds) must equalTo(0)
    }

    "Edit group" in Statics.WithFreshDatabase {
      Await.result(Groups.add(new Group("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Groups.edit(1, "GROUP", "New description"), 2.seconds) must equalTo(1)

      val r = Await.result(Groups.get(1), 2.seconds)

      r match {
        case Some(r) => {
          r.Name must equalTo("GROUP")
          r.Description must equalTo("New description")
        }
        case None => 0 must equalTo(1)
      }
    }

    "Not edit invalid group" in Statics.WithFreshDatabase {
      Await.result(Groups.edit(1, "GROUP", "New description"), 2.seconds) must equalTo(0)
    }

    "Get by id" in Statics.WithFreshDatabase {
      Await.result(Groups.add(new Group("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Groups.get(1), 2.seconds) must equalTo(Some(Group(1,"TEST","Description")))
    }

    "Get by name" in Statics.WithFreshDatabase {
      Await.result(Groups.add(new Group("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Groups.get("TEST"), 2.seconds) must equalTo(Some(Group(1,"TEST","Description")))
    }

    "Get by id invalid" in Statics.WithFreshDatabase {
      Await.result(Groups.get(1), 2.seconds) must equalTo(None)
    }

    "Get by name invalid" in Statics.WithFreshDatabase {
      Await.result(Groups.get("TEST"), 2.seconds) must equalTo(None)
    }

    "Get by list" in Statics.WithFreshDatabase {
      Await.result(Groups.add(new Group("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Groups.add(new Group("TEST2", "Description")), 2.seconds) must equalTo(1)
      Await.result(Groups.add(new Group("TEST3", "Description")), 2.seconds) must equalTo(1)

      val r = Await.result(Groups.get( List[Long](2, 3) ), 2.seconds)
      r.length must equalTo(2)
      r(0).Name must equalTo("TEST2")
      r(1).Name must equalTo("TEST3")
    }

    "List all groups" in Statics.WithFreshDatabase {
      Await.result(Groups.add(new Group("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Groups.add(new Group("TEST2", "Description")), 2.seconds) must equalTo(1)
      Await.result(Groups.add(new Group("TEST3", "Description")), 2.seconds) must equalTo(1)
      Await.result(Groups.listAll, 2.seconds).length must equalTo(3)
    }
  }
}
