package database

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import java.sql.Timestamp
import java.util.Date
import play.api.test._
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import utilities._

@RunWith(classOf[JUnitRunner])
class UserDaoSpec extends Specification {

  def user = new User(
    1,
    "Test User",
    "test@test.com",
    Array.fill[Byte](5)(0),
    Array.fill[Byte](5)(0),
    "test_api_key",
    new Timestamp(new Date().getTime()),
    0,
    false)

  "UserDao" should {
    "Add user" in Statics.WithFreshDatabase {
      Await.result(Users.add(user), 2.seconds) must equalTo(1)
    }

    "Not add duplicates" in Statics.WithFreshDatabase {
      Await.result(Users.add(user), 2.seconds) must equalTo(1)
      try {
        Await.result(Users.add(user), 2.seconds) must equalTo(0)
        false must equalTo(true)
      } catch {
        case _: Exception => true must equalTo(true)
      }
    }

    "Delete added user" in Statics.WithFreshDatabase {
      Await.result(Users.add(user), 2.seconds) must equalTo(1)
      Await.result(Users.delete(1), 2.seconds) must equalTo(1)
    }

    "Not delete invalid user" in Statics.WithFreshDatabase {
      Await.result(Users.delete(1), 2.seconds) must equalTo(0)
    }

    "Update user" in Statics.WithFreshDatabase {
      Await.result(Users.add(user), 2.seconds) must equalTo(1)
      Await.result(Users.updateUser(1, user), 2.seconds) must equalTo(1)
    }

    "Not update invalid user" in Statics.WithFreshDatabase {
      try {
        Await.result(Users.updateUser(1, null), 2.seconds) must equalTo(0)
        false must equalTo(true)
      } catch {
        case _: Exception => true must equalTo(true)
      }
    }

    "Block user" in Statics.WithFreshDatabase {
      Await.result(Users.add(user), 2.seconds) must equalTo(1)
      Await.result(Users.setBlock(1, true), 2.seconds) must equalTo(1)
      val r = Await.result(Users.get(1), 2.seconds)
      r match {
        case Some(u) => u.Blocked must equalTo(true)
        case None => 0 must equalTo(1)
      }
    }

    "Not block invalid user" in Statics.WithFreshDatabase {
      Await.result(Users.setBlock(1, true), 2.seconds) must equalTo(0)
    }

    "Get by id" in Statics.WithFreshDatabase {
      Await.result(Users.add(user), 2.seconds) must equalTo(1)
      Await.result(Users.get(1), 2.seconds) match {
        case Some(u) => u.Email must equalTo(user.Email)
        case None => 0 must equalTo(1)
      }
    }

    "Get by id invalid" in Statics.WithFreshDatabase {
      Await.result(Users.get(1), 2.seconds) must equalTo(None)
    }

    "Get by email" in Statics.WithFreshDatabase {
      Await.result(Users.add(user), 2.seconds) must equalTo(1)
      Await.result(Users.getFromEmail(user.Email), 2.seconds) match {
        case Some(u) => u.Email must equalTo(user.Email)
        case None => 0 must equalTo(1)
      }
    }

    "Get by email invalid" in Statics.WithFreshDatabase {
      Await.result(Users.getFromEmail(user.Email), 2.seconds) must equalTo(None)
    }

    "Get by apikey" in Statics.WithFreshDatabase {
      Await.result(Users.add(user), 2.seconds) must equalTo(1)
      Await.result(Users.getFromApiKey(user.ApiKey), 2.seconds) match {
        case Some(u) => u.Email must equalTo(user.Email)
        case None => 0 must equalTo(1)
      }
    }

    "Get by apikey invalid" in Statics.WithFreshDatabase {
      Await.result(Users.getFromApiKey(user.ApiKey), 2.seconds) must equalTo(None)
    }

    "Get by list" in Statics.WithFreshDatabase {
      val bytes = Array.fill[Byte](5)(0)
      val user1 = new User("Test User", "test1@test.com", bytes, bytes, "test_api_key_1")
      val user2 = new User("Test User", "test2@test.com", bytes, bytes, "test_api_key_2")
      val user3 = new User("Test User", "test3@test.com", bytes, bytes, "test_api_key_3")

      Await.result(Users.add(user1), 2.seconds) must equalTo(1)
      Await.result(Users.add(user2), 2.seconds) must equalTo(1)
      Await.result(Users.add(user3), 2.seconds) must equalTo(1)

      val r = Await.result(Users.get( List[Long](2, 3) ), 2.seconds)
      r.length must equalTo(2)
      r(0).Email must equalTo("test2@test.com")
      r(1).Email must equalTo("test3@test.com")
    }

    "List all apps" in Statics.WithFreshDatabase {
      val bytes = Array.fill[Byte](5)(0)
      val user1 = new User("Test User", "test1@test.com", bytes, bytes, "test_api_key_1")
      val user2 = new User("Test User", "test2@test.com", bytes, bytes, "test_api_key_2")
      val user3 = new User("Test User", "test3@test.com", bytes, bytes, "test_api_key_3")

      Await.result(Users.add(user1), 2.seconds) must equalTo(1)
      Await.result(Users.add(user2), 2.seconds) must equalTo(1)
      Await.result(Users.add(user3), 2.seconds) must equalTo(1)
      Await.result(Users.listAll, 2.seconds).length must equalTo(3)
    }
  }
}
