package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import auth.models.BasicAuthViewResponse;
import play.*;
import play.mvc.*;
import play.libs.Json;
import interfaces.ILeaderboardManager;
import views.html.*;
import javax.inject.Inject;

@auth.IsAuthenticated
public class LeaderboardViews extends Controller {
    @Inject
    ILeaderboardManager manager;

    public Result index() {
        BasicAuthViewResponse response = new BasicAuthViewResponse("Leaderboards", (auth.models.AuthenticatedUser)ctx().args.get("auth-user"));
        ObjectNode viewData = Json.newObject();
        viewData.put("TotalLeaderboards", manager.countAllLeaderboards() + 10);
        viewData.put("ActiveLeaderboards", manager.countActiveLeaderboards() + 5);
        return ok(index.render(viewData, response));
    }

    public Result game(Long gameId) {
        return ok(gameId.toString());
    }

    public Result leaderboard(Long gameId, String leaderboardName) {
        return ok(gameId.toString() + " : " + leaderboardName);
    }

}
