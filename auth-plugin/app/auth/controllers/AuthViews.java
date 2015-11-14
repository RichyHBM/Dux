package auth.controllers;

import auth.models.AuthenticatedUser;
import auth.models.BasicAuthViewResponse;
import com.google.inject.Inject;
import play.*;
import play.mvc.*;


public class AuthViews extends Controller {

    public Result signin() {
        BasicAuthViewResponse response = new BasicAuthViewResponse("Authentication", (AuthenticatedUser)ctx().args.get("auth-user"));
        return ok(auth.views.html.signin.render(response));
    }

}
