import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Guice;
import com.google.inject.Injector;
import interfaces.ILeaderboardProvider;
import models.LeaderboardUser;
import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;
import play.twirl.api.Content;
import implementors.RedisLeaderboardProvider;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import utilities.FakeAppRunner;
import utilities.ITest;
import javax.inject.Inject;

import static play.test.Helpers.*;
import static org.junit.Assert.*;


public class RedisLeaderboardProviderTest {

    final int GAME_ID = 123;
    final String LB_ID = "LB1";
    final String REDIS_KEY = RedisLeaderboardProvider.redisKey(GAME_ID, LB_ID);
    final String EXTRA_KEY = RedisLeaderboardProvider.redisKeyExtra(GAME_ID, LB_ID);

    static ILeaderboardProvider provider;
    static JedisPool jedisPool;

    //Redis is required in order to run the tests
    @BeforeClass
    public static void oneTimeSetUp() throws IOException, InterruptedException {
        jedisPool = new JedisPool("localhost");
        provider = new RedisLeaderboardProvider(jedisPool);
    }

    @Before
    public void setUp() {
        Jedis j = jedisPool.getResource();
        try {
            j.flushDB();

            for(int i = 0; i < 10; i++) {
                j.zadd(REDIS_KEY, i, "u" + i);
                j.hset(EXTRA_KEY, "u" + i, "data" + i);
            }
        } finally {
            jedisPool.returnResource(j);
        }
    }

    @Test
    public void getUserCountInLeaderboardTest(){
        assertEquals(10, provider.getUserCountInLeaderboard(GAME_ID, LB_ID));

        Jedis j = jedisPool.getResource();
        try {
            j.flushDB();
        } finally {
            jedisPool.returnResource(j);
        }

        assertEquals(0, provider.getUserCountInLeaderboard(GAME_ID, LB_ID));

        assertEquals(0, provider.getUserCountInLeaderboard(123, "lb"));
    }

    @Test
    public void getRankedUsersAscendingTest() {
        List<LeaderboardUser> invalidUsers = provider.getRankedUsers(GAME_ID, LB_ID, false, 100, 10);
        assertEquals(0, invalidUsers.size());

        List<LeaderboardUser> endUsers = provider.getRankedUsers(GAME_ID, LB_ID, false, -5, 2);
        assertEquals(2, endUsers.size());
        for(int i = 5; i < 6; i++) {
            LeaderboardUser user = endUsers.get(i - 5);
            assertNotNull(user);
            assertEquals("u" + i, user.userId());
            assertEquals(i - 10, user.rank());
            assertEquals(i, user.score(), 0.0001);
            assertEquals("data" + i, user.extra());
        }

        List<LeaderboardUser> smallUsers = provider.getRankedUsers(GAME_ID, LB_ID, false, 0, 2);
        for(int i = 0; i < 2; i++) {
            LeaderboardUser user = smallUsers.get(i);
            assertNotNull(user);
            assertEquals("u" + i, user.userId());
            assertEquals(i, user.rank());
            assertEquals(i, user.score(), 0.0001);
            assertEquals("data" + i, user.extra());
        }
        assertEquals(2, smallUsers.size());

        List<LeaderboardUser> validUsers = provider.getRankedUsers(GAME_ID, LB_ID, false, 0, 10);
        for(int i = 0; i < 10; i++) {
            LeaderboardUser user = validUsers.get(i);
            assertNotNull(user);
            assertEquals("u" + i, user.userId());
            assertEquals(i, user.rank());
            assertEquals(i, user.score(), 0.0001);
            assertEquals("data" + i, user.extra());
        }
        assertEquals(10, validUsers.size());

        List<LeaderboardUser> moreThanValidUsers = provider.getRankedUsers(GAME_ID, LB_ID, false, 0, 15);
        for(int i = 0; i < 10; i++) {
            LeaderboardUser user = moreThanValidUsers.get(i);
            assertNotNull(user);
            assertEquals("u" + i, user.userId());
            assertEquals(i, user.rank());
            assertEquals(i, user.score(), 0.0001);
            assertEquals("data" + i, user.extra());
        }
        assertEquals(10, moreThanValidUsers.size());
    }

    @Test
    public void getRankedUsersDescendingTest() {
        List<LeaderboardUser> invalidUsers = provider.getRankedUsers(GAME_ID, LB_ID, true, 100, 10);
        assertEquals(0, invalidUsers.size());

        List<LeaderboardUser> endUsers = provider.getRankedUsers(GAME_ID, LB_ID, true, -5, 2);
        assertEquals(2, endUsers.size());
        for(int i = 5; i < 6; i++) {
            LeaderboardUser user = endUsers.get(i - 5);
            assertNotNull(user);
            assertEquals("u" + (9 - i), user.userId());
            assertEquals(i - 10, user.rank());
            assertEquals(9 - i, user.score(), 0.0001);
            assertEquals("data" + (9 - i), user.extra());
        }

        List<LeaderboardUser> smallUsers = provider.getRankedUsers(GAME_ID, LB_ID, true, 0, 2);
        for(int i = 0; i < 2; i++) {
            LeaderboardUser user = smallUsers.get(i);
            assertNotNull(user);
            assertEquals("u" + (9 - i), user.userId());
            assertEquals(i, user.rank());
            assertEquals(9 - i, user.score(), 0.0001);
            assertEquals("data" + (9 - i), user.extra());
        }
        assertEquals(2, smallUsers.size());

        List<LeaderboardUser> validUsers = provider.getRankedUsers(GAME_ID, LB_ID, true, 0, 10);
        for(int i = 0; i < 10; i++) {
            LeaderboardUser user = validUsers.get(i);
            assertNotNull(user);
            assertEquals("u" + (9 - i), user.userId());
            assertEquals(i, user.rank());
            assertEquals((9 - i), user.score(), 0.0001);
            assertEquals("data" + (9 - i), user.extra());
        }
        assertEquals(10, validUsers.size());

        List<LeaderboardUser> moreThanValidUsers = provider.getRankedUsers(GAME_ID, LB_ID, true, 0, 15);
        for(int i = 0; i < 10; i++) {
            LeaderboardUser user = moreThanValidUsers.get(i);
            assertNotNull(user);
            assertEquals("u" + (9 - i), user.userId());
            assertEquals(i, user.rank());
            assertEquals(9 - i, user.score(), 0.0001);
            assertEquals("data" + (9 - i), user.extra());
        }
        assertEquals(10, moreThanValidUsers.size());
    }

    @Test
    public void getUserTest() {
        LeaderboardUser invalidUser1 = provider.getUser(GAME_ID, LB_ID, false, "123");
        assertNull(invalidUser1);
        //Ascending
        for(int i = 0; i < 10; i++) {
            LeaderboardUser user = provider.getUser(GAME_ID, LB_ID, false, "u" + i);
            assertNotNull(user);
            assertEquals("u" + i, user.userId());
            assertEquals(i, user.rank());
            assertEquals(i, user.score(), 0.0001);
            assertEquals("data" + i, user.extra());
        }
        //Descending
        for(int i = 0; i < 10; i++) {
            LeaderboardUser user = provider.getUser(GAME_ID, LB_ID, true, "u" + i);
            assertNotNull(user);
            assertEquals("u" + i, user.userId());
            assertEquals(9 - i, user.rank());
            assertEquals(i, user.score(), 0.0001);
            assertEquals("data" + i, user.extra());
        }
    }

    @Test
    public void getRankForUserTest() {
        //Ascending
        long lastRankAsc = provider.getRankForUser(GAME_ID, LB_ID, false, "123");
        assertEquals(10, lastRankAsc);

        for(int i = 0; i < 10; i++) {
            long rank = provider.getRankForUser(GAME_ID, LB_ID, false, "u" + i);
            assertEquals(i, rank);
        }

        for(int i = 10; i < 15; i++) {
            long rank = provider.getRankForUser(GAME_ID, LB_ID, false, "u" + i);
            assertEquals(10, rank);
        }
        //Descending
        long lastRankDesc = provider.getRankForUser(GAME_ID, LB_ID, true, "123");
        assertEquals(10, lastRankDesc);

        for(int i = 0; i < 10; i++) {
            long rank = provider.getRankForUser(GAME_ID, LB_ID, true, "u" + i);
            assertEquals(9 - i, rank);
        }

        for(int i = 10; i < 15; i++) {
            long rank = provider.getRankForUser(GAME_ID, LB_ID, true, "u" + i);
            assertEquals(10, rank);
        }
    }

    @Test
    public void getScoreForUserTest() {
        double noScore = provider.getScoreForUser(GAME_ID, LB_ID, "123");
        assertEquals(0, noScore, 0.0001);

        for(int i = 0; i < 10; i++) {
            double score = provider.getScoreForUser(GAME_ID, LB_ID, "u" + i);
            assertEquals(i, score, 0.0001);
        }

        for(int i = 10; i < 15; i++) {
            double score = provider.getScoreForUser(GAME_ID, LB_ID, "u" + i);
            assertEquals(0, noScore, 0.0001);
        }
    }

    @Test
    public void setScoreForUserTest() {
        assertTrue( provider.setScoreForUser(GAME_ID, LB_ID, "123", 5, "") );

        Jedis j = jedisPool.getResource();
        try {
            assertEquals(5, j.zscore(REDIS_KEY, "123"), 0.0001);
            assertTrue(provider.setScoreForUser(GAME_ID, LB_ID, "123", 10, ""));
            assertEquals(10, j.zscore(REDIS_KEY, "123"), 0.0001);
        } finally {
            jedisPool.returnResource(j);
        }
    }

    @Test
    public void incrementScoreForUserTest() {
        assertTrue( provider.incrementScoreForUser(GAME_ID, LB_ID, "123", 5, "") );

        Jedis j = jedisPool.getResource();
        try {
            assertEquals(5, j.zscore(REDIS_KEY, "123"), 0.0001);
            assertTrue(provider.incrementScoreForUser(GAME_ID, LB_ID, "123", 10, ""));
            assertEquals(15, j.zscore(REDIS_KEY, "123"), 0.0001);
        } finally {
            jedisPool.returnResource(j);
        }
    }

    @Test
    public void removeUserTest() {
        assertFalse( provider.removeUser(GAME_ID, LB_ID, "123") );

        for(int i = 0; i < 5; i++)
        {
            assertTrue(provider.removeUser(GAME_ID, LB_ID, "u" + i));
        }

        Jedis j = jedisPool.getResource();
        try {
            long count = j.zcard(REDIS_KEY);
            assertEquals(5, count);
        } finally {
            jedisPool.returnResource(j);
        }
    }

    @Test
    public void deleteLeaderboardTest() {
        assertTrue( provider.deleteLeaderboard(10, "lb") );
        assertTrue( provider.deleteLeaderboard(GAME_ID, LB_ID));

        Jedis j = jedisPool.getResource();
        try {
            long count = j.zcard(REDIS_KEY);
            assertEquals(0 , count);
        } finally {
            jedisPool.returnResource(j);
        }
    }
}
