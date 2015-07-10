package eu.icarus.momca.momcapi.resource.cei;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by daniel on 10.07.2015.
 */
public class CeiDateRangeTest {

    private static final CeiDateRange CEI_DATE_RANGE = new CeiDateRange("14970801", "14970810", "01. - 10. August 1497");

    @Test
    public void testGetFromValue() throws Exception {
        assertEquals(CEI_DATE_RANGE.getFromValue(), new DateValue("14970801"));
    }

    @Test
    public void testGetFromValueAsString() throws Exception {
        assertEquals(CEI_DATE_RANGE.getFromValueAsString(), "14970801");
    }

    @Test
    public void testGetToValue() throws Exception {
        assertEquals(CEI_DATE_RANGE.getToValue(), new DateValue("14970810"));
    }

    @Test
    public void testGetToValueAsString() throws Exception {
        assertEquals(CEI_DATE_RANGE.getToValueAsString(), "14970810");
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(CEI_DATE_RANGE.isValid());
        assertFalse(new CeiDateRange("970801", "14970810", "01. - 10. August 1497").isValid());
        assertFalse(new CeiDateRange("14970801", "70810", "01. - 10. August 1497").isValid());
    }
}