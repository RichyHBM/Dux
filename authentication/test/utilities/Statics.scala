package utilities

import java.io.File

import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterEach
import play.api.Play
import play.api.db.evolutions.{OfflineEvolutions, Evolutions}
import play.api.test.{FakeApplication, FakeHeaders}
import play.api.test.Helpers._
import play.api.db._
import play.mvc.Http.MimeTypes

object Statics {


  def WithFreshDatabase[T](block: => T) = {
    val dbApi = Play.current.injector.instanceOf[DBApi]
    Evolutions.cleanupEvolutions(dbApi.database("default"))
    Evolutions.applyEvolutions(dbApi.database("default"))
    block
  }

  val jsonHeaders = FakeHeaders(Seq((CONTENT_TYPE, MimeTypes.JSON)))

  val conf = Map(
    "slick.dbs.default.driver" -> "slick.driver.H2Driver$",
    "slick.dbs.default.db.driver" -> "org.h2.Driver",
    "slick.dbs.default.db.url" -> "jdbc:h2:mem:test;MODE=PostgreSQL;DATABASE_TO_UPPER=FALSE"
  )

  val fakeApp: FakeApplication = new FakeApplication(additionalConfiguration = conf)

  Play.start(fakeApp)

}
