package auth.models

import play.api.mvc._

case class AuthenticatedRequest[A](
  val sessionKey: String,
  val user: AuthenticatedUser,
  request: Request[A]
) extends WrappedRequest(request)
