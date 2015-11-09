package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.LeaderboardModify;
import database.LeaderboardRead;
import database.Structure;
import implementors.RedisLeaderboardProvider;
import models.Leaderboard;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Logger;
import play.db.DB;
import play.libs.Json;
import play.test.TestBrowser;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import utilities.WebRequest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;


public class LeaderboardViewAPITest {

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
    public void getLeaderboardsForGameNoLeaderboardsTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            ObjectNode request = Json.newObject();
            request.put("gameId", 123);

            JsonNode response = Json.parse(WebRequest.jsonPost(url + "/view-api/get-leaderboards-for-game", request.toString()));
            assertEquals(0, response.size());
        });
    }

    @Test
    public void getLeaderboardsForGameOneLeaderboardTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            ObjectNode request = Json.newObject();
            request.put("gameId", 123);

            LeaderboardModify.createLeaderboard(123, "lbTest", null, null, false);

            JsonNode response = Json.parse(WebRequest.jsonPost(url + "/view-api/get-leaderboards-for-game", request.toString()));
            List<JsonNode> leaderboards = new ArrayList<JsonNode>();

            for(int i = 0; i < response.size(); i++) {
                leaderboards.add(response.get(i));
            }
            assertEquals(1, leaderboards.size());
            assertEquals(123, leaderboards.get(0).findValue("gameId").asInt(-1));
            assertEquals("lbTest", leaderboards.get(0).findValue("leaderboardName").asText(""));
        });
    }

    @Test
    public void getLeaderboardsForGameTwoLeaderboardTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            ObjectNode request = Json.newObject();
            request.put("gameId", 123);

            LeaderboardModify.createLeaderboard(123, "lbTest", null, null, false);
            LeaderboardModify.createLeaderboard(123, "lbTest2", null, null, false);

            JsonNode response = Json.parse(WebRequest.jsonPost(url + "/view-api/get-leaderboards-for-game", request.toString()));
            List<JsonNode> leaderboards = new ArrayList<JsonNode>();

            for(int i = 0; i < response.size(); i++) {
                leaderboards.add(response.get(i));
            }
            assertEquals(2, leaderboards.size());
            assertEquals(123, leaderboards.get(0).findValue("gameId").asInt(-1));
            assertEquals("lbTest", leaderboards.get(0).findValue("leaderboardName").asText(""));

            assertEquals(123, leaderboards.get(1).findValue("gameId").asInt(-1));
            assertEquals("lbTest2", leaderboards.get(1).findValue("leaderboardName").asText(""));
        });
    }

    @Test
    public void getLeaderboardCountNonExistentTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            ObjectNode request = Json.newObject();
            request.put("gameId", 123);
            request.put("leaderboardName", "lbTest");

            assertEquals(0, Json.parse(WebRequest.jsonPost(url + "/view-api/get-leaderboard-count", request.toString())).asInt());
        });
    }

    @Test
    public void getLeaderboardCountTest() {
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

            assertEquals(10, Json.parse(WebRequest.jsonPost(url + "/view-api/get-leaderboard-count", request.toString())).asInt());
        });
    }

    @Test
    public void editLeaderboardTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            LeaderboardModify.createLeaderboard(123, "lbTest", null, null, false);

            ObjectNode request = Json.newObject();
            request.put("gameId", 123);
            request.put("leaderboardName", "lbTest");
            request.put("startTime", 150000);
            request.put("endTime", 3000000);

            assertTrue(Json.parse(WebRequest.jsonPost(url + "/view-api/edit-leaderboard", request.toString())).asBoolean());

            Leaderboard leaderboard = LeaderboardRead.getLeaderboard(123, "lbTest");
            assertNotNull(leaderboard);
            assertEquals(150000, leaderboard.startTime().getTime());
            assertEquals(3000000, leaderboard.endTime().getTime());
        });
    }

    @Test
    public void newLeaderboardTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            ObjectNode request = Json.newObject();
            request.put("gameId", 123);
            request.put("leaderboardName", "lbTest");
            request.put("startTime", -1);
            request.put("endTime", -1);
            request.put("descending", false);

            assertTrue(Json.parse(WebRequest.jsonPost(url + "/view-api/new-leaderboard", request.toString())).asBoolean());

            assertEquals(1, LeaderboardRead.countAllLeaderboards());
            assertNotNull(LeaderboardRead.getLeaderboard(123, "lbTest"));
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

            JsonNode response = Json.parse(WebRequest.jsonPost(url + "/view-api/get-ranked-users", request.toString()));
            assertEquals(5, response.size());

            for (int i = 0; i < 5; i++) {
                assertEquals("u"+(i+2), response.get(i).findValue("userId").asText(""));
                assertEquals(i + 2, response.get(i).findValue("rank").asInt(-1));
            }
        });
    }

    @Test
    public void deleteUserTest() {
        TestServerRunner.runTest((TestBrowser browser) -> {
            LeaderboardModify.createLeaderboard(123, "lbTest", null, null, false);
            Jedis j = jedisPool.getResource();
            try {
                for (int i = 0; i < 10; i++) {
                    j.zadd(RedisLeaderboardProvider.redisKey(123, "lbTest"), i, "u" + i);
                    j.hset(RedisLeaderboardProvider.redisKeyExtra(123, "lbTest"), "u" + i, "data" + i);
                }

                for (int i = 0; i < 3; i++) {
                    ObjectNode deleteRequest = Json.newObject();
                    deleteRequest.put("gameId", 123);
                    deleteRequest.put("leaderboardName", "lbTest");
                    deleteRequest.put("userId", "u" + i);
                    assertTrue(Json.parse(WebRequest.jsonPost(url + "/view-api/delete-user", deleteRequest.toString())).asBoolean());
                }

                assertEquals(7, (long)j.zcard(RedisLeaderboardProvider.redisKey(123, "lbTest")));

            } finally {
                jedisPool.returnResource(j);
            }
        });
    }

    @Test
    public void setUserScoreTest() {
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

                assertTrue(Json.parse(WebRequest.jsonPost(url + "/view-api/set-user-score", request.toString())).asBoolean());

                assertEquals(500.0, j.zscore(RedisLeaderboardProvider.redisKey(123, "lbTest"), "userTest"), 0.01);
                assertEquals("userDataNew", j.hget(RedisLeaderboardProvider.redisKeyExtra(123, "lbTest"), "userTest"));

            } finally {
                jedisPool.returnResource(j);
            }
        });
    }
}
