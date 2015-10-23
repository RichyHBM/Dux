package common.utilities;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilsTest {
    @Test
    public void isEmptyTest(){
        assertTrue(StringUtils.isEmpty(""));
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(new String()));

        assertFalse(StringUtils.isEmpty(" "));
        assertFalse(StringUtils.isEmpty("String"));
        assertFalse(StringUtils.isEmpty(Integer.toString(1)));
    }

    @Test
    public void isNotEmptyTest(){
        assertFalse(StringUtils.isNotEmpty(""));
        assertFalse(StringUtils.isNotEmpty(null));
        assertFalse(StringUtils.isNotEmpty(new String()));

        assertTrue(StringUtils.isNotEmpty(" "));
        assertTrue(StringUtils.isNotEmpty("String"));
        assertTrue(StringUtils.isNotEmpty(Integer.toString(1)));
    }
}
