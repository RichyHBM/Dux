package auth.controllers;

import auth.Authenticator;
import auth.interfaces.IAuthCache;
import auth.models.SessionUser;
import auth.scala.DefinedStrings;
import com.fasterxml.jackson.databind.JsonNode;
import common.utilities.StringUtils;
import play.Configuration;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;

public class AuthAPI extends Controller {

    @Inject
    IAuthCache authCache;

    @Inject
    Configuration config;

    public Result signin() {
        JsonNode json = request().body().asJson();
        if (json == null)
            return badRequest("Email and Password required");

        String email = json.findPath("email").asText();
        String password = json.findPath("password").asText();

        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(password))
            return badRequest("Bad Email and Password");

        //Do log in
        String authUrl = config.getString(DefinedStrings.duxConfigAuthUrl(), DefinedStrings.defaultAuthUrl());
        String appPriviledge = config.getString(DefinedStrings.duxDefaultAuthPrivilege(), "");

        SessionUser sessionUser = Authenticator.logIn(authUrl, email, password, appPriviledge, authCache);
        if(sessionUser == null)
            return badRequest("Log in failed");

        response().setCookie(DefinedStrings.sessionCookieName(), sessionUser.sessionKey());
        response().setHeader("REDIRECT", "/");
        return ok();
    }

    public Result logout() {
        String authUrl = config.getString(DefinedStrings.duxConfigAuthUrl(), DefinedStrings.defaultAuthUrl());
        Http.Cookie cookie = ctx().request().cookie(DefinedStrings.sessionCookieName());
        if(cookie != null) {
            String session = cookie.value();
            Authenticator.logOut(authUrl, session);
            authCache.removeUserFromCache(session);
            response().discardCookie(DefinedStrings.sessionCookieName());
        }
        return redirect("/");
    }

}
