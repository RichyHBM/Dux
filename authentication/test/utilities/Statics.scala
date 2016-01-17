package utilities

import play.api.test.FakeHeaders
import play.api.test.Helpers._
import play.mvc.Http.MimeTypes

object Statics {
  val jsonHeaders = FakeHeaders(Seq((CONTENT_TYPE, MimeTypes.JSON)))
}
