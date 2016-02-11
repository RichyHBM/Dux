package auth.models

import play.api.mvc._

case class AuthenticatedRequest[A](sessionKey: String,
                                   user: AuthenticatedUser,
                                   request: Request[A]
) extends WrappedRequest(request)
