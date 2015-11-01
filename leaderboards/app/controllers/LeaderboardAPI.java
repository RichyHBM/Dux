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
import java.util.List;

public class LeaderboardAPI extends Controller {
    @Inject
    ILeaderboardManager manager;

    @Inject
    ILeaderboardProvider provider;

    public Result getLeaderboardsForGame() {
        JsonNode json = request().body().asJson();

        if(json == null)
            return badRequest("Game ID required");

        int gameId = json.findPath("gameId").asInt(-1);

        List<Leaderboard> leaderboards = manager.getLeaderboardsForGame(gameId);

        return ok(Json.toJson(leaderboards));
    }

    public Result getLeaderboardCount() {
        JsonNode json = request().body().asJson();
        if(json == null)
            return badRequest("Game ID and Leaderboard Name required");

        int gameId = json.findPath("gameId").asInt(-1);
        String leaderboardName = json.findPath("leaderboardName").asText("");
        Long count = provider.getUserCountInLeaderboard(gameId, leaderboardName);
        return ok(count.toString());
    }
}
