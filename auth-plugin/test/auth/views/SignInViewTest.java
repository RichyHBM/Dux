package auth.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import auth.models.AuthenticatedUser;
import common.models.BasicViewResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;
import play.twirl.api.Content;
import play.twirl.api.Html;

import static play.test.Helpers.*;
import static org.junit.Assert.*;


/**
 * These tests make sure all the views compile as expected
 */
public class SignInViewTest {
    @Test
    public void signinViewNullUserTest() {
        BasicViewResponse response = new BasicViewResponse("Sign In");

        Content html = auth.views.html.signin.render(response);

        assertTrue(contentAsString(html).contains("Sign In"));
    }
}
