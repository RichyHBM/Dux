package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import interfaces.ILeaderboardManager;
import interfaces.ILeaderboardProvider;
import models.Leaderboard;
import models.LeaderboardUser;
import play.cache.CacheApi;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import common.LoggedException;
import common.utilities.StringUtils;

public class LeaderboardAPIv1 extends Controller {
    @Inject
    ILeaderboardManager manager;

    @Inject
    ILeaderboardProvider provider;

    @Inject
    CacheApi cache;

    final int CacheExpirationTime = 60;

    private Leaderboard getCachedLeaderboard(int gameId, String leaderboardName) throws LoggedException {
        String gameLeaderboardName = gameId + "_" + leaderboardName;
        Leaderboard leaderboard = cache.get(gameLeaderboardName);

        if(leaderboard == null) {
            leaderboard = manager.getLeaderboard(gameId, leaderboardName);
            if(leaderboard == null)
                throw new LoggedException("Invalid leaderboard given",
                        String.format("Leaderboard %s for game %s doesnt exist", leaderboardName, gameId));
            cache.set(gameLeaderboardName, leaderboard, CacheExpirationTime);
        }

        return leaderboard;
    }

    private boolean isLeaderboardActive(int gameId, String leaderboardName) throws LoggedException {
        Leaderboard leaderboard = getCachedLeaderboard(gameId, leaderboardName);

        if(leaderboard.startTime() == null || leaderboard.endTime() == null)
            return false;

        Date now = new Date();

        return leaderboard.startTime().before(now) && leaderboard.endTime().after(now);
    }

    public Result getUserCountInLeaderboard() {
        JsonNode json = request().body().asJson();
        if (json == null)
            return badRequest("Game ID and Leaderboard Name required");

        int gameId = json.findPath("gameId").asInt(-1);
        String leaderboardName = json.findPath("leaderboardName").asText("");

        Long count = provider.getUserCountInLeaderboard(gameId, leaderboardName);
        return ok(Json.toJson(count));
    }

    public Result getRankedUsers() {
        JsonNode json = request().body().asJson();
        if (json == null)
            return badRequest("Game ID and Leaderboard data required");

        int gameId = json.findPath("gameId").asInt(-1);
        String leaderboardName = json.findPath("leaderboardName").asText("");
        int startRank = json.findPath("startRank").asInt(-1);
        int count = json.findPath("count").asInt(-1);

        if(gameId == -1 || StringUtils.isEmpty(leaderboardName) || startRank == -1 || count == -1)
            return badRequest("Bad request body");

        boolean descending = getCachedLeaderboard(gameId, leaderboardName).descending();

        List<LeaderboardUser> users = provider.getRankedUsers(gameId, leaderboardName, descending, startRank, count);

        return ok(Json.toJson(users));
    }

    public Result getUser() {
        JsonNode json = request().body().asJson();
        if (json == null)
            return badRequest("Game ID and Leaderboard data required");

        int gameId = json.findPath("gameId").asInt(-1);
        String leaderboardName = json.findPath("leaderboardName").asText("");
        String userId = json.findPath("userId").asText("");

        if(gameId == -1 || StringUtils.isEmpty(leaderboardName) || StringUtils.isEmpty(userId))
            return badRequest("Bad request body");

        boolean descending = getCachedLeaderboard(gameId, leaderboardName).descending();

        LeaderboardUser user = provider.getUser(gameId, leaderboardName, descending, userId);

        return ok(Json.toJson(user));
    }

    public Result getRankForUser() {
        JsonNode json = request().body().asJson();
        if (json == null)
            return badRequest("Game ID and Leaderboard data required");

        int gameId = json.findPath("gameId").asInt(-1);
        String leaderboardName = json.findPath("leaderboardName").asText("");
        String userId = json.findPath("userId").asText("");

        if(gameId == -1 || StringUtils.isEmpty(leaderboardName) || StringUtils.isEmpty(userId))
            return badRequest("Bad request body");

        boolean descending = getCachedLeaderboard(gameId, leaderboardName).descending();

        Long userRank = provider.getRankForUser(gameId, leaderboardName, descending, userId);

        return ok(Json.toJson(userRank));
    }

    public Result getScoreForUser() {
        JsonNode json = request().body().asJson();
        if (json == null)
            return badRequest("Game ID and Leaderboard data required");

        int gameId = json.findPath("gameId").asInt(-1);
        String leaderboardName = json.findPath("leaderboardName").asText("");
        String userId = json.findPath("userId").asText("");

        if(gameId == -1 || StringUtils.isEmpty(leaderboardName) || StringUtils.isEmpty(userId))
            return badRequest("Bad request body");

        Double userScore = provider.getScoreForUser(gameId, leaderboardName, userId);

        return ok(Json.toJson(userScore));
    }

    public Result setScoreForUser() throws LoggedException {
        JsonNode json = request().body().asJson();
        if (json == null)
            return badRequest("Game ID and Leaderboard data required");

        int gameId = json.findPath("gameId").asInt(-1);
        String leaderboardName = json.findPath("leaderboardName").asText("");
        String userId = json.findPath("userId").asText();
        String userExtraData = json.findPath("userExtraData").asText("");
        double newScore = json.findPath("newUserScore").asDouble(-1.0);

        if(gameId == -1 || StringUtils.isEmpty(leaderboardName) || StringUtils.isEmpty(userId) || newScore < 0 || StringUtils.isEmpty(userExtraData))
            return badRequest("Bad request body");

        if(!isLeaderboardActive(gameId, leaderboardName))
            throw new LoggedException("This leaderboard is inactive", null);

        boolean success = provider.setScoreForUser(gameId, leaderboardName, userId, newScore, userExtraData);

        return ok(Json.toJson(success));
    }

    public Result incrementScoreForUser() throws LoggedException {
        JsonNode json = request().body().asJson();
        if (json == null)
            return badRequest("Game ID and Leaderboard data required");

        int gameId = json.findPath("gameId").asInt(-1);
        String leaderboardName = json.findPath("leaderboardName").asText("");
        String userId = json.findPath("userId").asText();
        String userExtraData = json.findPath("userExtraData").asText("");
        double increment = json.findPath("increment").asDouble(-1.0);

        if(gameId == -1 || StringUtils.isEmpty(leaderboardName) || StringUtils.isEmpty(userId) || increment < 0 || StringUtils.isEmpty(userExtraData))
            return badRequest("Bad request body");

        if(!isLeaderboardActive(gameId, leaderboardName))
            throw new LoggedException("This leaderboard is inactive", null);

        boolean success = provider.incrementScoreForUser(gameId, leaderboardName, userId, increment, userExtraData);

        return ok(Json.toJson(success));
    }
}
