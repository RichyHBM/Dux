package auth;

import auth.interfaces.IAuthCache;
import auth.models.AuthenticatedUser;
import com.google.inject.Inject;
import play.Configuration;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Result;
import play.mvc.Http;

import java.lang.Throwable;

public class AuthenticatedAction extends Action<IsAuthenticated> {

    @Inject
    IAuthCache authCache;

    public F.Promise<Result> call(Http.Context ctx) throws Throwable {
        switch(configuration.value()) {
            case None:
                return delegate.call(ctx);

            case Session:
                // Verify Session
                Http.Cookie cookie = ctx.request().cookie("session");
                if(cookie != null) {
                    AuthenticatedUser user = authCache.getUserFromCache(cookie.value());
                    if(user != null) {
                        ctx.args.put("auth-session", cookie.value());
                        ctx.args.put("auth-user", user);
                        return delegate.call(ctx);
                    }
                }
                break;

            case ApiKey:
                // Verify API Key
                break;

        }
        return F.Promise.pure(unauthorized("You are not authorised to view this page"));
    }
}
