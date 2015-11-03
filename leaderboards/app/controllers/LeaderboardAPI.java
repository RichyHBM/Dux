package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import interfaces.ILeaderboardManager;
import interfaces.ILeaderboardProvider;
import models.Leaderboard;
import models.LeaderboardUser;
import play.Logger;
import play.cache.CacheApi;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import common.utilities.StringUtils;

public class LeaderboardAPI extends Controller {
    @Inject
    ILeaderboardManager manager;

    @Inject
    ILeaderboardProvider provider;

    @Inject
    CacheApi cache;

    final int CacheExpirationTime = 60 * 2;

    private Leaderboard getCachedLeaderboard(int gameId, String leaderboardName) {
        String gameLeaderboardName = gameId + "_" + leaderboardName;
        Leaderboard leaderboard = cache.get(gameLeaderboardName);

        if(leaderboard == null) {
            leaderboard = manager.getLeaderboard(gameId, leaderboardName);
            cache.set(gameLeaderboardName, leaderboard, CacheExpirationTime);
        }
        
        return leaderboard;
    }

    public Result getLeaderboardsForGame() {
        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Game ID required");

        int gameId = json.findPath("gameId").asInt(-1);

        List<Leaderboard> leaderboards = manager.getLeaderboardsForGame(gameId);

        return ok(Json.toJson(leaderboards));
    }

    public Result getLeaderboardCount() {
        JsonNode json = request().body().asJson();
        if (json == null)
            return badRequest("Game ID and Leaderboard Name required");

        int gameId = json.findPath("gameId").asInt(-1);
        String leaderboardName = json.findPath("leaderboardName").asText("");
        Long count = provider.getUserCountInLeaderboard(gameId, leaderboardName);
        return ok(count.toString());
    }

    public Result editLeaderboard() {
        JsonNode json = request().body().asJson();
        if (json == null)
            return badRequest("Game ID, Leaderboard Name, and data required");

        int gameId = json.findPath("gameId").asInt(-1);
        String leaderboardName = json.findPath("leaderboardName").asText("");

        long startTime = json.findPath("startTime").asLong(-1);
        long endTime = json.findPath("endTime").asLong(-1);

        Date startDate = startTime == -1 ? null : new Date(startTime);
        Date endDate = endTime == -1 ? null : new Date(endTime);

        Boolean success = manager.editLeaderboard(gameId, leaderboardName, startDate, endDate);

        return ok(Json.toJson(success));
    }

    public Result newLeaderboard() {
        JsonNode json = request().body().asJson();
        if (json == null)
            return badRequest("Game ID and Leaderboard data required");

        int gameId = json.findPath("gameId").asInt(-1);
        String leaderboardName = json.findPath("leaderboardName").asText("");

        if(gameId == -1 || StringUtils.isEmpty(leaderboardName))
            return badRequest("Bad Game ID or Leaderboard name");

        long startTime = json.findPath("startTime").asLong(-1);
        long endTime = json.findPath("endTime").asLong(-1);
        boolean descending = json.findPath("descending").asBoolean(false);

        Date startDate = startTime == -1 ? null : new Date(startTime);
        Date endDate = endTime == -1 ? null : new Date(endTime);

        Boolean success = manager.createLeaderboard(gameId, leaderboardName, startDate, endDate, descending);

        return ok(Json.toJson(success));
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
}
