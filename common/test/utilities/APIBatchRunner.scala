package utilities

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._

/**
  * Runs all defined methods in a single Specs test to avoid API DB issue
  */

@RunWith(classOf[JUnitRunner])
abstract class APIBatchRunner[T](fakeApp: FakeApplication = FakeApplication()) extends Specification {
  sequential

  def getThis: T = this.asInstanceOf[T]

  getThis.getClass.getSimpleName should {
    "run all batched tests" in new WithApplication(fakeApp) {
      val methods = getThis.getClass.getDeclaredMethods

      println("[info] " + getThis.getClass.getSimpleName + " should")
      for(m <- methods) {
        m.invoke(getThis)
        println("[info]   + " + m.getName)
      }
      println("[info]")
    }
  }
}
