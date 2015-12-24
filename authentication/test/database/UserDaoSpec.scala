package database

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import java.sql.Timestamp
import java.util.Date
import play.api.test._
import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class UserDaoSpec extends Specification {

  def user = new User(
    0,
    "Test User",
    "test@test.com",
    Array.fill[Byte](5)(0),
    Array.fill[Byte](5)(0),
    "test_api_key",
    new Timestamp(new Date().getTime()),
    0,
    false)

  "UserDao" should {
    "Add user" in new WithApplication(FakeApp.fakeApp){
      Users.add(user).map(r => r must equalTo(1))
    }

    "Not add duplicates" in new WithApplication(FakeApp.fakeApp){
      Users.add(user).map(r => r must equalTo(1))
      Users.add(user).map(r => r must equalTo(0))
    }

    "Delete added user" in new WithApplication(FakeApp.fakeApp){
      Users.add(user).map(r => r must equalTo(1))
      Users.delete(1).map(r => r must equalTo(1))
    }

    "Not delete invalid user" in new WithApplication(FakeApp.fakeApp){
      Users.delete(1).map(r => r must equalTo(0))
    }

    "Update user" in new WithApplication(FakeApp.fakeApp){
      Users.add(user).map(r => r must equalTo(1))
      Users.updateUser(1, user).map(r => r must equalTo(1))
    }

    "Not update invalid user" in new WithApplication(FakeApp.fakeApp){
      Users.updateUser(1, null).map(r => r must equalTo(0))
    }

    "Block user" in new WithApplication(FakeApp.fakeApp){
      Users.add(user).map(r => r must equalTo(1))
      Users.setBlock(1, true).map(r => r must equalTo(1))
    }

    "Not block invalid user" in new WithApplication(FakeApp.fakeApp){
      Users.setBlock(1, true).map(r => r must equalTo(0))
    }

    "Get by id" in new WithApplication(FakeApp.fakeApp){
      Users.add(user).map(r => r must equalTo(1))
      Users.get(1).map(r => r must equalTo(Some))
    }

    "Get by id invalid" in new WithApplication(FakeApp.fakeApp){
      Users.get(1).map(r => r must equalTo(None))
    }

    "Get by email" in new WithApplication(FakeApp.fakeApp){
      Users.add(user).map(r => r must equalTo(1))
      Users.getFromEmail(user.Email).map(r => r must equalTo(Some))
    }

    "Get by email invalid" in new WithApplication(FakeApp.fakeApp){
      Users.getFromEmail(user.Email).map(r => r must equalTo(None))
    }

    "Get by apikey" in new WithApplication(FakeApp.fakeApp){
      Users.add(user).map(r => r must equalTo(1))
      Users.getFromApiKey(user.ApiKey).map(r => r must equalTo(Some))
    }

    "Get by apikey invalid" in new WithApplication(FakeApp.fakeApp){
      Users.getFromApiKey(user.ApiKey).map(r => r must equalTo(None))
    }

    "List all apps" in new WithApplication(FakeApp.fakeApp){
      val timeStamp = new Timestamp(new Date().getTime())
      val bytes = Array.fill[Byte](5)(0)
      val user1 = new User(0, "Test User", "test1@test.com", bytes, bytes, "test_api_key_1", timeStamp, 0, false)
      val user2 = new User(0, "Test User", "test2@test.com", bytes, bytes, "test_api_key_2", timeStamp, 0, false)
      val user3 = new User(0, "Test User", "test3@test.com", bytes, bytes, "test_api_key_3", timeStamp, 0, false)

      Users.add(user1).map(r => r must equalTo(1))
      Users.add(user2).map(r => r must equalTo(1))
      Users.add(user3).map(r => r must equalTo(1))
      Users.listAll.map(r => r.length must equalTo(3))
    }
  }
}
