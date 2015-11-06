package auth.implementors;

import auth.interfaces.IAuthCache;
import auth.models.AuthenticatedUser;
import com.google.inject.Inject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

import utilities.FakeAppRunner;
import utilities.ITest;
import common.utilities.StringUtils;

import java.util.HashSet;

import static org.junit.Assert.*;
import static play.inject.Bindings.bind;

public class PlayAuthCacheTest {

    static Application application;

    @Before
    public void setUp() {
        application = new GuiceApplicationBuilder()
                .overrides(bind(IAuthCache.class).to(PlayAuthCache.class))
                .build();
    }

    @Test
    public void addUserToCache() {
        FakeAppRunner.runTestWithApplication(application, () -> {
            IAuthCache authCache = application.injector().instanceOf(PlayAuthCache.class);
            AuthenticatedUser user = new AuthenticatedUser(1, "test", "test@test");
            HashSet<String> sessionSet = new HashSet<String>();

            for(int i = 0; i < 1000; i++) {
                String session = authCache.storeUserInCache(user);
                assertTrue(StringUtils.isNotEmpty(session));
                assertTrue("Sessions should be unique", sessionSet.add(session));
            }
        });
    }

    @Test
    public void randomUserShouldNotBeInCache() {
        FakeAppRunner.runTestWithApplication(application, () -> {
            IAuthCache authCache = application.injector().instanceOf(PlayAuthCache.class);

            assertFalse(authCache.isUserInCache("randomSessionKey"));
        });
    }

    @Test
    public void userShouldNotBeInCache() {
        FakeAppRunner.runTestWithApplication(application, () -> {
            IAuthCache authCache = application.injector().instanceOf(PlayAuthCache.class);
            AuthenticatedUser user = new AuthenticatedUser(1, "test", "test@test");
            String session = authCache.storeUserInCache(user);

            assertTrue(StringUtils.isNotEmpty(session));
            assertTrue(authCache.isUserInCache(session));
        });
    }

    @Test
    public void randomUserShouldNotReturnFromCache() {
        FakeAppRunner.runTestWithApplication(application, () -> {
            IAuthCache authCache = application.injector().instanceOf(PlayAuthCache.class);

            assertNull(authCache.getUserFromCache("randomSessionKey"));
        });
    }

    @Test
    public void userShouldReturnFromCache() {
        FakeAppRunner.runTestWithApplication(application, () -> {
            IAuthCache authCache = application.injector().instanceOf(PlayAuthCache.class);
            AuthenticatedUser user = new AuthenticatedUser(1, "test", "test@test");
            String session = authCache.storeUserInCache(user);

            assertTrue(StringUtils.isNotEmpty(session));
            AuthenticatedUser cachedUser = authCache.getUserFromCache(session);
            assertNotNull(cachedUser);

            assertEquals(user.id(), cachedUser.id());
            assertEquals(user.name(), cachedUser.name());
            assertEquals(user.email(), cachedUser.email());
        });
    }

    @Test
    public void removeUserFromCache() {
        FakeAppRunner.runTestWithApplication(application, () -> {
            IAuthCache authCache = application.injector().instanceOf(PlayAuthCache.class);
            AuthenticatedUser user = new AuthenticatedUser(1, "test", "test@test");
            String session = authCache.storeUserInCache(user);

            assertTrue(StringUtils.isNotEmpty(session));
            assertTrue(authCache.removeUserFromCache(session));
            assertNull(authCache.getUserFromCache(session));
        });
    }

    @Test
    public void refreshRandomUserInCacheCache() {
        FakeAppRunner.runTestWithApplication(application, () -> {
            IAuthCache authCache = application.injector().instanceOf(PlayAuthCache.class);

            assertFalse(authCache.refreshUserAuthenticationSession("randomSessionKey"));
        });
    }

    @Test
    public void refreshUserInCacheCache() {
        FakeAppRunner.runTestWithApplication(application, () -> {
            IAuthCache authCache = application.injector().instanceOf(PlayAuthCache.class);
            AuthenticatedUser user = new AuthenticatedUser(1, "test", "test@test");
            String session = authCache.storeUserInCache(user);

            assertTrue(StringUtils.isNotEmpty(session));
            assertTrue(authCache.refreshUserAuthenticationSession(session));
        });
    }
}
