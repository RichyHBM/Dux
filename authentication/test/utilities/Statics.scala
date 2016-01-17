package utilities

import play.api.test.{FakeApplication, FakeHeaders}
import play.api.test.Helpers._
import play.mvc.Http.MimeTypes

object Statics {
  val jsonHeaders = FakeHeaders(Seq((CONTENT_TYPE, MimeTypes.JSON)))

  val conf = Map(
    "slick.dbs.default.driver" -> "slick.driver.H2Driver$",
    "slick.dbs.default.db.driver" -> "org.h2.Driver",
    "slick.dbs.default.db.url" -> "jdbc:h2:mem:test;MODE=PostgreSQL;DATABASE_TO_UPPER=FALSE"
  )

  val fakeApp: FakeApplication = new FakeApplication(additionalConfiguration = conf)

}
