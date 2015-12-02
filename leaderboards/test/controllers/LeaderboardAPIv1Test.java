package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.LeaderboardModify;
import database.LeaderboardRead;
import database.Structure;
import implementors.RedisLeaderboardProvider;
import models.Leaderboard;
import play.libs.Json;
import utilities.WebRequest;
import database.Structure;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.db.DB;
import play.Logger;
import play.test.TestBrowser;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LeaderboardAPIv1Test {

    String url = "http://127.0.0.1:3333";
    static JedisPool jedisPool;

    @BeforeClass
    public static void oneTimeSetUp() {
        jedisPool = new JedisPool("localhost");
    }

    @Before
    public void setUp() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            try (Connection conn = DB.getConnection()) {
                PreparedStatement stm = conn.prepareStatement(String.format("DELETE FROM %s", Structure.Leaderboards.Name));
                stm.executeUpdate();
            }
        });

        Jedis j = jedisPool.getResource();
        try {
            j.flushDB();
        } finally {
            jedisPool.returnResource(j);
        }
    }

    @Test
    public void getUserCountInLeaderboardNonExistentTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            ObjectNode request = Json.newObject();
            request.put("gameId", 123);
            request.put("leaderboardName", "lbTest");

            assertEquals(0, Json.parse(WebRequest.jsonPost(url + "/api/v1/get-leaderboard-count", request.toString())).asInt());
        });
    }

    @Test
    public void getUserCountInLeaderboardTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            ObjectNode request = Json.newObject();
            request.put("gameId", 123);
            request.put("leaderboardName", "lbTest");

            LeaderboardModify.createLeaderboard(123, "lbTest", null, null, false);

            Jedis j = jedisPool.getResource();
            try {
                for (int i = 0; i < 10; i++) {
                    j.zadd(RedisLeaderboardProvider.redisKey(123, "lbTest"), i, "u" + i);
                    j.hset(RedisLeaderboardProvider.redisKeyExtra(123, "lbTest"), "u" + i, "data" + i);
                }

            } finally {
                jedisPool.returnResource(j);
            }

            assertEquals(10, Json.parse(WebRequest.jsonPost(url + "/api/v1/get-leaderboard-count", request.toString())).asInt());
        });
    }

    @Test
    public void setScoreForUserNonActiveLeaderboardTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            LeaderboardModify.createLeaderboard(123, "lbTest", null, null, false);
            Jedis j = jedisPool.getResource();
            try {
                j.zadd(RedisLeaderboardProvider.redisKey(123, "lbTest"), 100, "userTest");
                j.hset(RedisLeaderboardProvider.redisKeyExtra(123, "lbTest"), "userTest", "userData");

                ObjectNode request = Json.newObject();
                request.put("gameId", 123);
                request.put("leaderboardName", "lbTest");
                request.put("userId", "userTest");
                request.put("newUserScore", 500.0);
                request.put("userExtraData", "userDataNew");

                try {
                    Logger.debug("Expected exception");
                    WebRequest.jsonPost(url + "/api/v1/set-score", request.toString());
                    assertTrue("Non active leaderboard should throw exception", false);
                }catch(Exception e) {
                    assertTrue(true);
                }

            } finally {
                jedisPool.returnResource(j);
            }
        });
    }

    @Test
    public void setScoreForUserTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            LeaderboardModify.createLeaderboard(
                    123,
                    "lbTest",
                    new Date( new Date().getTime() - 50000 ),
                    new Date( new Date().getTime() + 50000 ),
                    false);

            Jedis j = jedisPool.getResource();
            try {
                j.zadd(RedisLeaderboardProvider.redisKey(123, "lbTest"), 100, "userTest");
                j.hset(RedisLeaderboardProvider.redisKeyExtra(123, "lbTest"), "userTest", "userData");

                ObjectNode request = Json.newObject();
                request.put("gameId", 123);
                request.put("leaderboardName", "lbTest");
                request.put("userId", "userTest");
                request.put("newUserScore", 500.0);
                request.put("userExtraData", "userDataNew");

                assertTrue(Json.parse(WebRequest.jsonPost(url + "/api/v1/set-score", request.toString())).asBoolean());

                assertEquals(500.0, j.zscore(RedisLeaderboardProvider.redisKey(123, "lbTest"), "userTest"), 0.01);
                assertEquals("userDataNew", j.hget(RedisLeaderboardProvider.redisKeyExtra(123, "lbTest"), "userTest"));

            } finally {
                jedisPool.returnResource(j);
            }
        });
    }

    @Test
    public void incrementScoreForUserNonActiveLeaderboardTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            LeaderboardModify.createLeaderboard(123, "lbTest", null, null, false);
            Jedis j = jedisPool.getResource();
            try {
                j.zadd(RedisLeaderboardProvider.redisKey(123, "lbTest"), 100, "userTest");
                j.hset(RedisLeaderboardProvider.redisKeyExtra(123, "lbTest"), "userTest", "userData");

                ObjectNode request = Json.newObject();
                request.put("gameId", 123);
                request.put("leaderboardName", "lbTest");
                request.put("userId", "userTest");
                request.put("increment", 500.0);
                request.put("userExtraData", "userDataNew");

                try {
                    Logger.debug("Expected exception");
                    WebRequest.jsonPost(url + "/api/v1/increment-score", request.toString());
                    assertTrue("Non active leaderboard should throw exception", false);
                }catch(Exception e) {
                    assertTrue(true);
                }

            } finally {
                jedisPool.returnResource(j);
            }
        });
    }

    @Test
    public void incrementScoreForUserTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            LeaderboardModify.createLeaderboard(
                    123,
                    "lbTest",
                    new Date( new Date().getTime() - 50000 ),
                    new Date( new Date().getTime() + 50000 ),
                    false);

            Jedis j = jedisPool.getResource();
            try {
                j.zadd(RedisLeaderboardProvider.redisKey(123, "lbTest"), 100, "userTest");
                j.hset(RedisLeaderboardProvider.redisKeyExtra(123, "lbTest"), "userTest", "userData");

                ObjectNode request = Json.newObject();
                request.put("gameId", 123);
                request.put("leaderboardName", "lbTest");
                request.put("userId", "userTest");
                request.put("increment", 500.0);
                request.put("userExtraData", "userDataNew");

                assertTrue(Json.parse(WebRequest.jsonPost(url + "/api/v1/increment-score", request.toString())).asBoolean());

                assertEquals(600.0, j.zscore(RedisLeaderboardProvider.redisKey(123, "lbTest"), "userTest"), 0.01);
                assertEquals("userDataNew", j.hget(RedisLeaderboardProvider.redisKeyExtra(123, "lbTest"), "userTest"));


            } finally {
                jedisPool.returnResource(j);
            }
        });
    }

    @Test
    public void getRankedUsersTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            LeaderboardModify.createLeaderboard(123, "lbTest", null, null, false);
            Jedis j = jedisPool.getResource();
            try {
                for (int i = 0; i < 10; i++) {
                    j.zadd(RedisLeaderboardProvider.redisKey(123, "lbTest"), i, "u" + i);
                    j.hset(RedisLeaderboardProvider.redisKeyExtra(123, "lbTest"), "u" + i, "data" + i);
                }

            } finally {
                jedisPool.returnResource(j);
            }

            ObjectNode request = Json.newObject();
            request.put("gameId", 123);
            request.put("leaderboardName", "lbTest");
            request.put("startRank", 2);
            request.put("count", 5);

            JsonNode response = Json.parse(WebRequest.jsonPost(url + "/api/v1/get-ranked-users", request.toString()));
            assertEquals(5, response.size());

            for (int i = 0; i < 5; i++) {
                assertEquals("u"+(i+2), response.get(i).findValue("userId").asText(""));
                assertEquals(i + 2, response.get(i).findValue("rank").asInt(-1));
            }
        });
    }

    @Test
    public void getUserTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            LeaderboardModify.createLeaderboard(123, "lbTest", null, null, false);
            Jedis j = jedisPool.getResource();
            try {
                j.zadd(RedisLeaderboardProvider.redisKey(123, "lbTest"), 123, "u123");
                j.hset(RedisLeaderboardProvider.redisKeyExtra(123, "lbTest"), "u123", "dataU123");

            } finally {
                jedisPool.returnResource(j);
            }

            ObjectNode request = Json.newObject();
            request.put("gameId", 123);
            request.put("leaderboardName", "lbTest");
            request.put("userId", "u123");

            JsonNode response = Json.parse(WebRequest.jsonPost(url + "/api/v1/get-user", request.toString()));

            assertEquals(0, response.findValue("rank").asLong());
            assertEquals("u123", response.findValue("userId").asText());
            assertEquals(123, response.findValue("score").asDouble(), 0.01);
            assertEquals("dataU123", response.findValue("extra").asText());
        });
    }

    @Test
    public void getInvalidUserTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            LeaderboardModify.createLeaderboard(123, "lbTest", null, null, false);
            Jedis j = jedisPool.getResource();
            try {
                j.zadd(RedisLeaderboardProvider.redisKey(123, "lbTest"), 123, "u123");
                j.hset(RedisLeaderboardProvider.redisKeyExtra(123, "lbTest"), "u123", "dataU123");

            } finally {
                jedisPool.returnResource(j);
            }

            ObjectNode request = Json.newObject();
            request.put("gameId", 123);
            request.put("leaderboardName", "lbTest");
            request.put("userId", "u12345");

            try {
                Logger.debug("Expected exception");
                WebRequest.jsonPost(url + "/api/v1/get-user", request.toString());
                assertTrue("Invalid user should throw exception", false);
            }catch(Exception e) {
                assertTrue(true);
            }
        });
    }

    @Test
    public void getRankForUserTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            LeaderboardModify.createLeaderboard(123, "lbTest", null, null, false);
            Jedis j = jedisPool.getResource();
            try {
                j.zadd(RedisLeaderboardProvider.redisKey(123, "lbTest"), 123, "u123");
                j.hset(RedisLeaderboardProvider.redisKeyExtra(123, "lbTest"), "u123", "dataU123");

            } finally {
                jedisPool.returnResource(j);
            }

            ObjectNode request = Json.newObject();
            request.put("gameId", 123);
            request.put("leaderboardName", "lbTest");
            request.put("userId", "u123");

            JsonNode response = Json.parse(WebRequest.jsonPost(url + "/api/v1/get-user-rank", request.toString()));

            assertEquals(0, response.asLong());
        });
    }

    @Test
    public void getRankForInvalidUserTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            LeaderboardModify.createLeaderboard(123, "lbTest", null, null, false);
            Jedis j = jedisPool.getResource();
            try {
                j.zadd(RedisLeaderboardProvider.redisKey(123, "lbTest"), 123, "u123");
                j.hset(RedisLeaderboardProvider.redisKeyExtra(123, "lbTest"), "u123", "dataU123");

            } finally {
                jedisPool.returnResource(j);
            }

            ObjectNode request = Json.newObject();
            request.put("gameId", 123);
            request.put("leaderboardName", "lbTest");
            request.put("userId", "u12345");

            JsonNode response = Json.parse(WebRequest.jsonPost(url + "/api/v1/get-user-rank", request.toString()));

            assertEquals(1, response.asLong());
        });
    }

    @Test
    public void getScoreForUserTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            LeaderboardModify.createLeaderboard(123, "lbTest", null, null, false);
            Jedis j = jedisPool.getResource();
            try {
                j.zadd(RedisLeaderboardProvider.redisKey(123, "lbTest"), 123, "u123");
                j.hset(RedisLeaderboardProvider.redisKeyExtra(123, "lbTest"), "u123", "dataU123");

            } finally {
                jedisPool.returnResource(j);
            }

            ObjectNode request = Json.newObject();
            request.put("gameId", 123);
            request.put("leaderboardName", "lbTest");
            request.put("userId", "u123");

            JsonNode response = Json.parse(WebRequest.jsonPost(url + "/api/v1/get-user-score", request.toString()));

            assertEquals(123, response.asDouble(), 0.01);
        });
    }

    @Test
    public void getScoreForInvalidUserTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            LeaderboardModify.createLeaderboard(123, "lbTest", null, null, false);
            Jedis j = jedisPool.getResource();
            try {
                j.zadd(RedisLeaderboardProvider.redisKey(123, "lbTest"), 123, "u123");
                j.hset(RedisLeaderboardProvider.redisKeyExtra(123, "lbTest"), "u123", "dataU123");

            } finally {
                jedisPool.returnResource(j);
            }

            ObjectNode request = Json.newObject();
            request.put("gameId", 123);
            request.put("leaderboardName", "lbTest");
            request.put("userId", "u12345");

            JsonNode response = Json.parse(WebRequest.jsonPost(url + "/api/v1/get-user-score", request.toString()));

            assertEquals(0, response.asDouble(), 0.01);
        });
    }
}
