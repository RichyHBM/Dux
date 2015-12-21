package interfaces

import models.UserSession

trait IAuthenticationCache {
  def getAllLoggedIn() : List[(String, UserSession)]

  def createSession(session: UserSession): String

  def getSession(sessionKey: String) : Option[(String, UserSession)]
  def getSessionFromEmail(email: String): Option[(String, UserSession)]

  def removeSession(sessionKey: String)
  def removeSessionWithEmail(email: String)

  def createToken(email: String): String
  def getEmailFromToken(token: String): Option[String]
  def removeEmailFromToken(token: String)
}
