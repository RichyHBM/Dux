package auth.interfaces

import auth.models.AuthenticatedUser
import auth.implementors.PlayAuthCache
import com.google.inject.ImplementedBy

@ImplementedBy(classOf[PlayAuthCache])
trait IAuthCache {
    def isUserInCache(session: String): Boolean
    def storeUserInCache(authenticatedUser: AuthenticatedUser): String
    def getUserFromCache(session: String): AuthenticatedUser
    def removeUserFromCache(session: String): Boolean
    def refreshUserAuthenticationSession(session: String): Boolean
}
