package eu.icarus.momca.momcapi.model.xml.cei;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by daniel on 10.07.2015.
 */
public class DateExactTest {

    private static final DateExact CEI_DATE = new DateExact("12310801", "01. August 1231");

    @Test
    public void testConstructor() throws Exception {
        assertEquals(CEI_DATE.toXML(), "<cei:date xmlns:cei=\"http://www.monasterium.net/NS/cei\" value=\"12310801\">01. August 1231</cei:date>");
    }

    @Test
    public void testGetDateValue() throws Exception {
        assertEquals(CEI_DATE.getDateValue(), new DateValue("12310801"));
    }

    @Test
    public void testGetDateValueAsString() throws Exception {
        assertEquals(CEI_DATE.getDateValue().getValue(), "12310801");
    }

    @Test
    public void testGetLiteralDate() throws Exception {
        assertEquals(CEI_DATE.getLiteralDate(), "01. August 1231");
    }

    @Test
    public void testIsUndated() throws Exception {
        assertTrue(new DateExact("99990812", "12th August").isUndated());
        assertFalse(new DateExact("12180812", "12th August 1218").isUndated());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(CEI_DATE.isValid());
        assertFalse(new DateExact("310801", "01. August 1231").isValid());
    }

    @Test
    public void testIsWrongDateType() throws Exception {
        assertTrue(new DateExact("12310899", "August 1231").couldBeOtherDateType());
        assertTrue(new DateExact("9319999", "1231").couldBeOtherDateType());
    }
}