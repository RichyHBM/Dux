package utilities;

import static org.junit.Assert.assertTrue;

public class ExceptionTester {
    public static void assertThrowsException(ITest exceptionThrower) {
        try {
            exceptionThrower.test();
            assertTrue("Failed to throw exception", false);
        }catch (Exception e){
            assertTrue(e.getMessage(), true);
        }
    }
}
