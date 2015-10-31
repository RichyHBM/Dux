package common.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.models.BasicViewResponse;
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
public class MainViewTest {

    @Test
    public void mainViewTest() {
        BasicViewResponse response = new BasicViewResponse("Test");

        Content html = common.views.html.main.render(response, new Html(""));

        assertTrue(contentAsString(html).contains("Test"));
    }
}
