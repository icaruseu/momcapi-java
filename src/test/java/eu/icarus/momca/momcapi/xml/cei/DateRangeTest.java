package eu.icarus.momca.momcapi.xml.cei;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by daniel on 10.07.2015.
 */
public class DateRangeTest {

    private static final DateRange CEI_DATE_RANGE = new DateRange("14970801", "14970810", "01. - 10. August 1497");

    @Test
    public void testGetFromValue() throws Exception {
        assertEquals(CEI_DATE_RANGE.getFromDateValue(), new DateValue("14970801"));
    }

    @Test
    public void testGetFromValueAsString() throws Exception {
        assertEquals(CEI_DATE_RANGE.getFromDateValue().getValue(), "14970801");
    }

    @Test
    public void testGetToValue() throws Exception {
        assertEquals(CEI_DATE_RANGE.getToDateValue(), new DateValue("14970810"));
    }

    @Test
    public void testGetToValueAsString() throws Exception {
        assertEquals(CEI_DATE_RANGE.getToDateValue().getValue(), "14970810");
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(CEI_DATE_RANGE.isValid());
        assertFalse(new DateRange("970801", "14970810", "01. - 10. August 1497").isValid());
        assertFalse(new DateRange("14970801", "70810", "01. - 10. August 1497").isValid());
    }

    @Test
    public void testIsWrongDateType() throws Exception {
        assertFalse(CEI_DATE_RANGE.couldBeOtherDateType());
        DateRange wrongDateRange = new DateRange("14970801", "14970801", "01. August 1497");
        assertTrue(wrongDateRange.couldBeOtherDateType());
    }

}