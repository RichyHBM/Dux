package auth.models

case class SessionUser(
  val sessionKey: String,
  val user: AuthenticatedUser)
