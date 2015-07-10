package eu.icarus.momca.momcapi.resource.cei;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by daniel on 10.07.2015.
 */
public class CeiDateTest {

    private static final CeiDate CEI_DATE = new CeiDate("12310801", "01. August 1231");

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
        assertEquals(CEI_DATE.getDateValueAsString(), "12310801");
    }

    @Test
    public void testGetLiteralDate() throws Exception {
        assertEquals(CEI_DATE.getLiteralDate(), "01. August 1231");
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(CEI_DATE.isValid());
        assertFalse(new CeiDate("310801", "01. August 1231").isValid());
    }

    @Test
    public void testIsWrongDateType() throws Exception {
        assertTrue(new CeiDate("12310899", "August 1231").isWrongDateType());
        assertTrue(new CeiDate("9319999", "1231").isWrongDateType());
    }

}