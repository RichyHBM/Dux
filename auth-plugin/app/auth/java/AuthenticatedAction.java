package auth.java;

import auth.AuthenticationType;
import auth.Authenticator;
import auth.interfaces.IAuthCache;
import auth.models.AuthenticatedUser;
import auth.models.SessionUser;
import auth.scala.DefinedStrings;
import common.utilities.StringUtils;
import play.Configuration;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;

public class AuthenticatedAction extends Action<IsAuthenticated> {

    @Inject
    IAuthCache authCache;

    @Inject
    Configuration config;

    public F.Promise<Result> call(Http.Context ctx) throws Throwable {
        AuthenticationType authType = configuration.authentication();

        if(authType == AuthenticationType.None) {
            return delegate.call(ctx);
        }

        String authUrl = config.getString(DefinedStrings.duxConfigAuthUrl(), DefinedStrings.defaultAuthUrl());
        String privilege = configuration.priviledge();
        if(StringUtils.isEmpty(privilege))
            privilege = config.getString(DefinedStrings.duxDefaultAuthPrivilege(), "");

        if(authType == AuthenticationType.Session ||
            authType == AuthenticationType.Either) {
            Http.Cookie cookie = ctx.request().cookie(DefinedStrings.sessionCookieName());
            if(cookie != null) {
                SessionUser sessionUser = Authenticator.checkSession(authUrl, cookie.value(), privilege, authCache);
                if(sessionUser != null) {
                    ctx.args.put(DefinedStrings.sessionCookieName(), sessionUser.sessionKey());
                    return delegate.call(ctx);
                }
            }
        }

        if(authType == AuthenticationType.ApiKey ||
            authType == AuthenticationType.Either) {
            AuthenticatedUser authenticatedUser = Authenticator.checkApiKey(authUrl, ctx.request().getHeader(DefinedStrings.duxApiHeader()), privilege);
            if(authenticatedUser != null) {
                return delegate.call(ctx);
            }
        }

        return F.Promise.pure(unauthorized("You are not authorised to view this page"));
    }
}
