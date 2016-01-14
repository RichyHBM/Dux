package utilities

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import scala.collection.mutable.ListBuffer

@RunWith(classOf[JUnitRunner])
class PasswordsSpec extends Specification {

  "PasswordsSpec" should {

    "getSalt should return unique salt" in {
      var salt: ListBuffer[Array[Byte]] = new ListBuffer[Array[Byte]]

      var i = 0
      for(i <- 1 to 1000) {
        val s = Passwords.getNextSalt
        salt.contains(s) must equalTo(false)
        salt += s
      }

      salt.length must equalTo(1000)
    }

    "should hash passwords" in {
      val salt = Passwords.getNextSalt
      val password = Passwords.hash("password", salt)
      Passwords.hash("password", salt) must equalTo(password)
    }

    "hash should not be same for different passwords" in {
      val salt = Passwords.getNextSalt
      val password = Passwords.hash("password", salt)
      Passwords.hash("password12", salt) must not equalTo password
    }

    "hash should not be same for different salts" in {
      val salt = Passwords.getNextSalt
      val password = Passwords.hash("password", salt)
      Passwords.hash("password", Passwords.getNextSalt) must not equalTo password
    }

    "should correctly identify expected passwords" in {
      val salt = Passwords.getNextSalt
      val password = Passwords.hash("password", salt)
      Passwords.isExpectedPassword("password", salt, password) must equalTo(true)
    }

    "should not match different passwords" in {
      val salt = Passwords.getNextSalt
      val password = Passwords.hash("password", salt)
      Passwords.isExpectedPassword("password12", salt, password) must equalTo(false)
    }

    "should not match different salts" in {
      val salt = Passwords.getNextSalt
      val password = Passwords.hash("password", salt)
      Passwords.isExpectedPassword("password", Passwords.getNextSalt, password) must equalTo(false)
    }

    "should generate random passwords" in {
      var passwords: ListBuffer[String] = new ListBuffer[String]

      var i = 0
      for(i <- 1 to 1000) {
        val s = Passwords.generateRandomPassword(36)
        passwords.contains(s) must equalTo(false)
        passwords += s
      }

      passwords.length must equalTo(1000)
    }
  }
}

