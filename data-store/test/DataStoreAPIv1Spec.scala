import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.mvc.MultipartFormData

import play.api.test._
import play.api.test.Helpers._

import scala.collection.immutable.HashMap

@RunWith(classOf[JUnitRunner])
class DataStoreAPIv1Spec extends Specification {

  "DataStoreAPIv1" should {
    "save data correctly" in new WithApplication{
      val response = route(FakeRequest(POST, "/v1/put?name=test&data=testData")).get
      status(response) must equalTo(OK)
    }

    "save partial data" in new WithApplication{
      val response = route(FakeRequest(POST, "/v1/put?name=test")).get
      status(response) must equalTo(OK)
    }

    "not save empty data" in new WithApplication{
      val response = route(FakeRequest(POST, "/v1/put")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "save and retreive data correctly" in new WithApplication{
      val response = route(FakeRequest(POST, "/v1/put?name=test&data=testData")).get
      val result = route(FakeRequest(GET, "/v1/get?name=test&data=testData")).get
      status(response) must equalTo(OK)
      status(result) must equalTo(OK)
      contentAsString(result) must contain ("testData")
    }

    "not get empty data" in new WithApplication{
      val response = route(FakeRequest(GET, "/v1/get")).get
      status(response) must equalTo(BAD_REQUEST)
    }

    "get random data" in new WithApplication{
      val response = route(FakeRequest(GET, "/v1/get?name=1234")).get
      status(response) must equalTo(OK)
    }
  }
}

