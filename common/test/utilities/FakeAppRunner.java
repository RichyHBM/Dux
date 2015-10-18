package utilities;

import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import java.util.HashMap;

import static play.test.Helpers.*;
import static org.junit.Assert.*;

public class FakeAppRunner {
    public static void runTest(ITest test) {
        HashMap<String, Object> config = new HashMap<String, Object>();

        running(fakeApplication(config), () -> {
            try{
                test.test();
            }catch(Exception e){
                assertTrue(e.getMessage(), false);
            }
        });
    }

    public static void runTest(ITest test, HashMap<String, Object> config) {
        running(fakeApplication(config), () -> {
            try{
                test.test();
            }catch(Exception e){
                assertTrue(e.getMessage(), false);
            }
        });
    }
}
