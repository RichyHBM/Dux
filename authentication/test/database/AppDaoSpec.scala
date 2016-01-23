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
class AppDaoSpec extends Specification {

  "AppDAO" should {

    "Add app" in Statics.WithFreshDatabase {
      Await.result(Apps.add(new App("TEST", "Description")), 2.seconds) must equalTo(1)
    }

    "Not add duplicates" in Statics.WithFreshDatabase {
      Await.result(Apps.add(new App("TEST", "Description")), 2.seconds) must equalTo(1)
      try {
        Await.result(Apps.add(new App("TEST", "Description")), 2.seconds) must equalTo(0)
        false must equalTo(true)
      } catch {
        case _: Exception => true must equalTo(true)
      }
    }

    "Delete added app" in Statics.WithFreshDatabase {
      Await.result(Apps.add(new App("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Apps.delete(1), 2.seconds) must equalTo(1)
    }

    "Not delete invalid app" in Statics.WithFreshDatabase {
      Await.result(Apps.delete(1), 2.seconds) must equalTo(0)
    }

    "Edit app" in Statics.WithFreshDatabase {
      Await.result(Apps.add(new App("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Apps.edit(1, "APP", "New description"), 2.seconds) must equalTo(1)
      val r = Await.result(Apps.get(1), 2.seconds)
      r must equalTo(Some(App(1, "APP", "New description")))
      r match {
        case Some(r) => {
          r.Name must equalTo("APP")
          r.Description must equalTo("New description")
        }
        case None => 1 must equalTo(0)
      }
    }

    "Not edit invalid app" in Statics.WithFreshDatabase {
      Await.result(Apps.edit(1, "APP", "New description"), 2.seconds) must equalTo(0)
    }

    "Get by id" in Statics.WithFreshDatabase {
      Await.result(Apps.add(new App("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Apps.get(1), 2.seconds) must equalTo(Some(App(1, "TEST", "Description")))
    }

    "Get by name" in Statics.WithFreshDatabase {
      Await.result(Apps.add(new App("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Apps.get("TEST"), 2.seconds) must equalTo(Some(App(1, "TEST", "Description")))
    }

    "Get by id invalid" in Statics.WithFreshDatabase {
      Await.result(Apps.get(1), 2.seconds) must equalTo(None)
    }

    "Get by name invalid" in Statics.WithFreshDatabase {
      Await.result(Apps.get("TEST"), 2.seconds) must equalTo(None)
    }

    "Get by list" in Statics.WithFreshDatabase {
      Await.result(Apps.add(new App("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Apps.add(new App("TEST2", "Description")), 2.seconds) must equalTo(1)
      Await.result(Apps.add(new App("TEST3", "Description")), 2.seconds) must equalTo(1)

      val r = Await.result(Apps.get( List[Long](2, 3) ), 2.seconds)
      r.length must equalTo(2)
      r(0).Name must equalTo("TEST2")
      r(1).Name must equalTo("TEST3")
    }

    "List all apps" in Statics.WithFreshDatabase {
      Await.result(Apps.add(new App("TEST", "Description")), 2.seconds) must equalTo(1)
      Await.result(Apps.add(new App("TEST2", "Description")), 2.seconds) must equalTo(1)
      Await.result(Apps.add(new App("TEST3", "Description")), 2.seconds) must equalTo(1)
      Await.result(Apps.listAll, 2.seconds).length must equalTo(3)
    }
  }
}
