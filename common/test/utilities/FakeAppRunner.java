package utilities;

import org.junit.*;

import org.openqa.selenium.remote.DesiredCapabilities;
import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static play.test.Helpers.*;
import static org.junit.Assert.*;
import org.openqa.selenium.phantomjs.*;

public class FakeAppRunner {
    public static void runTest(ITest test) {
        HashMap<String, Object> config = new HashMap<String, Object>();
        runTest(test, config);
    }

    public static void runTest(ITest test, HashMap<String, Object> config) {
        running(fakeApplication(config), () -> {
            try {
                test.test();
            } catch (Exception e) {
                assertTrue(e.getMessage(), false);
            }
        });
    }

    public static void runBrowserTest(ITestBrowser test) {
        //Turn off PhantomJS Info logs
        Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.WARNING);

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        String[] phantomArgs = new String[] {
                "--webdriver-loglevel=WARNING"
        };
        desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);
        PhantomJSDriver phantomDriver = new PhantomJSDriver(desiredCapabilities);

        running(testServer(3333,fakeApplication(inMemoryDatabase())), phantomDriver, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                try {
                    test.test(browser);
                } catch (Exception e) {
                    assertTrue(e.getMessage(), false);
                }
            }
        });
    }
}
