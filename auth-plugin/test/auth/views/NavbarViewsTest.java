package auth.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import auth.models.AuthenticatedUser;
import auth.models.BasicAuthViewResponse;
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
public class NavbarViewsTest {

    @Test
    public void navbarViewNullUserTest() {
        BasicAuthViewResponse response = new BasicAuthViewResponse("Test", null);

        Content html = auth.views.html.navbar.render(response, new Html(""));

        assertTrue(contentAsString(html).contains("Sign In"));
    }

    @Test
    public void navbarViewWithUserTest() {
        BasicAuthViewResponse response = new BasicAuthViewResponse("Test", new AuthenticatedUser(1, "test", "test@test"));

        Content html = auth.views.html.navbar.render(response, new Html(""));

        assertTrue(contentAsString(html).contains("test"));
    }

}
