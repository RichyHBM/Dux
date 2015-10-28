package auth.implementors;

import auth.interfaces.IAuthCache;
import auth.models.AuthenticatedUser;
import com.google.inject.Inject;
import play.cache.CacheApi;
import play.libs.Json;

import java.util.UUID;

public class PlayAuthCache implements IAuthCache {

    static int expiryTimeAmount = 60 * 60 * 24 * 7;

    CacheApi cache;

    @Inject
    public PlayAuthCache(CacheApi playCache) {
        this.cache = playCache;
    }

    @Override
    public boolean isUserInCache(String session) {
        return cache.get(session) != null;
    }

    @Override
    public String storeUserInCache(AuthenticatedUser authenticatedUser) {
        String session = UUID.randomUUID().toString();
        cache.set(session, authenticatedUser.toJson(), expiryTimeAmount);
        return session;
    }

    @Override
    public AuthenticatedUser getUserFromCache(String session) {
        String json = cache.get(session);

        if(json == null)
            return null;

        return AuthenticatedUser.fromJson(json);
    }

    @Override
    public boolean removeUserFromCache(String session) {
        cache.remove(session);
        return true;
    }

    @Override
    public boolean refreshUserAuthenticationSession(String session) {
        String json = cache.get(session);

        if(json == null)
            return false;

        cache.set(session, json, expiryTimeAmount);
        return true;
    }
}