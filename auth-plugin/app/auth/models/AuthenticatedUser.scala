package auth.models

import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.beans.BeanProperty

case class AuthenticatedUser(@BeanProperty var id: Long,
                             @BeanProperty var name: String,
                             @BeanProperty var email: String)