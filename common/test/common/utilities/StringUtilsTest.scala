package common.utilities

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.test.WithApplication

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class StringUtilsSpec extends Specification {
  "StringUtils" should {
    "pass isEmpty tests" in {
      StringUtils.isEmpty("") must equalTo(true)
      StringUtils.isEmpty(null) must equalTo(true)
      StringUtils.isEmpty(new String()) must equalTo(true)

      StringUtils.isEmpty(" ") must equalTo(false)
      StringUtils.isEmpty("String") must equalTo(false)
      StringUtils.isEmpty(Integer.toString(1)) must equalTo(false)
    }

    "pass isNotEmpty tests" in {
      StringUtils.isNotEmpty("") must equalTo(false)
      StringUtils.isNotEmpty(null) must equalTo(false)
      StringUtils.isNotEmpty(new String()) must equalTo(false)

      StringUtils.isNotEmpty(" ") must equalTo(true)
      StringUtils.isNotEmpty("String") must equalTo(true)
      StringUtils.isNotEmpty(Integer.toString(1)) must equalTo(true)
    }
  }
}
