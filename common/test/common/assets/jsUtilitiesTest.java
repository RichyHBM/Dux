package common.assets;

import org.junit.Test;
import utilities.JavaScriptEngine;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class jsUtilitiesTest {
    @Test
    public void padNumberToXDecimalsTest() throws ScriptException, IOException, NoSuchMethodException {
        JavaScriptEngine jsEngine = new JavaScriptEngine();
        jsEngine.loadJsFile("./app/assets/js/utilities.js");

        assertEquals("0001", (String)jsEngine.callFunction("padNumberToXDecimals", "1", 4));
        assertEquals("0012", (String)jsEngine.callFunction("padNumberToXDecimals", "12", 4));
        assertEquals("0123", (String)jsEngine.callFunction("padNumberToXDecimals", "123", 4));
    }

    @Test
    public void padNumberToXDecimalsShouldntDoAnythingTest() throws ScriptException, IOException, NoSuchMethodException {
        JavaScriptEngine jsEngine = new JavaScriptEngine();
        jsEngine.loadJsFile("./app/assets/js/utilities.js");

        assertEquals("1234", (String)jsEngine.callFunction("padNumberToXDecimals", "1234", 4));
        assertEquals("12345", (String)jsEngine.callFunction("padNumberToXDecimals", "12345", 4));
        assertEquals("1", (String)jsEngine.callFunction("padNumberToXDecimals", "1", 0));
        assertEquals("1", (String)jsEngine.callFunction("padNumberToXDecimals", "1", -1));
    }

    @Test
    public void dateToYYYYMMDD_HHMMSSTest() throws ScriptException, IOException, NoSuchMethodException {
        JavaScriptEngine jsEngine = new JavaScriptEngine();
        jsEngine.loadJsFile("./app/assets/js/utilities.js");
        jsEngine.loadJs("function dateToFormatFromUnix(unixTS) { return dateToYYYYMMDD_HHMMSS( new Date(unixTS) ); }");
        Calendar calendar = Calendar.getInstance();

        calendar.set(2015, Calendar.NOVEMBER, 1, 12, 0, 0);
        assertEquals("2015-11-01 12:00:00", (String) jsEngine.callFunction("dateToFormatFromUnix", calendar.getTime().getTime()));

        calendar.set(1975, Calendar.JANUARY, 1, 3, 0, 0);
        assertEquals("1975-01-01 03:00:00", (String) jsEngine.callFunction("dateToFormatFromUnix", calendar.getTime().getTime()));

        calendar.set(9999, Calendar.DECEMBER, 31, 23, 59, 59);
        assertEquals("9999-12-31 23:59:59", (String) jsEngine.callFunction("dateToFormatFromUnix", calendar.getTime().getTime()));

    }

    @Test
    public void regexYYYYMMDD_HHMMSSTest() throws ScriptException, IOException, NoSuchMethodException {
        JavaScriptEngine jsEngine = new JavaScriptEngine();
        jsEngine.loadJsFile("./app/assets/js/utilities.js");
        jsEngine.loadJs("function checkRegex(str) { return date_YYYYMMDD_HHMMSS_ValidationRegex.test(str); }");

        assertTrue((boolean) jsEngine.callFunction("checkRegex", "2015-11-01 12:00:00"));
        assertFalse((boolean) jsEngine.callFunction("checkRegex", "2015-1-01 12:00:00"));
        assertFalse((boolean) jsEngine.callFunction("checkRegex", "2015-13-01 12:00:00"));
        assertFalse((boolean) jsEngine.callFunction("checkRegex", "2015-11-1 12:00:00"));
        assertFalse((boolean) jsEngine.callFunction("checkRegex", "2015-11-01  12:00:00"));
        assertFalse((boolean) jsEngine.callFunction("checkRegex", "2015-11-01 12:0:0"));
        assertFalse((boolean) jsEngine.callFunction("checkRegex", "215-11-01 12:0:0"));
    }
}
