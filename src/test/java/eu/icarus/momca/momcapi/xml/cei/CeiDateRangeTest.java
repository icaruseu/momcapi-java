package eu.icarus.momca.momcapi.xml.cei;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by daniel on 10.07.2015.
 */
public class CeiDateRangeTest {

    private static final CeiDateRange CEI_DATE_RANGE = new CeiDateRange("14970801", "14970810", "01. - 10. August 1497");

    @Test
    public void testGetFromValue() throws Exception {
        assertEquals(CEI_DATE_RANGE.getNumericFromDate(), new NumericDate("14970801"));
    }

    @Test
    public void testGetFromValueAsString() throws Exception {
        assertEquals(CEI_DATE_RANGE.getNumericFromDate().getValue(), "14970801");
    }

    @Test
    public void testGetToValue() throws Exception {
        assertEquals(CEI_DATE_RANGE.getNumericToDate(), new NumericDate("14970810"));
    }

    @Test
    public void testGetToValueAsString() throws Exception {
        assertEquals(CEI_DATE_RANGE.getNumericToDate().getValue(), "14970810");
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(CEI_DATE_RANGE.isValid());
        assertFalse(new CeiDateRange("970801", "14970810", "01. - 10. August 1497").isValid());
        assertFalse(new CeiDateRange("14970801", "70810", "01. - 10. August 1497").isValid());
    }

    @Test
    public void testIsWrongDateType() throws Exception {
        assertFalse(CEI_DATE_RANGE.couldBeOtherDateType());
        CeiDateRange wrongDateRange = new CeiDateRange("14970801", "14970801", "01. August 1497");
        assertTrue(wrongDateRange.couldBeOtherDateType());
    }

}