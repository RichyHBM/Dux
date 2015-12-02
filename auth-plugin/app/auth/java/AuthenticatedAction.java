package auth.java;

import auth.interfaces.IAuthCache;
import auth.AuthenticationType;
import auth.models.*;
import com.google.inject.Inject;
import play.Configuration;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Result;
import play.mvc.Http;
import auth.Authenticator;

import java.lang.Throwable;

public class AuthenticatedAction extends Action<IsAuthenticated> {

    @Inject
    IAuthCache authCache;

    public F.Promise<Result> call(Http.Context ctx) throws Throwable {
        AuthenticationType authType = configuration.value();

        if(authType == AuthenticationType.None) {
            return delegate.call(ctx);
        }

        if(authType == AuthenticationType.Session ||
            authType == AuthenticationType.Either) {
            Http.Cookie cookie = ctx.request().cookie("session");
            if(cookie != null) {
                SessionUser sessionUser = Authenticator.checkSession(cookie.value(), authCache);
                if(sessionUser != null) {
                    ctx.args.put("auth-session", sessionUser.sessionKey());
                    ctx.args.put("auth-user", sessionUser.user());
                    return delegate.call(ctx);
                }
            }
        }

        if(authType == AuthenticationType.ApiKey ||
            authType == AuthenticationType.Either) {

        }

        return F.Promise.pure(unauthorized("You are not authorised to view this page"));
    }
}
