package eu.icarus.momca.momcapi.xml.cei;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by daniel on 10.07.2015.
 */
public class DateValueTest {

    @Test
    public void testEquals() throws Exception {
        assertTrue(new DateValue("12471027").equals(new DateValue("12471027")));
    }

    @Test
    public void testGetValue() throws Exception {
        assertEquals(new DateValue("12471027").getValue(), "12471027");
    }

    @Test
    public void testIsValid() throws Exception {
        assertFalse(new DateValue("09471027").isValid());
        assertTrue(new DateValue("9471027").isValid());
        assertTrue(new DateValue("12471027").isValid());
    }

    @Test
    public void testValidateNumericDate() throws Exception {
        assertFalse(DateValue.validateNumericDate("09471027"));
        assertTrue(DateValue.validateNumericDate("9471027"));
        assertTrue(DateValue.validateNumericDate("12471027"));
    }

}