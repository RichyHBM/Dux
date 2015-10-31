package views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import auth.models.AuthenticatedUser;
import auth.models.BasicAuthViewResponse;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
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
public class IndexViewTest extends WithApplication {

    @Test
    public void indexViewNullUserTest() {
        BasicAuthViewResponse response = new BasicAuthViewResponse("Leaderboards", null);
        Content html = views.html.index.render(Json.newObject(), response);

        assertTrue(contentAsString(html).contains(response.title()));
        assertTrue(contentAsString(html).contains("Sign In"));
    }

    @Test
    public void indexViewWithUserTest() {
        BasicAuthViewResponse response = new BasicAuthViewResponse("Leaderboards", new AuthenticatedUser(1, "test", "test@test"));
        Content html = views.html.index.render(Json.newObject(), response);

        assertTrue(contentAsString(html).contains(response.title()));
        assertTrue(contentAsString(html).contains("test"));
    }

}
