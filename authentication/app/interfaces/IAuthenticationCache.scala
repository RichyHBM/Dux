package interfaces

import models.UserSession

trait IAuthenticationCache {
  def getAllLoggedIn() : List[(String, UserSession)]

  def createSession(session: UserSession): String

  def getSession(sessionKey: String) : (String, UserSession)
  def getSessionFromEmail(email: String): (String, UserSession)

  def removeSession(sessionKey: String)
  def removeSessionWithEmail(email: String)

  def createToken(email: String): String
  def getEmailFromToken(token: String): String
}
