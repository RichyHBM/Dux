import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static play.test.Helpers.*;
import static org.junit.Assert.*;
import utilities.FakeAppRunner;

import static org.fluentlenium.core.filter.FilterConstructor.*;

public class IntegrationTest {

    /**
     * add your integration test here
     * in this example we just check if the welcome page is being shown
     */
    @Test
    public void test() {
        FakeAppRunner.runBrowserTest( (TestBrowser browser) -> {
            browser.goTo("http://localhost:3333");
            assertTrue(browser.title().contains("Leaderboards"));
        });
    }

}
