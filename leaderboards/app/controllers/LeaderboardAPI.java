package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import interfaces.ILeaderboardManager;
import interfaces.ILeaderboardProvider;
import models.Leaderboard;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LeaderboardAPI extends Controller {
    @Inject
    ILeaderboardManager manager;

    @Inject
    ILeaderboardProvider provider;

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
}
