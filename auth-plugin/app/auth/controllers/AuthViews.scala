package auth.controllers

import auth.models.AuthenticatedUser
import common.models._
import play.api._
import play.api.mvc._


class AuthViews extends Controller {

    def signin() = Action {
        val response = new BasicViewResponse("Sign In")
        Ok(auth.views.html.signin.render(response))
    }

}
