package auth.interfaces;

import auth.models.AuthenticatedUser;
import auth.implementors.PlayAuthCache;
import com.google.inject.ImplementedBy;

@ImplementedBy(PlayAuthCache.class)
public interface IAuthCache {
    boolean isUserInCache(String session);
    String storeUserInCache(AuthenticatedUser authenticatedUser);
    AuthenticatedUser getUserFromCache(String session);
    boolean removeUserFromCache(String session);
    boolean refreshUserAuthenticationSession(String session);
}
