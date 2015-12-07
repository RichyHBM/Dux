package auth.controllers;

import auth.interfaces.IAuthCache;
import com.fasterxml.jackson.databind.JsonNode;
import javax.inject.Inject;
import play.*;
import play.mvc.*;

import views.html.*;
import common.utilities.StringUtils;

public class AuthAPI extends Controller {

    @Inject
    IAuthCache authCache;

    public Result signin() {
        JsonNode json = request().body().asJson();
        if (json == null)
            return badRequest("Email and Password required");

        String email = json.findPath("email").asText();
        String password = json.findPath("password").asText();

        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(password))
            return badRequest("Bad Email and Password");

        //Do log in


        return redirect("/");
    }

    public Result logout() {
        authCache.removeUserFromCache((String) ctx().args.get("auth-session"));
        response().discardCookie("session");
        return redirect("/");
    }

}
