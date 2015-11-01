package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import auth.models.BasicAuthViewResponse;
import auth.models.AuthenticatedUser;
import play.*;
import play.mvc.*;
import play.libs.Json;
import interfaces.ILeaderboardManager;
import views.html.*;
import javax.inject.Inject;


public class LeaderboardViews extends Controller {
    @Inject
    ILeaderboardManager manager;

    public Result index() {
        BasicAuthViewResponse response = new BasicAuthViewResponse("Leaderboards", (AuthenticatedUser)ctx().args.get("auth-user"));
        ObjectNode viewData = Json.newObject();
        viewData.put("TotalLeaderboards", manager.countAllLeaderboards());
        viewData.put("ActiveLeaderboards", manager.countActiveLeaderboards());
        return ok(index.render(viewData, response));
    }

    public Result game(Integer gameId) {
        BasicAuthViewResponse response = new BasicAuthViewResponse("Leaderboards", (AuthenticatedUser)ctx().args.get("auth-user"));
        ObjectNode viewData = Json.newObject();
        viewData.put("GameId", gameId);
        viewData.put("ActiveLeaderboards", manager.countActiveLeaderboardsForGame(gameId));
        viewData.put("TotalLeaderboards", manager.countLeaderboardsForGame(gameId));
        return ok(game.render(viewData, response));
    }

    public Result leaderboard(Integer gameId, String leaderboardName) {
        return ok(gameId.toString() + " : " + leaderboardName);
    }

}
